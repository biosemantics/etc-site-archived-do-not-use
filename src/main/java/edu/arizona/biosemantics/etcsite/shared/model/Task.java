package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable, Comparable<Task> {

	private static final long serialVersionUID = 5053756810897454852L;
	private int id;
	private String name;
	private TaskType taskType;
	private TaskStage taskStage;
	private AbstractTaskConfiguration taskConfiguration;
	private TinyUser user;
	private boolean resumable;
	private boolean complete;
	private boolean failed;
	private boolean tooLong;
	private Date completed;
	private Date created;
	private Date failedTime;
	
	public Task() { }
	
	public Task(int id, String name, TaskType taskType, TaskStage taskStage, AbstractTaskConfiguration taskConfiguration, 
			TinyUser user, boolean resumable, boolean complete, Date completed, boolean failed, Date failedTime, Date created, boolean tooLong) {
		super();
		this.id = id;
		this.name = name;
		this.taskType = taskType;
		this.taskStage = taskStage;
		this.taskConfiguration = taskConfiguration;
		this.user = user;
		this.resumable = resumable;
		this.complete = complete;
		this.completed = completed;
		this.failed = failed;
		this.failedTime = failedTime;
		this.created = created;
		this.tooLong = tooLong;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public TaskStage getTaskStage() {
		return taskStage;
	}

	public void setTaskStage(TaskStage taskStage) {
		this.taskStage = taskStage;
	}

	public AbstractTaskConfiguration getConfiguration() {
		return taskConfiguration;
	}

	public void setTaskConfiguration(AbstractTaskConfiguration taskConfiguration) {
		this.taskConfiguration = taskConfiguration;
	}

	public TinyUser getUser() {
		return user;
	}

	public void setUser(TinyUser user) {
		this.user = user;
	}

	public boolean isResumable() {
		return resumable;
	}

	public void setResumable(boolean resumable) {
		this.resumable = resumable;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public Date getCompleted() {
		return completed;
	}

	public void setCompleted(Date completed) {
		this.completed = completed;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public boolean isTooLong() {
		return tooLong;
	}

	public void setTooLong(boolean tooLong) {
		this.tooLong = tooLong;
	}
	
	public Date getFailedTime() {
		return failedTime;
	}

	public void setFailedTime(Date failedTime) {
		this.failedTime = failedTime;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public int compareTo(Task o) {
		return this.getId() - o.getId();
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
		Task task = (Task)object;
		if(task.getId()==this.id)
			return true;
		return false;
	}
}
