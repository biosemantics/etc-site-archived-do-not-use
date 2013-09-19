package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.thirdparty.guava.common.io.Files;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.Tree;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class FileService extends RemoteServiceServlet implements IFileService {

	private static final long serialVersionUID = -9193602268703418530L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	/**
	 * Cannot hand a fileFilter implementation of .filter(File file) to the method because
	 * file access is not available in the client code. Hence, translation from enum to implementation 
	 * becomes necessary.
	 */
	@Override
	public Tree<String> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter) {
		Tree<String> result = new Tree<String>("", true);
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			decorateTree(authenticationToken, result, fileFilter, Configuration.fileBase + "//" + authenticationToken.getUsername());
		} 
		return result;
	}
	
	private void decorateTree(AuthenticationToken authenticationToken, Tree<String> fileTree, FileFilter fileFilter, String filePath) {
		File file = new File(filePath);
		for(File child : file.listFiles()) {
			String name = child.getName();
			if(child.isFile() && !filter(authenticationToken, fileFilter, child)) {
				fileTree.addChild(new Tree<String>(name, false));
			} else if(child.isDirectory()) {
				Tree<String> childTree = new Tree<String>(name, true);
				decorateTree(authenticationToken, childTree, fileFilter, child.getAbsolutePath());
				fileTree.addChild(childTree);
			}
		}
	}

	private boolean filter(AuthenticationToken authenticationToken, FileFilter fileFilter, File child) {
		String target = getTarget(authenticationToken, child);
		IFileFormatService fileFormatService = new FileFormatService();
		File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
		switch(fileFilter) {
		case TAXON_DESCRIPTION:
			return !fileFormatService.isValidTaxonDescription(authenticationToken, target); 
		case GLOSSARY:
			return !fileFormatService.isValidGlossary(authenticationToken, target); 
		case EULER:
			return !fileFormatService.isValidEuler(authenticationToken, target); 
		case ALL:
			return false;
		case FILE:
			return !file.isFile();
		case DIRECTORY:
			return !file.isDirectory();
		}
		return true;
	}

	private String getTarget(AuthenticationToken authenticationToke, File child) {
		String absolutePath = child.getAbsolutePath();
		absolutePath = absolutePath.replaceAll("\\" + File.separator, "//");
		return absolutePath.replace(Configuration.fileBase + "//" + authenticationToke.getUsername() + "//", "");
	}

	@Override
	public boolean deleteFile(AuthenticationToken authenticationToken, String target) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
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
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			File targetFile = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + newTarget);
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
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target + "//" + directoryName);
			result = file.mkdir();
		}
		return result;
	}

	@Override
	public boolean isDirectory(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return file.isDirectory();
		}
		return false;
	}

	@Override
	public boolean isFile(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return file.isFile();
		}
		return false;
	}

	@Override
	public List<String> getDirectoriesFiles(AuthenticationToken authenticationToken, String target) {
		List<String> result = new LinkedList<String>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			for(File child : file.listFiles())
				if(child.isFile())
					result.add(child.getName());
			return result;
		}
		return result;
	}
}
