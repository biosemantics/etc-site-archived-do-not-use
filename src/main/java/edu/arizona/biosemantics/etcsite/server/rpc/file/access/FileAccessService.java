package edu.arizona.biosemantics.etcsite.server.rpc.file.access;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.process.file.FileFormatter;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.SetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;

public class FileAccessService extends RemoteServiceServlet implements IFileAccessService {

	private static final long serialVersionUID = 5956919724639140570L;
	private IFilePermissionService filePermissionService;
	//private IFileFormatService fileFormatService = new FileFormatService();
	
	@Inject
	public FileAccessService(IFilePermissionService filePermissionService) {
		this.filePermissionService = filePermissionService;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	/**
	 * if set successfully, message =""
	 * else message = error
	 * 
	 * setFileContent assumes the content is valid to set for the file. format validation should be done before calling to setFileContent.
	 * @throws PermissionDeniedException 
	 * @throws SetFileContentFailedException 
	 */
	@Override
	public void setFileContent(AuthenticationToken authenticationToken, String filePath, String content) throws PermissionDeniedException, SetFileContentFailedException {
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult) 
			throw new PermissionDeniedException();
		
		/*if(filetype==FileTypeEnum.TAXON_DESCRIPTION){
			//validation
			RPCResult<Boolean> validationResult = fileFormatService.isValidTaxonDescriptionContent(authenticationToken, content);	
			if(!validationResult.getData().booleanValue()) return new RPCResult<Void>(false, "File content is not valid. Check it against the input schema");			
		}else if(filetype==FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION){
			RPCResult<Boolean> validationResult = fileFormatService.isValidMarkedupTaxonDescriptionContent(authenticationToken, content);	
			if(!validationResult.getData().booleanValue()) return new RPCResult<Void>(false, "File content is not valid. Check it against the output schema");			
		}*/
		File file = new File(filePath);
		if(file.exists() && file.isFile()) {
			try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
				writer.append(content);
				writer.flush();
			} catch (IOException e) {
				throw new SetFileContentFailedException();
			}
		}
	}

	@Override
	public String getFileContent(AuthenticationToken authenticationToken, String filePath) 
					throws PermissionDeniedException, GetFileContentFailedException {		
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult) 
			throw new PermissionDeniedException();
		
		File file = new File(filePath);
		if(!file.exists() || !file.isFile())
			throw new GetFileContentFailedException();
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch(IOException e) {
			log(LogLevel.ERROR, "Can't read file content", e);
			throw new GetFileContentFailedException();
		}
	}
	

	private String prettyFormat(String input) {
    	try (StringReader reader = new StringReader(input)) {
	        Source xmlInput = new StreamSource(reader);
	        try(StringWriter stringWriter = new StringWriter()) {
		        StreamResult xmlOutput = new StreamResult(stringWriter);
		        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        //transformerFactory.setAttribute("indent-number", indent);
		        
		        Transformer transformer = transformerFactory.newTransformer(); 
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        transformer.transform(xmlInput, xmlOutput);
		        return xmlOutput.getWriter().toString();
	        } catch (TransformerConfigurationException e) {
	        	log(LogLevel.ERROR, "Problem with transformer configuration", e);
			} catch (TransformerException e) {
				log(LogLevel.ERROR, "Problem with transformer", e);
			} catch (IOException e) {
				log(LogLevel.ERROR, "Problem opening/closing writer", e);
			}
    	}
    	return input;
	}
	
	@Override
	public String getFileContent(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType) throws PermissionDeniedException, GetFileContentFailedException {		
		String fileContentResult = getFileContent(authenticationToken, filePath);
		String formattedContent = new FileFormatter().format(fileContentResult, fileType);
		return formattedContent;
	}

	@Override
	public String getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType) throws PermissionDeniedException, GetFileContentFailedException {		
		String fileContentResult = getFileContent(authenticationToken, filePath);
		MyXmlXhtmlRenderer renderer = new MyXmlXhtmlRenderer();
		//Renderer renderer = XhtmlRendererFactory.getRenderer(XhtmlRendererFactory.XML); 
		try(OutputStream out = new ByteArrayOutputStream()) {
			renderer.highlight(null, IOUtils.toInputStream(fileContentResult), out, "UTF-8", false);
			String outString = out.toString();
			return outString;
		} catch (IOException e) {
			log(LogLevel.ERROR, "Problem opening/closing out stream", e);
		}
		return fileContentResult;

		/*try {
			TransformerFactory tf = TransformerFactory.newInstance();
			try(InputStream xslt = new FileInputStream("xmlverbatim.xsl"); // or FileInputStream
			Transformer t = tf.newTransformer(new StreamSource(xslt));
			System.out.println(t);
			t.setParameter("indent-elements", "yes");
			try(ByteArrayOutputStream s = new ByteArrayOutputStream();
			t.transform(new StreamSource(new ByteArrayInputStream(content.getBytes())), new StreamResult(s));
			byte[] out = s.toByteArray();
			String result = new String(out);
			//return out.toString();
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} */
	}

}
