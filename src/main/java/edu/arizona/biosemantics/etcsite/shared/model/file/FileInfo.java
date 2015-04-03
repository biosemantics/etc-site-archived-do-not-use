package edu.arizona.biosemantics.etcsite.shared.model.file;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 7766562389762413709L;
	private String name;
	private String filePath;
	private FileTypeEnum fileType;
	private int ownerUserId;
	private String displayFilePath;
	private boolean systemFile = true;
	private boolean allowsNewFiles = true;
	private boolean allowsNewFolders = true;
	
	public FileInfo() { }
	
	public FileInfo(String name, String filePath, String displayFilePath, FileTypeEnum fileType, int ownerUserId, boolean systemFile, boolean allowsNewFiles, boolean allowsNewFolders) {
		super();
		this.name = name;
		this.filePath = filePath;
		this.displayFilePath = displayFilePath;
		this.fileType = fileType;
		this.ownerUserId = ownerUserId;
		this.systemFile = systemFile;
		this.allowsNewFiles = allowsNewFiles;
		this.allowsNewFolders = allowsNewFolders;
		
	}
	
	public String getName(boolean includeExtension) {
		if(isAllowsNewFiles() || isAllowsNewFolders())
			return name;
		if(includeExtension)
			return name;
		else
			return name.substring(0, name.lastIndexOf("."));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getExtension() {
		if(name.lastIndexOf(".") != -1) 
			return name.substring(name.lastIndexOf(".") + 1, name.length());
		return "";
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

	public int getOwnerUserId() {
		return ownerUserId;
	}

	public String getDisplayFilePath() {
		return this.displayFilePath;
	}

	public boolean isSystemFile() {
		return systemFile;
	}
	
	public boolean isEditable() {
		boolean result = true;
		//result &= !isSystemFile();
		result &= name.endsWith(".xml");
		//result &= !name.endsWith(".mx");
		//result &= !name.endsWith(".nxs");
		//result &= !name.endsWith(".nex");
		return result;
	}

	public boolean isAllowsNewFiles() {
		return allowsNewFiles;
	}
	
	public boolean isAllowsNewFolders() {
		return this.allowsNewFolders;
	}
	
	
	
}
