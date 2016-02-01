package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.core.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;

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
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		
		String result = fileTreeItem.getFilePath();
		String displayFilePath = fileTreeItem.getDisplayFilePath();
		switch(fileTreeItem.getFileSource()) {
		case OWNED:
			result = "OWNED" + seperator + displayFilePath;
			break;
		case PUBLIC:
			result = "PUBLIC" + seperator + displayFilePath;
			break;
		case SHARED:
			result = "PUBLIC" + seperator + displayFilePath;
		default:
			result = displayFilePath;
			break;
		}
		
		result = result.replace(seperator + seperator, seperator);
		return result;
	}
	
}
