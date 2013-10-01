package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskStageDAO extends AbstractDAO {

	private TaskStageDAO() throws IOException, ClassNotFoundException, SQLException {
		super();
	}

	private static TaskStageDAO instance;

	public static TaskStageDAO getInstance() throws ClassNotFoundException, IOException, SQLException {
		if(instance == null)
			instance = new TaskStageDAO();
		return instance;
	}
	
	public TaskStage getTaskStage(int id) throws SQLException, ClassNotFoundException, IOException {
		TaskStage taskStage = null;
		Query query = new Query("SELECT * FROM taskstages WHERE id = " + id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int tasktypeId = result.getInt(2);
			TaskStageEnum taskStageEnum = TaskStageEnum.valueOf(result.getString(3));
			long created = result.getLong(4);
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(tasktypeId);
			taskStage = new TaskStage(id, taskType, taskStageEnum, created);
		}
		query.close();
		return taskStage;
	}

	public TaskStage getTaskStage(TaskType taskType, TaskStageEnum taskStageEnum) throws SQLException, ClassNotFoundException, IOException {		
		TaskStage taskStage = null;
		Query query = new Query("SELECT * FROM taskstages WHERE tasktype = " + taskType.getId() + " AND " +
				"name = '" + taskStageEnum.toString() + "'");
		query.execute();
		ResultSet result = query.getResultSet();		
		while(result.next()) {
			int id = result.getInt(1);
			int taskTypeId = result.getInt(2);
			taskStageEnum = TaskStageEnum.valueOf(result.getString(3));
			long created = result.getLong(4);
			taskType = TaskTypeDAO.getInstance().getTaskType(taskTypeId);
			taskStage = new TaskStage(id, taskType, taskStageEnum, created);
		}
		query.close();
		return taskStage;
	}

}
