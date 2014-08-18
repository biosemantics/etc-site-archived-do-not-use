package edu.arizona.biosemantics.etcsite.shared.file;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 7766562389762413709L;
	private String name;
	private String filePath;
	private FileTypeEnum fileType;
	private int ownerUserId;
	private String displayFilePath;
	private boolean systemFile = true;
	private boolean allowsNewChildren = true;
	
	public FileInfo() { }
	
	public FileInfo(String name, String filePath, String displayFilePath, FileTypeEnum fileType, int ownerUserId, boolean systemFile, boolean allowsNewChildren) {
		super();
		this.name = name;
		this.filePath = filePath;
		this.displayFilePath = displayFilePath;
		this.fileType = fileType;
		this.ownerUserId = ownerUserId;
		this.systemFile = systemFile;
		this.allowsNewChildren = allowsNewChildren;
	}
	
	public String getName(boolean includeExtension) {
		if(isAllowsNewChildren())
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
		return name.substring(name.lastIndexOf("."), name.length());
	}
	
	public void setName(String name, boolean maintainExtension) {
		if(maintainExtension)
			this.setName(name + "." + getExtension());
		else
			this.setName(name);
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

	public boolean isAllowsNewChildren() {
		return allowsNewChildren;
	}
	
	
	
}
