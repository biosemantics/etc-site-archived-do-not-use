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
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.file.FileFormatter;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class FileAccessService extends RemoteServiceServlet implements IFileAccessService {

	private static final long serialVersionUID = 5956919724639140570L;
	private IFilePermissionService filePermissionService = new FilePermissionService();
	
	@Override
	public RPCResult<Void> setFileContent(AuthenticationToken authenticationToken, String filePath, String content) {
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(!permissionResult.getData()) 
			return new RPCResult<Void>(false, "No permission");
		
		File file = new File(filePath);
		if(file.exists() && file.isFile()) {
			try {
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				writer.append(content);
				writer.flush();
				writer.close();
				return new RPCResult<Void>(true);
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
			StringBuilder  stringBuilder = new StringBuilder();
			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			while((line = reader.readLine()) != null ) {
		        stringBuilder.append(line);
		        stringBuilder.append("\n");
		    }
			reader.close();
		    return new RPCResult<String>(true, "", stringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Internal Server Error", "");
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
			OutputStream out = new ByteArrayOutputStream();
			try {
				renderer.highlight(null, IOUtils.toInputStream(fileContentResult.getData()), out, "UTF-8", false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String outString = out.toString();
			return new RPCResult<String>(true, "", outString);
		}
		return fileContentResult;

		/*try {
			TransformerFactory tf = TransformerFactory.newInstance();
			InputStream xslt = new FileInputStream("xmlverbatim.xsl"); // or FileInputStream
			Transformer t = tf.newTransformer(new StreamSource(xslt));
			System.out.println(t);
			t.setParameter("indent-elements", "yes");
			ByteArrayOutputStream s = new ByteArrayOutputStream();
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
