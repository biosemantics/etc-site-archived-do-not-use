package edu.arizona.biosemantics.etcsite.client.common.files;

import java.io.File;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.Setup;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
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
	
	public static void main(String[] args) {
		//Authentication.getInstance().setUserId(1);
		ServerSetup.getInstance().setSetup(new Setup());
		ServerSetup.getInstance().getSetup().setFileBase("/var/lib/etcsite/users");
		ServerSetup.getInstance().getSetup().setSeperator("/");
		ServerSetup.getInstance().getSetup().setPublicFolder("/var/lib/etcsite/public");
		FilePathShortener shortener = new FilePathShortener();
		System.out.println(shortener.shortenPath("/var/lib/etcsite/users/1/something/1.xml"));
		System.out.println(shortener.shortenPath("/var/lib/etcsite/users/2/something/1.xml"));
		System.out.println(shortener.shortenPath("/var/lib/etcsite/public/1/something/1.xml"));
	}
	
	public String shortenPath(String filePath) {
		String fileBase = ServerSetup.getInstance().getSetup().getFileBase();
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		String publicBase = ServerSetup.getInstance().getSetup().getPublicFolder();
		
		int userId = Authentication.getInstance().getUserId();
		if(filePath.startsWith(fileBase)) {
			String remainder = filePath.replace(fileBase, "");
			if(remainder.startsWith(seperator + userId))
				return "OWNED" + seperator + remainder.replace(seperator + userId + seperator, "");
			else
				return "SHARED" + seperator + removeUserId(remainder);
		} else if(filePath.startsWith(publicBase))
			return "PUBLIC" + filePath.replace(publicBase, "");
		return filePath;
	}
		
	private String removeUserId(String remainder) {
		String seperator = ServerSetup.getInstance().getSetup().getSeperator();
		if(remainder.startsWith(seperator))
			remainder = remainder.replaceFirst(seperator, "");
		int index = remainder.indexOf(seperator);
		if(index != -1) {
			return remainder.substring(index + 1);
		}
		return remainder;
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
