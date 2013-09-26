package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;

import edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum;

public class TaskType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -655524918747896723L;
	private int id;
	private edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum taskTypeEnum;
	private long created;
	
	public TaskType() { }
	
	public TaskType(int id, TaskTypeEnum taskTypeEnum, long created) {
		super();
		this.id = id;
		this.taskTypeEnum = taskTypeEnum;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum getTaskTypeEnum() {
		return taskTypeEnum;
	}
	public void setTaskTypeEnum(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum taskTypeEnum) {
		this.taskTypeEnum = taskTypeEnum;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

}
