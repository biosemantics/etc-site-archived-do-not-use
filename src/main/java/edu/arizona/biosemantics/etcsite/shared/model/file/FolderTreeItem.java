package edu.arizona.biosemantics.etcsite.shared.model.file;


public class FolderTreeItem extends FileTreeItem {
	
	public FolderTreeItem() {
		
	}

	public FolderTreeItem(String id, String name, String path, String displayPath, FileTypeEnum type, int ownerUserId, boolean isSystemFile, 
			  boolean isAllowsNewFiles, boolean isAllowsNewFolders) { 
		super(id, name, path, displayPath, type, ownerUserId, isSystemFile, isAllowsNewFiles, isAllowsNewFolders);
	}

}
