package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum;

public class TaskTypeDAO extends AbstractDAO {

	private static TaskTypeDAO instance;

	public TaskTypeDAO() throws IOException, ClassNotFoundException {
		super();
	}

	public static TaskTypeDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new TaskTypeDAO();
		return instance;
	}

	public TaskType getTaskType(int id) throws SQLException {
		TaskType taskType = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM tasktypes WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			long created = result.getLong(3);
			taskType = new TaskType(id, edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.valueOf(name), created);
		}
		this.closeConnection();
		return taskType;
	}
	
	public TaskType getTaskType(TaskTypeEnum taskTypeEnum) throws SQLException {		
		TaskType taskType = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM tasktypes WHERE name = '" + taskTypeEnum.toString() + "'");
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			int id = result.getInt(1);
			taskTypeEnum = TaskTypeEnum.valueOf(result.getString(2));
			long created = result.getLong(3);
			taskType = new TaskType(id, taskTypeEnum, created);
		}
		this.closeConnection();
		return taskType;
	}
}
