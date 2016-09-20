package edu.arizona.biosemantics.etcsite.shared.model.file;

import java.io.Serializable;

public class FileTreeItem implements Serializable {
	
	private String id;
	private String name;
	private String path;
	private String displayPath;
	private FileTypeEnum type;
	private int ownerUserId;
	private boolean isSystemFile;
	private boolean isAllowsNewFiles;
	private boolean isAllowsNewFolders;
	private FileSource fileSource;
	private String displayName;

	public FileTreeItem() { 
		
	}
	
	public FileTreeItem(String id, String name, String displayName, String path, String displayPath, FileTypeEnum type, int ownerUserId, boolean isSystemFile, boolean isAllowsNewFiles, boolean isAllowsNewFolders, 
			FileSource fileSource) { 
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.path = path;
		this.displayPath = displayPath;
		this.type = type;
		this.ownerUserId = ownerUserId;
		this.isSystemFile = isSystemFile;
		this.isAllowsNewFiles = isAllowsNewFiles;
		this.isAllowsNewFolders = isAllowsNewFolders;
		this.fileSource = fileSource;
	}
	
	public String getPath() {
		return path;
	}

	public String getText() {
		return displayName;
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
		if(this instanceof FolderTreeItem)
			return name;
		if(includeExtension)
			return name;
		else
			return name.substring(0, name.lastIndexOf("."));
	}

	public String getId() {
		return id;
	}
	
	

	public void setPath(String path) {
		this.path = path;
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

	public FileTypeEnum getFileType() {
		return type;
	}
	
	public FileSource getFileSource() {
		return fileSource;
	}

}
