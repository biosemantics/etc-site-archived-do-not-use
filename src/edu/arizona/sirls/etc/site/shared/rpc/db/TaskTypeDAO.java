package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
			id = result.getInt(0);
			String name = result.getString(1);
			taskType = new TaskType(id, edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.valueOf(name));
		}
		this.closeConnection();
		return taskType;
	}
	
	public TaskType getTaskType(String name) throws SQLException {		
		TaskType taskType = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM tasktypes WHERE name = '" + name + "'");
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			int id = result.getInt(0);
			name = result.getString(1);
			taskType = new TaskType(id, edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.valueOf(name));
		}
		this.closeConnection();
		return taskType;
	}
}
