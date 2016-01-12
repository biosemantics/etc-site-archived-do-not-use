package edu.arizona.biosemantics.etcsite.shared.model;

import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum;

public class OntologizeTaskStage extends TaskStage {

	private TaskStageEnum taskStage;

	public OntologizeTaskStage() { }
	
	public OntologizeTaskStage(int id, TaskType taskType, Date created, TaskStageEnum taskStage) {
		super(id, taskType, created);
		this.taskStage = taskStage;
	}

	public void setTaskStage(TaskStageEnum taskStage) {
		this.taskStage = taskStage;
	}
	
	@Override
	public int getTaskStageNumber() {
		int j=0;
		for (TaskStageEnum step : TaskStageEnum.values()) {
			j++;
			if (step.equals(this.taskStage)) {
				return j;
			}
		}
		return -1;
	}

	@Override
	public int getMaxTaskStageNumber() {
		return TaskStageEnum.values().length;
	}

	@Override
	public String getTaskStage() {
		return taskStage.toString();
	}
	
	@Override
	public String getDisplayName() {
		return taskStage.displayName();
	}
	
	@Override
	public int compareTo(TaskStage o) {
		if(o instanceof TreeGenerationTaskStage) 
			return this.taskStage.compareTo(((OntologizeTaskStage)o).taskStage);
		return 0;
	}
}
