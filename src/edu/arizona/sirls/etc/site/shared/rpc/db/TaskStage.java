package edu.arizona.sirls.etc.site.shared.rpc.db;

public class TaskStage {

	private int id;
	private TaskType taskType;
	private String name;

	public TaskStage(int id, TaskType taskType, String name) {
		super();
		this.id = id;
		this.taskType = taskType;
		this.name = name;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
