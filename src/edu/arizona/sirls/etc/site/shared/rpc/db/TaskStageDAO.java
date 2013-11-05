package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

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
			TaskStageEnum taskStageEnum = TaskStageEnum.valueOf(result.getString(2));
			int tasktypeId = result.getInt(3);
			Date created = result.getTimestamp(4);
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(tasktypeId);
			taskStage = new TaskStage(id, taskStageEnum, taskType, created);
		}
		query.close();
		return taskStage;
	}

	public TaskStage getTaskStage(TaskType taskType, TaskStageEnum taskStageEnum) throws SQLException, ClassNotFoundException, IOException {		
		TaskStage taskStage = null;
		Query query = new Query("SELECT * FROM taskstages WHERE tasktype = ? AND name = ?");
		query.setParameter(1, taskType.getId());
		query.setParameter(2, taskStageEnum.toString());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			taskStageEnum = TaskStageEnum.valueOf(result.getString(2));
			int taskTypeId = result.getInt(3);
			Date created = result.getTimestamp(4);
			taskType = TaskTypeDAO.getInstance().getTaskType(taskTypeId);
			taskStage = new TaskStage(id, taskStageEnum, taskType, created);
		}
		query.close();
		return taskStage;
	}

}
