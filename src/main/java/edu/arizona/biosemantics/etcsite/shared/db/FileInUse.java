package edu.arizona.biosemantics.etcsite.shared.db;

import java.util.Date;
import java.util.List;

public class FileInUse {

	private int id;
	private String filePath;
	private List<Task> usingTasks;
	private Date created;
	
	public FileInUse(int id, String filePath, List<Task> usingTasks, Date created) {
		super();
		this.id = id;
		this.filePath = filePath;
		this.usingTasks = usingTasks;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public List<Task> getUsingTasks() {
		return usingTasks;
	}
	public void setUsingTasks(List<Task> usingTasks) {
		this.usingTasks = usingTasks;
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
		FileInUse fileInUse = (FileInUse)object;
		if(fileInUse.getId()==this.id)
			return true;
		return false;
	}
}
