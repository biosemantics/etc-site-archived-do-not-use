package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.sirls.etc.site.shared.rpc.TaskType;

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

	public int getTaskType(TaskType taskType) throws SQLException {
		this.openConnection();
		int taskTypeId = -1;
		
		PreparedStatement statement = this.executeSQL("SELECT id FROM taskTypes WHERE name = '" + taskType.toString() + "'");
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			taskTypeId = result.getInt(0);
		}
		
		this.closeConnection();
		return taskTypeId;
	}

}
