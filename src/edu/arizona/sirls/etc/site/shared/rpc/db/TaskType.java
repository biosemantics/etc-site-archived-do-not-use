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
	
	public TaskType() { }
	
	public TaskType(int id, TaskTypeEnum taskTypeEnum) {
		super();
		this.id = id;
		this.taskTypeEnum = taskTypeEnum;
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

}
