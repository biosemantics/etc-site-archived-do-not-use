package edu.arizona.biosemantics.etcsite.shared.file;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class FilePathShortener {

	public String shortenOutput(String fullFilePath, Task task, String viewerUser) {
		String fileBase = ServerSetup.getInstance().getSetup().getFileBase();
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		
		String fileOwnerUser = task.getUser().getName();
		String usersOutputPath = fullFilePath;
		if(fileOwnerUser.equals(viewerUser)) {
			usersOutputPath = (fullFilePath.replace(fileBase + seperator + fileOwnerUser, ""));
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

		String fileOwnerUser = Authentication.getInstance().getUsername();
		String result = filePath;
		result = result.replace(fileBase + seperator + fileOwnerUser, "");
		result = "OWNED" + result;
		result = result.replace(seperator + seperator, seperator);
		return result;
	}

	public String shorten(FileInfo fileInfo, String viewerUser) {
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		
		String result = fileInfo.getFilePath();
		String ownerUser = fileInfo.getOwner();
		if(ownerUser.equals(viewerUser)) {
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
