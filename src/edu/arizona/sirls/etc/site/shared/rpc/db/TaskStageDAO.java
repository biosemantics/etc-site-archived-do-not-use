package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskStageDAO extends AbstractDAO {

	private TaskStageDAO() throws IOException, ClassNotFoundException {
		super();
	}

	private static TaskStageDAO instance;

	public static TaskStageDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new TaskStageDAO();
		return instance;
	}
	
	public TaskStage getTaskStage(int id) throws SQLException, ClassNotFoundException, IOException {
		TaskStage taskStage = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM taskstages WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(1);
			int tasktypeId = result.getInt(2);
			String name = result.getString(3);
			long created = result.getLong(4);
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(tasktypeId);
			taskStage = new TaskStage(id, taskType, name, created);
		}
		this.closeConnection();
		return taskStage;
	}

	public TaskStage getTaskStage(TaskType taskType, String name) throws SQLException, ClassNotFoundException, IOException {		
		TaskStage taskStage = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM taskstages WHERE tasktype = " + taskType.getId() + " " +
				"name = '" + name + "'");
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			int id = result.getInt(1);
			int taskTypeId = result.getInt(2);
			name = result.getString(3);
			long created = result.getLong(4);
			taskType = TaskTypeDAO.getInstance().getTaskType(taskTypeId);
			taskStage = new TaskStage(id, taskType, name, created);
		}
		this.closeConnection();
		return taskStage;
	}

}
