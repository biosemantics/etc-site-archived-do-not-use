package edu.arizona.biosemantics.etcsite.shared.model.file;


public class FolderTreeItem extends FileTreeItem {
	
	public FolderTreeItem() {
		
	}

	public FolderTreeItem(String name, String path, String displayPath, FileTypeEnum type, int ownerUserId, boolean isSystemFile, 
			  boolean isAllowsNewFiles, boolean isAllowsNewFolders) { 
		super(name, path, displayPath, type, ownerUserId, isSystemFile, isAllowsNewFiles, isAllowsNewFolders);
	}

}
