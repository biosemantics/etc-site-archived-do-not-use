package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum;

public class TaskTypeDAO extends AbstractDAO {

	private static TaskTypeDAO instance;

	public TaskTypeDAO() throws IOException, ClassNotFoundException, SQLException {
		super();
	}

	public static TaskTypeDAO getInstance() throws ClassNotFoundException, IOException, SQLException {
		if(instance == null)
			instance = new TaskTypeDAO();
		return instance;
	}

	public TaskType getTaskType(int id) throws SQLException, ClassNotFoundException, IOException {
		TaskType taskType = null;
		Query query = new Query("SELECT * FROM tasktypes WHERE id = " + id);
		query.execute();
		query.getResultSet();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			long created = result.getLong(3);
			taskType = new TaskType(id, edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.valueOf(name), created);
		}
		query.close();
		return taskType;
	}
	
	public TaskType getTaskType(TaskTypeEnum taskTypeEnum) throws SQLException, ClassNotFoundException, IOException {		
		TaskType taskType = null;
		Query query = new Query("SELECT * FROM tasktypes WHERE name = '" + taskTypeEnum.toString() + "'");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			int id = result.getInt(1);
			taskTypeEnum = TaskTypeEnum.valueOf(result.getString(2));
			long created = result.getLong(3);
			taskType = new TaskType(id, taskTypeEnum, created);
		}
		query.close();
		return taskType;
	}
}
