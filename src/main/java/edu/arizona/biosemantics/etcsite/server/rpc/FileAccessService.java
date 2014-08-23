package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.process.file.FileFormatter;
import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;

public class FileAccessService extends RemoteServiceServlet implements IFileAccessService {

	private static final long serialVersionUID = 5956919724639140570L;
	private IFilePermissionService filePermissionService = new FilePermissionService();
	//private IFileFormatService fileFormatService = new FileFormatService();
	
	/**
	 * if set successfully, message =""
	 * else message = error
	 * 
	 * setFileContent assumes the content is valid to set for the file. format validation should be done before calling to setFileContent.
	 */
	@Override
	public RPCResult<Void> setFileContent(AuthenticationToken authenticationToken, String filePath, String content) {
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(!permissionResult.getData()) 
			return new RPCResult<Void>(false, "No permission");
		
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
				return new RPCResult<Void>(true, "");
			} catch (Exception e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "Internal Server Error");
			}
		}
		return new RPCResult<Void>(false, "File at path does not exist or is not a file");
	}

	@Override
	public RPCResult<String> getFileContent(AuthenticationToken authenticationToken, String filePath) {		
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<String>(false, permissionResult.getMessage(), "");
		if(!permissionResult.getData()) 
			return new RPCResult<String>(false, "No permission", "");
		
		File file = new File(filePath);
		if(!file.exists() || !file.isFile())
			return new RPCResult<String>(false, "File doesn't exist", "");
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			return new RPCResult<String>(true, "", prettyFormat(new String(encoded, StandardCharsets.UTF_8)));
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Internal Server Error", "");
		}
	}
	

	private String prettyFormat(String input) {
	    try {
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
		        }
	    	}
	    } catch (Exception e) {
	        e.printStackTrace(); // simple exception handling, please review it
	        return input;
	    }
	}
	@Override
	public RPCResult<String> getFileContent(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType) {		
		RPCResult<String> fileContentResult = getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String formattedContent = new FileFormatter().format(fileContentResult.getData(), fileType);
			return new RPCResult<String>(true, "", formattedContent);
		}
		return fileContentResult;
	}

	@Override
	public RPCResult<String> getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType) {		
		RPCResult<String> fileContentResult = getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			MyXmlXhtmlRenderer renderer = new MyXmlXhtmlRenderer();
			//Renderer renderer = XhtmlRendererFactory.getRenderer(XhtmlRendererFactory.XML); 
			try(OutputStream out = new ByteArrayOutputStream()) {
				renderer.highlight(null, IOUtils.toInputStream(fileContentResult.getData()), out, "UTF-8", false);
				String outString = out.toString();
				return new RPCResult<String>(true, "", outString);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
