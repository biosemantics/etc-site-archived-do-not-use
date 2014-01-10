package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;

public class FileType implements Serializable {
	
	private static final long serialVersionUID = -5836126918662301203L;
	private int id;
	private FileTypeEnum fileTypeEnum;
	private Date created;
	
	public FileType(int id, FileTypeEnum fileTypeEnum, Date created) {
		super();
		this.id = id;
		this.fileTypeEnum = fileTypeEnum;
		this.created = created;
	}
	public FileType() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public FileTypeEnum getFileTypeEnum() {
		return fileTypeEnum;
	}
	public void setFileTypeEnum(FileTypeEnum fileType) {
		this.fileTypeEnum = fileType;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		FileType fileType = (FileType)object;
		if(fileType.getId()==this.id)
			return true;
		return false;
	}
	
}
