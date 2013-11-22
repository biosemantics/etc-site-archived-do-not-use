package edu.arizona.sirls.etc.site.shared.rpc.file;

import edu.arizona.sirls.etc.site.client.Configuration;

public class FilePathShortener {

	private String fileBase;
	private String seperator;

	public FilePathShortener(String fileBase, String seperator) {
		this.fileBase = fileBase;
		this.seperator = seperator;
	}
	
	public String shorten(String fullFilePath, String fileOwnerUser, String viewerUser) {
		String usersOutputPath = (fullFilePath.replace(fileBase + seperator + fileOwnerUser, ""));
		if(fileOwnerUser.equals(viewerUser)) {
			usersOutputPath = "OWNED" + usersOutputPath;
		} else {
			usersOutputPath = "SHARED" + usersOutputPath;
		}
		return usersOutputPath;
	}
	
}
