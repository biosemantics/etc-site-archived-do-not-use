package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;

public class TaskStageDAO {
	
	private TaskTypeDAO taskTypeDAO;
		
	public void setTaskTypeDAO(TaskTypeDAO taskTypeDAO) {
		this.taskTypeDAO = taskTypeDAO;
	}

	public TaskStage getTaskStage(int id) {
		TaskStage taskStage = null;
		try(Query query = new Query("SELECT * FROM taskstages WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				String taskStageString = result.getString(2);
				int tasktypeId = result.getInt(3);
				Date created = result.getTimestamp(4);
				TaskType taskType = taskTypeDAO.getTaskType(tasktypeId);
				taskStage = createTaskStage(id, taskStageString, taskType, created);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return taskStage;
	}

	public TaskStage getTaskStage(TaskType taskType, String taskStageString) {		
		TaskStage taskStage = null;
		try(Query query = new Query("SELECT * FROM taskstages WHERE tasktype = ? AND name = ?")) {
			query.setParameter(1, taskType.getId());
			query.setParameter(2, taskStageString);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				taskStageString = result.getString(2);
				int tasktypeId = result.getInt(3);
				Date created = result.getTimestamp(4);
				taskType = taskTypeDAO.getTaskType(tasktypeId);
				taskStage = createTaskStage(id, taskStageString, taskType, created);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return taskStage;
	}
	
	public MatrixGenerationTaskStage getMatrixGenerationTaskStage(int taskStageId) {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof MatrixGenerationTaskStage)
			return (MatrixGenerationTaskStage)taskStage;
		return null;
	}
	
	public MatrixGenerationTaskStage getMatrixGenerationTaskStage(String name) {
		TaskType taskType = taskTypeDAO.getTaskType(TaskTypeEnum.MATRIX_GENERATION);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof MatrixGenerationTaskStage)
			return (MatrixGenerationTaskStage)taskStage;
		return null;
	}
	
	public SemanticMarkupTaskStage getSemanticMarkupTaskStage(int taskStageId) {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof SemanticMarkupTaskStage)
			return (SemanticMarkupTaskStage)taskStage;
		return null;
	}
	
	public SemanticMarkupTaskStage getSemanticMarkupTaskStage(String name) {
		TaskType taskType = taskTypeDAO.getTaskType(TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof SemanticMarkupTaskStage)
			return (SemanticMarkupTaskStage)taskStage;
		return null;
	}
	
	private TaskStage createTaskStage(int id, String taskStage, TaskType taskType, Date created) {
		switch(taskType.getTaskTypeEnum()) {
		case MATRIX_GENERATION:
			return new MatrixGenerationTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum.valueOf(taskStage));
		case SEMANTIC_MARKUP:
			return new SemanticMarkupTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum.valueOf(taskStage));
		case TAXONOMY_COMPARISON:
			break;
		case TREE_GENERATION:
			break;
		case VISUALIZATION:
			break;
		default:
			break;
		}
		return null;
	}

}