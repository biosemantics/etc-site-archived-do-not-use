package edu.arizona.sirls.etc.site.server.rpc;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.uwyn.jhighlight.renderer.Renderer;
import com.uwyn.jhighlight.renderer.XhtmlRendererFactory;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchService;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFormatter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLFileFormatter;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;

public class FileAccessService extends RemoteServiceServlet implements IFileAccessService {

	private static final long serialVersionUID = 5956919724639140570L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public RPCResult<Boolean> setFileContent(AuthenticationToken authenticationToken, String target, String content) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		if(target.trim().isEmpty()) 
			return new RPCResult<Boolean>(false, "Target may not be empty");
		
		File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
		if(file.exists() && file.isFile()) {
			try {
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				writer.append(content);
				writer.flush();
				writer.close();
				return new RPCResult<Boolean>(true, true);
			} catch (Exception e) {
				e.printStackTrace();
				return new RPCResult<Boolean>(false, "Internal Server Error");
			}
		}
		return new RPCResult<Boolean>(false, "Target does not exist or is not a file");
	}

	@Override
	public RPCResult<String> getFileContent(AuthenticationToken authenticationToken, String target) {
		RPCResult<String> result = new RPCResult<String>(false, "", "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult() && !target.trim().isEmpty()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			if(file.exists() && file.isFile()) {
				try {
					StringBuilder  stringBuilder = new StringBuilder();
					String line = null;
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					while((line = reader.readLine() ) != null ) {
				        stringBuilder.append(line);
				        stringBuilder.append("\n");
				    }
				    result = new RPCResult<String>(true, "", stringBuilder.toString());
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public RPCResult<String> getFileContent(AuthenticationToken authenticationToken, String target, FileType fileType) {
		RPCResult<String> fileContentResult = getFileContent(authenticationToken, target);
		if(fileContentResult.isSucceeded()) 
			return new RPCResult<String>(true, "", new FileFormatter().format(fileContentResult.getData(), fileType));
		return fileContentResult;
	}

	@Override
	public RPCResult<String> getFileContentHighlighted(AuthenticationToken authenticationToken, String target, FileType fileType) {
		RPCResult<String> fileContentResult = getFileContent(authenticationToken, target);
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
