package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;
import java.util.Date;

public class Task implements Comparable<Task>, Serializable {

	private static final long serialVersionUID = -5559391963528999905L;
	private Date start;
	private TaskType taskType;
	private String name;
	private int progress;
	
	public Task() { 
		
	}
	
	public Task(Date start, TaskType taskType, String name, int progress) {
		super();
		this.start = start;
		this.taskType = taskType;
		this.name = name;
		this.progress = progress;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
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
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	@Override
	public int compareTo(Task task) {
		int progressDifference = this.progress - task.progress;
		if(progressDifference == 0)
			return this.start.compareTo(task.start);
		return progressDifference;
	}
	
	
	
}
