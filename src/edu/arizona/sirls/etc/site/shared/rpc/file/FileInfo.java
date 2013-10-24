package edu.arizona.sirls.etc.site.shared.rpc.file;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 7766562389762413709L;
	private String name;
	private FileType fileType;
	
	public FileInfo() { }
	
	public FileInfo(String name, FileType fileType) {
		super();
		this.name = name;
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
	
	
	
}
