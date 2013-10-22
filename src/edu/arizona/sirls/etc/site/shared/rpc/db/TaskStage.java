package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;
import java.util.Date;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskStage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7915389383513784433L;
	private int id;
	private TaskStageEnum taskStageEnum;
	private TaskType taskType;
	private Date created;

	public TaskStage() { }
	
	public TaskStage(int id, TaskStageEnum taskStageEnum, TaskType taskType, Date created) {
		super();
		this.id = id;
		this.taskType = taskType;
		this.taskStageEnum = taskStageEnum;
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


	public TaskStageEnum getTaskStageEnum() {
		return taskStageEnum;
	}

	public void setTaskStageEnum(TaskStageEnum taskStageEnum) {
		this.taskStageEnum = taskStageEnum;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	
	
}
