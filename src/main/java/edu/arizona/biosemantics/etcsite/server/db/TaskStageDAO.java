package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.util.Date;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TreeGenerationTaskStage;

public class TaskStageDAO {
	
	private TaskTypeDAO taskTypeDAO;
		
	public void setTaskTypeDAO(TaskTypeDAO taskTypeDAO) {
		this.taskTypeDAO = taskTypeDAO;
	}

	public TaskStage getTaskStage(int id) {
		TaskStage taskStage = null;
		try(Query query = new Query("SELECT * FROM etcsite_taskstages WHERE id = ?")) {
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
			log(LogLevel.ERROR, "Couldn't get task stage", e);
		}
		return taskStage;
	}

	public TaskStage getTaskStage(TaskType taskType, String taskStageString) {		
		TaskStage taskStage = null;
		try(Query query = new Query("SELECT * FROM etcsite_taskstages WHERE tasktype = ? AND name = ?")) {
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
			log(LogLevel.ERROR, "Couldn't get task stage of type and name", e);
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
		case ONTOLOGIZE: 
			return new OntologizeTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum.valueOf(taskStage));
		case MATRIX_GENERATION:
			return new MatrixGenerationTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum.valueOf(taskStage));
		case SEMANTIC_MARKUP:
			return new SemanticMarkupTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum.valueOf(taskStage));
		case TAXONOMY_COMPARISON:
			return new TaxonomyComparisonTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum.valueOf(taskStage));
		case TREE_GENERATION:
			return new TreeGenerationTaskStage(id, taskType, created, edu.arizona.biosemantics.etcsite.shared.model.treegeneration.TaskStageEnum.valueOf(taskStage));
		case VISUALIZATION:
			break;
		default:
			break;
		}
		return null;
	}

	public TaskStage getTreeGenerationTaskStage(String name) {
		TaskType taskType = taskTypeDAO.getTaskType(TaskTypeEnum.TREE_GENERATION);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof TreeGenerationTaskStage)
			return (TreeGenerationTaskStage)taskStage;
		return null;
	}

	public TreeGenerationTaskStage getTreeGenerationTaskStage(int taskStageId) {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof TreeGenerationTaskStage)
			return (TreeGenerationTaskStage)taskStage;
		return null;
	}

	public TaskStage getTaxonomyComparisonTaskStage(String name) {
		TaskType taskType = taskTypeDAO.getTaskType(TaskTypeEnum.TAXONOMY_COMPARISON);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof TaxonomyComparisonTaskStage)
			return (TaxonomyComparisonTaskStage)taskStage;
		return null;
	}
	
	public TaxonomyComparisonTaskStage getTaxonomyComparisonTaskStage(int taskStageId) {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof TaxonomyComparisonTaskStage)
			return (TaxonomyComparisonTaskStage)taskStage;
		return null;
	}
	
	public TaskStage getOntologizeTaskStage(String name) {
		TaskType taskType = taskTypeDAO.getTaskType(TaskTypeEnum.ONTOLOGIZE);
		TaskStage taskStage = this.getTaskStage(taskType, name);
		if(taskStage instanceof OntologizeTaskStage)
			return (OntologizeTaskStage)taskStage;
		return null;
	}
	
	public OntologizeTaskStage getOntologizeTaskStage(int taskStageId) {
		TaskStage taskStage = this.getTaskStage(taskStageId);
		if(taskStage instanceof OntologizeTaskStage)
			return (OntologizeTaskStage)taskStage;
		return null;
	}

}
