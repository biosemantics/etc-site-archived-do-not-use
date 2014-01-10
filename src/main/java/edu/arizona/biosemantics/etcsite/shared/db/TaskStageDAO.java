package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum;

public class TaskStageDAO {

	private static TaskStageDAO instance;

	public static TaskStageDAO getInstance() {
		if(instance == null)
			instance = new TaskStageDAO();
		return instance;
	}
	
	public TaskStage getTaskStage(int id) throws SQLException, ClassNotFoundException, IOException {
		TaskStage taskStage = null;
		Query query = new Query("SELECT * FROM taskstages WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			String taskStageString = result.getString(2);
			int tasktypeId = result.getInt(3);
			Date created = result.getTimestamp(4);
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(tasktypeId);
			taskStage = createTaskStage(id, taskStageString, taskType, created);
		}
		query.close();
		return taskStage;
	}

	public TaskStage getTaskStage(TaskType taskType, String taskStageString) throws SQLException, ClassNotFoundException, IOException {		
		TaskStage taskStage = null;
		Query query = new Query("SELECT * FROM taskstages WHERE tasktype = ? AND name = ?");
		query.setParameter(1, taskType.getId());
		query.setParameter(2, taskStageString);
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			taskStageString = result.getString(2);
			int tasktypeId = result.getInt(3);
			Date created = result.getTimestamp(4);
			taskType = TaskTypeDAO.getInstance().getTaskType(tasktypeId);
			taskStage = createTaskStage(id, taskStageString, taskType, created);
		}
		query.close();
		return taskStage;
	}
	
	public MatrixGenerationTaskStage getMatrixGenerationTaskStage(int taskStageId) throws ClassNotFoundException, SQLException, IOException {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof MatrixGenerationTaskStage)
			return (MatrixGenerationTaskStage)taskStage;
		return null;
	}
	
	public MatrixGenerationTaskStage getMatrixGenerationTaskStage(String name) throws ClassNotFoundException, SQLException, IOException {
		TaskType taskType = TaskTypeDAO.getInstance().getTaskType(TaskTypeEnum.MATRIX_GENERATION);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof MatrixGenerationTaskStage)
			return (MatrixGenerationTaskStage)taskStage;
		return null;
	}
	
	public SemanticMarkupTaskStage getSemanticMarkupTaskStage(int taskStageId) throws ClassNotFoundException, SQLException, IOException {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof SemanticMarkupTaskStage)
			return (SemanticMarkupTaskStage)taskStage;
		return null;
	}
	
	public SemanticMarkupTaskStage getSemanticMarkupTaskStage(String name) throws ClassNotFoundException, SQLException, IOException {
		TaskType taskType = TaskTypeDAO.getInstance().getTaskType(TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof SemanticMarkupTaskStage)
			return (SemanticMarkupTaskStage)taskStage;
		return null;
	}
	
	private TaskStage createTaskStage(int id, String taskStage, TaskType taskType, Date created) {
		switch(taskType.getTaskTypeEnum()) {
		case MATRIX_GENERATION:
			return new MatrixGenerationTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum.valueOf(taskStage));
		case SEMANTIC_MARKUP:
			return new SemanticMarkupTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum.valueOf(taskStage));
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
