package edu.arizona.biosemantics.etcsite.server.upload;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.JavaZipper;
import edu.arizona.biosemantics.etcsite.server.process.file.ContentValidatorProvider;
import edu.arizona.biosemantics.etcsite.server.process.file.IContentValidator;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.upload.Uploader.UploadResult;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;
import gwtupload.server.UploadAction;
import gwtupload.shared.UConsts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

public class UploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	private Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	private	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

	private AuthenticationService authenticationService;
	private XmlNamespaceManager xmlNamespaceManager;
	private ContentValidatorProvider contentValidatorProvider;
	
	@Inject
	public UploadServlet(AuthenticationService authenticationService, XmlNamespaceManager xmlNamespaceManager,
			ContentValidatorProvider contentValidatorProvider) {
		this.authenticationService = authenticationService;
		this.xmlNamespaceManager = xmlNamespaceManager;
		this.contentValidatorProvider = contentValidatorProvider;
	}
	
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) {
		receivedContentTypes.clear();
		receivedFiles.clear();
		
		String response = "";
		
		int userID = Integer.parseInt(request.getParameter("userID"));
		String sessionID = request.getParameter("sessionID");
		String target = request.getParameter("target");
		String fileType = request.getParameter("fileType");
		FileTypeEnum fileTypeEnum = FileTypeEnum.getEnum(fileType);
		
		AuthenticationResult authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(userID, sessionID));
		
		if(authenticationResult.getResult()) {
			Uploader uploader = null;
			
			switch(fileTypeEnum) {
			case TAXON_DESCRIPTION:
				uploader = new TaxonDescriptionUploader(xmlNamespaceManager, contentValidatorProvider);
				break;
			case MARKED_UP_TAXON_DESCRIPTION:
				uploader = new MarkedUpTaxonDescriptionUploader(xmlNamespaceManager, contentValidatorProvider);
				break;
			case MATRIX:
				uploader = new MatrixUploader();
				break;
			case OWL_ONTOLOGY:
				uploader = new OWLOntologyUploader();
				break;
			case MATRIX_GENERATION_SERIALIZED_MODEL:
			case CLEANTAX:
				uploader = new CleanTaxUploader();
				break;
			case PLAIN_TEXT:
				uploader = new TextFileUploader();
				break;
			case DIRECTORY:
			default:
				break;
			}
			if(uploader != null) {
				List<UploadResult> results = uploader.upload(authenticationResult.getUser(), sessionFiles, target, fileTypeEnum);
				
				for(UploadResult result : results) {
					receivedFiles.put(result.getFileItem().getFieldName(), result.getFile());
					receivedContentTypes.put(result.getFileItem().getFieldName(), result.getFileItem().getContentType());
				}
				
				response = createResponse(results);
			} else {
				response = "Unknown file type";
			}
		}
		
		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);
		return response;
	}
	
	private String createResponse(List<UploadResult> uploadResults) {
		String result = "";
		
		List<UploadResult> notAdded = new LinkedList<UploadResult>();
		List<UploadResult> fileExisted = new LinkedList<UploadResult>();
		List<UploadResult> invalidFormat = new LinkedList<UploadResult>();
		List<UploadResult> invalidEncoding = new LinkedList<UploadResult>();
		List<UploadResult> writeFailed = new LinkedList<UploadResult>();
		List<UploadResult> directoryNotAllowedInZip = new LinkedList<UploadResult>();
		
		for(UploadResult uploadResult : uploadResults) {
			receivedFiles.put(uploadResult.getFileItem().getFieldName(), uploadResult.getFile());
			receivedContentTypes.put(uploadResult.getFileItem().getFieldName(), uploadResult.getFileItem().getContentType());
			
			evaluate(uploadResult, notAdded, fileExisted, invalidFormat, invalidEncoding, writeFailed, directoryNotAllowedInZip);
		}
		
		if(!notAdded.isEmpty()) {
			if(notAdded.size() == 1)
				result += notAdded.size() + " file was not added.";
			if(notAdded.size() > 1)
				result += notAdded.size() + " files were not added.";
			
			String directoryNotAllowedInZipFiles = " "; //Do not remove this space - used in client side processing of server response
			String writeFailedFiles = " "; //Do not remove this space - used in client side processing of server response
			String existingFiles = " "; //Do not remove this space - used in client side processing of server response
			String invalidFormatFiles = " ";
			String invalidEncodingFiles = " ";
			if(fileExisted.size() > 0){
				existingFiles = getJoinedFileNames(fileExisted);
			}
			if(invalidFormat.size() > 0) {
				for(UploadResult uploadResult : invalidFormat) {
					invalidFormatFiles += uploadResult.getRelativeFileName() + " " + uploadResult.getFormatErrorMessage() + "\n";
				}
				
				//invalidFormatFiles = getJoinedFileNames(invalidFormat);
			}
			if(invalidEncoding.size() > 0) {
				invalidEncodingFiles = getJoinedFileNames(invalidEncoding);
			}
			if(writeFailed.size() > 0){
				writeFailedFiles = getJoinedFileNames(writeFailed);
			}
			if(directoryNotAllowedInZip.size() > 0) {
				directoryNotAllowedInZipFiles = getJoinedFileNames(directoryNotAllowedInZip);
			}
			result += "#" + directoryNotAllowedInZipFiles + "#" + writeFailedFiles + "#" + existingFiles + "#" + invalidFormatFiles + "#" + invalidEncodingFiles + "#";
		}
		
		return result;
	}

	private void evaluate(UploadResult uploadResult,
			List<UploadResult> notAdded, List<UploadResult> fileExisted,
			List<UploadResult> invalidFormat,
			List<UploadResult> invalidEncoding, List<UploadResult> writeFailed, 
			List<UploadResult> directoryNotAllowedInFile) {
		//if(uploadResult.getChildResults().isEmpty()) {
			if(!uploadResult.isSuccess())
				notAdded.add(uploadResult);
			if(uploadResult.isFileExisted())
				fileExisted.add(uploadResult);
			if(uploadResult.isInvalidFormat())
				invalidFormat.add(uploadResult);
			if(uploadResult.isInvalidEncoding())
				invalidEncoding.add(uploadResult);
			if(uploadResult.isWriteFailed())
				writeFailed.add(uploadResult);
			if(uploadResult.isDirectoryNotAllowedInFile())
				directoryNotAllowedInFile.add(uploadResult);
		//} else {
			for(UploadResult childResult : uploadResult.getChildResults()) 
				evaluate(childResult, notAdded, fileExisted, invalidFormat, invalidEncoding, writeFailed, directoryNotAllowedInFile);
		//}
	}

	private String getJoinedFileNames(List<UploadResult> uploadResults) {
		StringBuilder stringBuilder = new StringBuilder();
		for(UploadResult uploadResult : uploadResults)
			stringBuilder.append(uploadResult.getRelativeFileName() + "|");
		String result = stringBuilder.toString();
		if(result.isEmpty())
			return result;
		return result.substring(0, result.length() - 1);
	}

	/**
	 * Get the content of an uploaded file.
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) {
		String fieldName = request.getParameter(UConsts.PARAM_SHOW);
		File f = receivedFiles.get(fieldName);
		if (f != null) {
			response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = null;
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				String message = "Couldn't find file";
				log(message, e);
				log(LogLevel.ERROR, message, e);
			}
			if(is != null)
				try {
					copyFromInputStreamToOutputStream(is, response.getOutputStream());
				} catch (IOException e) {
					String message = "Couldn't copy input to output stream";
					log(message, e);
					log(LogLevel.ERROR, message, e);
				}
		} else {
			try {
				renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
			} catch (IOException e) {
				String message = "Couldn't render xml response";
				log(message, e);
				log(LogLevel.ERROR, message, e);
			}
		}
	}

	/**
	 * Remove a file when the user sends a delete request.
	 */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName) {
		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		receivedContentTypes.remove(fieldName);
		if (file != null) {
			file.delete();
		}
	}

}