package edu.arizona.sirls.etc.site.shared.rpc.file;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 7766562389762413709L;
	private String name;
	private String filePath;
	private FileType fileType;
	
	public FileInfo() { }
	
	public FileInfo(String name, String filePath, FileType fileType) {
		super();
		this.name = name;
		this.filePath = filePath;
		this.fileType = fileType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FileType getFileType() {
		return fileType;
	}
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String toString() {
		return name + " " + filePath + " " + fileType;
	}
	
}
