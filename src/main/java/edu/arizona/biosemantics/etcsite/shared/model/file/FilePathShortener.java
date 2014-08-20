package edu.arizona.biosemantics.etcsite.shared.model.file;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

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

	public String shorten(FileInfo fileInfo, int viewerUserId) {
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		
		String result = fileInfo.getFilePath();
		int ownerUserId = fileInfo.getOwnerUserId();
		if(ownerUserId == viewerUserId) {
			result = fileInfo.getDisplayFilePath();
			result = "OWNED" + seperator + result;
		} else {
			result = fileInfo.getDisplayFilePath();
			result = "SHARED" + seperator + result;
		}
		
		result = result.replace(seperator + seperator, seperator);
		return result;
	}
	
}
