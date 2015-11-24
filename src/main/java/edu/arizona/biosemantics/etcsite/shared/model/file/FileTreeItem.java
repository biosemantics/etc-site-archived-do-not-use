package edu.arizona.biosemantics.etcsite.shared.model.file;

import java.io.Serializable;

public class FileTreeItem implements Serializable {
	
	private String name;
	private String path;
	private String displayPath;
	private FileTypeEnum type;
	private int ownerUserId;
	private boolean isSystemFile;
	private boolean isAllowsNewFiles;
	private boolean isAllowsNewFolders;

	public FileTreeItem() { 
		
	}
	
	public FileTreeItem(String name, String path, String displayPath, FileTypeEnum type, int ownerUserId, boolean isSystemFile, boolean isAllowsNewFiles, boolean isAllowsNewFolders) { 
		this.name = name;
		this.path = path;
		this.displayPath = displayPath;
		this.type = type;
		this.ownerUserId = ownerUserId;
		this.isSystemFile = isSystemFile;
		this.isAllowsNewFiles = isAllowsNewFiles;
		this.isAllowsNewFolders = isAllowsNewFolders;
	}
	
	public String getPath() {
		return path;
	}

	public String getText() {
		return name;
	}

	public boolean isSystemFile() {
		return isSystemFile;
	}

	public int getOwnerUserId() {
		return ownerUserId;
	}

	public String getFilePath() {
		return path;
	}

	public String getDisplayFilePath() {
		return displayPath;
	}

	public boolean isAllowsNewFiles() {
		return this.isAllowsNewFiles;
	}

	public boolean isAllowsNewFolders() {
		return this.isAllowsNewFolders;
	}

	public String getExtension() {
		if(name.lastIndexOf(".") != -1) 
			return name.substring(name.lastIndexOf(".") + 1, name.length());
		return "";
	}

	public void setName(String newName) {
		name = newName;
	}
	
	public String getName(boolean includeExtension) {
		if(isAllowsNewFiles() || isAllowsNewFolders())
			return name;
		if(includeExtension)
			return name;
		else
			return name.substring(0, name.lastIndexOf("."));
	}

}
