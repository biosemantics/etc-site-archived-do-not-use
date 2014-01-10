package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

public abstract class TaskStage implements Serializable, Comparable<TaskStage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7915389383513784433L;
	private int id;
	private TaskType taskType;
	private Date created;

	public TaskStage() { }
	
	public TaskStage(int id, TaskType taskType, Date created) {
		super();
		this.id = id;
		this.taskType = taskType;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public abstract int getTaskStageNumber();
	
	public abstract int getMaxTaskStageNumber();
	
	public abstract String getTaskStage();
	
	public abstract String getDisplayName();
	
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
		TaskStage taskStage = (TaskStage)object;
		if(taskStage.getId()==this.id)
			return true;
		return false;
	}
	
}
