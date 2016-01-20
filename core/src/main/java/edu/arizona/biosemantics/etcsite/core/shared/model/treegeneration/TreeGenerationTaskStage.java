package edu.arizona.biosemantics.etcsite.core.shared.model.treegeneration;

import java.util.Date;

import edu.arizona.biosemantics.etcsite.core.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.core.shared.model.TaskType;

public class TreeGenerationTaskStage extends TaskStage {

	private TaskStageEnum taskStage;

	public TreeGenerationTaskStage() { }
	
	public TreeGenerationTaskStage(int id, TaskType taskType, Date created, TaskStageEnum taskStage) {
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
			return this.taskStage.compareTo(((TreeGenerationTaskStage)o).taskStage);
		return 0;
	}
}
