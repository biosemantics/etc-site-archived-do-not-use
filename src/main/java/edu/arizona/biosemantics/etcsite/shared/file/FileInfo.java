package edu.arizona.biosemantics.etcsite.shared.file;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 7766562389762413709L;
	private String name;
	private String filePath;
	private FileTypeEnum fileType;
	private String owner;
	private String displayFilePath;
	private boolean systemFile = true;
	private boolean allowsNewChildren = true;
	
	public FileInfo() { }
	
	public FileInfo(String name, String filePath, String displayFilePath, FileTypeEnum fileType, String owner, boolean systemFile, boolean allowsNewChildren) {
		super();
		this.name = name;
		this.filePath = filePath;
		this.displayFilePath = displayFilePath;
		this.fileType = fileType;
		this.owner = owner;
		this.systemFile = systemFile;
		this.allowsNewChildren = allowsNewChildren;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FileTypeEnum getFileType() {
		return fileType;
	}
	public void setFileType(FileTypeEnum fileType) {
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

	public String getOwner() {
		return owner;
	}

	public String getDisplayFilePath() {
		return this.displayFilePath;
	}

	public boolean isSystemFile() {
		return systemFile;
	}

	public boolean isAllowsNewChildren() {
		return allowsNewChildren;
	}
	
	
	
}
