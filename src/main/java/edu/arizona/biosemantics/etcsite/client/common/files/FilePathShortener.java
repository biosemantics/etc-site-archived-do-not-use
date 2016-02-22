package edu.arizona.biosemantics.etcsite.client.common.files;

import java.io.File;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileSource;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;

public class FilePathShortener {

	public String shortenOutput(String fullFilePath, Task task, int viewerUserId) {
		String fileBase = ServerSetup.getInstance().getSetup().getFileBase();
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		
		int fileOwnerUserId = task.getUser().getId();
		String usersOutputPath = fullFilePath;
		if(fileOwnerUserId == viewerUserId) {
			usersOutputPath = (fullFilePath.replace(fileBase + seperator + fileOwnerUserId, ""));
			usersOutputPath = "OWNED" + usersOutputPath;
		} else {
			int nameSeperator = fullFilePath.lastIndexOf(seperator);
			if(nameSeperator != -1) {
				fullFilePath = fullFilePath.substring(fullFilePath.lastIndexOf(seperator)+1, fullFilePath.length());
			} 
			usersOutputPath = "SHARED" + seperator + task.getName() + seperator + "Output" + seperator + fullFilePath;
		}
		usersOutputPath = usersOutputPath.replace(seperator + seperator, seperator);
		return usersOutputPath;
	}
	
	public String shortenOwnedPath(String filePath) {
		String fileBase = ServerSetup.getInstance().getSetup().getFileBase();
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();

		int fileOwnerUserId = Authentication.getInstance().getUserId();
		String result = filePath;
		result = result.replace(fileBase + seperator + fileOwnerUserId, "");
		result = "OWNED" + result;
		result = result.replace(seperator + seperator, seperator);
		return result;
	}

	public String shorten(FileTreeItem fileTreeItem, int viewerUserId) {
		String separator = ServerSetup.getInstance().getSetup().getSeperator();
		String result = fileTreeItem.getFileSource().toString() + separator + fileTreeItem.getDisplayFilePath();
		result = result.replace(separator + separator, separator);
		return result;
	}

	public String shorten(String path, FileSource fileSource, int userId) {
		String fileBase = ServerSetup.getInstance().getSetup().getFileBase();
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		String publicFolder = ServerSetup.getInstance().getSetup().getPublicFolder();
		
		switch(fileSource) {
		case OWNED:
			return path.replace(fileBase + seperator + userId, "OWNED");
		case PUBLIC:
			return path.replace(publicFolder, "PUBLIC");
		case SHARED:
			path = path.replace(fileBase + seperator, "");
			path = path.substring(path.indexOf(seperator));
			return "SHARED" + seperator + path;
		case SYSTEM:
		default:
			return path;
		}
	}
	
}
