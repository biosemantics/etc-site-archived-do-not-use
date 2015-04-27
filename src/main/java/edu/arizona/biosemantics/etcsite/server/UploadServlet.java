package edu.arizona.biosemantics.etcsite.server;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.process.file.ContentValidatorProvider;
import edu.arizona.biosemantics.etcsite.server.process.file.IContentValidator;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;


/**
 * This is an example of how to use UploadAction class.
 * 
 * This servlet saves all received files in a temporary folder, and deletes them
 * when the user sends a remove request.
 * 
 * @author Manolo Carrasco Mo�ino
 * 
 */
public class UploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	private Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	private	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();
	
	private ContentValidatorProvider contentValidatorProvider = new ContentValidatorProvider();
	private XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
	
	public UploadServlet() {

	}
	
	/**
	 * Override executeAction to save the received files in a custom place and
	 * delete this items from session.
	 */
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
		
		IAuthenticationService authenticationService = new AuthenticationService();
		AuthenticationResult authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(userID, sessionID));
		
		int numberNotAdded = 0;
		List<String> fileNames = new LinkedList<String>();
		List<String> fileAlreadyExists = new LinkedList<String>();
		if(authenticationResult.getResult()) {
			for (FileItem item : sessionFiles) {
				if (false == item.isFormField()) {
					
						// / Create a new file based on the remote file name in the
						// client
						// String saveName =
						// item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+",
						// "_");
						// File file =new File("/tmp/" + saveName);
	
						// / Create a temporary file placed in /tmp (only works in
						// unix)
						// File file = File.createTempFile("upload-", ".bin", new
						// File("/tmp"));
	
						// / Create a temporary file placed in the default system
						// temp folder
						File file = new File(target + File.separator + item.getName());
						if(!file.exists()) {
							String fileContent;
							try {
								fileContent = item.getString("UTF-8");
								boolean valid = true;
								IContentValidator validator = contentValidatorProvider.getValidator(fileTypeEnum);
								if(validator != null)
									valid = validator.validate(fileContent);
								
								if(valid) {
									try {
										file.createNewFile();
										item.write(file);
										
										try {
											xmlNamespaceManager.setXmlSchema(file, fileTypeEnum);
											
											// / Save a list with the received files
											receivedFiles.put(item.getFieldName(), file);
											receivedContentTypes.put(item.getFieldName(),
													item.getContentType());
						
											// / Send a customized message to the client.
											//response += "File saved as " + file.getAbsolutePath();
											
										} catch (Exception e) {
											String message = "Couldn't set xml schema to file. Will attempt to delete the file";
											log(message, e);
											log(LogLevel.ERROR, message, e);
											file.delete();
											numberNotAdded++;
											fileNames.add(item.getName());
										}
										
									} catch(IOException e) {
										String message = "Couldn't create new file";
										log(message, e);
										log(LogLevel.ERROR, message, e);
										numberNotAdded++;
										fileNames.add(item.getName());
									} catch(Exception e) {
										String message = "Couldn't write item to file";
										log(message, e);
										log(LogLevel.ERROR, message, e);
										numberNotAdded++;
										fileNames.add(item.getName());
									}
								} else {
									numberNotAdded++;
									fileNames.add(item.getName());
									//error message would when too long (because many files are not valid) freeze the web page
									//response += "File " + item.getName() + " was not added. Invalid file format.\n";
								}
							} catch (UnsupportedEncodingException e) {
								String message = "Couldn't get UTF-8 encoding";
								log(message, e);
								log(LogLevel.ERROR, message, e);
							}
						} else {
							numberNotAdded++;
							fileAlreadyExists.add(item.getName());
							//response += "File " + item.getName() + " was not added. File with same name exists in directory.\n";
						}
				}
			}
		}
		
		if(numberNotAdded == 1)
			response += numberNotAdded + " file was not added due to invalid file format and or name collisions";
		if(numberNotAdded > 1)
			response += numberNotAdded + " files were not added due to invalid file format and or name collisions";
		String allFiles = " "; //Do not remove this space - used in client side processing of server response
		String allExistingFiles = " "; //Do not remove this space - used in client side processing of server response
		if(fileNames.size()>0){
			allFiles = StringUtils.join(fileNames, '|');
		}
		if(fileAlreadyExists.size() > 0){
			allExistingFiles = StringUtils.join(fileAlreadyExists, '|');
		}
		if(fileNames.size() > 0 || fileAlreadyExists.size()>0){
			response += "#"+allFiles+"#"+allExistingFiles+"#";
		}
		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);
		
		// / Send your customized message to the client. <- this is where i specify which files were not valid
		return response;
	}

	
	/**
	 * Get the content of an uploaded file.
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request,
			HttpServletResponse response) {
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