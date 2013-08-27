package edu.arizona.sirls.etc.site.server.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gwt.thirdparty.guava.common.io.Files;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.Tree;

public class FileService extends RemoteServiceServlet implements IFileService {

	private static final long serialVersionUID = -9193602268703418530L;
	private final String fileBase = "C://test//users";
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Tree<String> getUsersFiles(AuthenticationToken authenticationToken) {
		Tree<String> result = new Tree<String>("", true);
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			decorateTree(result, fileBase + "//" + authenticationToken.getUsername());
		} 
		return result;
	}
	
	private void decorateTree(Tree<String> fileTree, String filePath) {
		File file = new File(filePath);
		for(File child : file.listFiles()) {
			String name = child.getName();
			if(child.isFile()) {
				fileTree.addChild(new Tree<String>(name, false));
			} else if(child.isDirectory()) {
				Tree<String> childTree = new Tree<String>(name, true);
				decorateTree(childTree, child.getAbsolutePath());
				fileTree.addChild(childTree);
			}
		}
	}

	@Override
	public boolean deleteFile(AuthenticationToken authenticationToken, String target) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty()) { 
			File file = new File(fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			result = deleteRecursively(file);
		} 
		return result;
	}
	
	private boolean deleteRecursively(File file) {
		boolean result = false;
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				result = file.delete();
			} else {
				String files[] = file.list();
				for (String child : files) {
					File fileDelete = new File(file, child);
					result |= deleteRecursively(fileDelete);
				}
				if (file.list().length == 0) {
					result |= file.delete();
				}
			}
		} else {
			result = file.delete();
		}
		return result;
	}

	@Override
	public boolean moveFile(AuthenticationToken authenticationToken, String target, String newTarget) { 
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty() &&  !newTarget.trim().isEmpty()) { 
			File file = new File(fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			File targetFile = new File(fileBase + "//" + authenticationToken.getUsername() + "//" + newTarget);
			try {
				Files.move(file, targetFile);
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	}
	
	@Override
	public boolean createDirectory(AuthenticationToken authenticationToken, String target, String directoryName) { 
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !directoryName.trim().isEmpty()) { 
			File file = new File(fileBase + "//" + authenticationToken.getUsername() + "//" + target + "//" + directoryName);
			result = file.mkdir();
		}
		return result;
	}

	@Override
	public boolean setFileContent(AuthenticationToken authenticationToken, String target, String content) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty()) { 
			File file = new File(fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			if(file.exists() && file.isFile()) {
				try {
					Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
					writer.append(content);
					writer.flush();
					writer.close();
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public String getFileContent(AuthenticationToken authenticationToken,
			String target) {
		String result = null;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty()) { 
			File file = new File(fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			if(file.exists() && file.isFile()) {
				try {
					StringBuilder  stringBuilder = new StringBuilder();
					String line = null;
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
					while((line = reader.readLine() ) != null ) {
				        stringBuilder.append(line);
				        stringBuilder.append("\n");
				    }
				    result = stringBuilder.toString();
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
