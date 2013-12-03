package edu.arizona.sirls.etc.site.shared.rpc.file;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class FilePathShortener {

	private String fileBase;
	private String seperator;

	public FilePathShortener(String fileBase, String seperator) {
		this.fileBase = fileBase;
		this.seperator = seperator;
	}
	
	public String shortenOutput(String fullFilePath, Task task, String viewerUser) {
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

	public String shorten(FileInfo fileInfo, String viewerUser) {
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
