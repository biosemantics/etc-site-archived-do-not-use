package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MatrixGenerationConfigurationDAO extends AbstractDAO {

	private static TaskStageDAO instance;

	public MatrixGenerationConfigurationDAO() throws IOException, ClassNotFoundException {
		super();
	}

	public static TaskStageDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new TaskStageDAO();
		return instance;
	}
	
	public MatrixGenerationConfiguration getConfiguration(int id) throws SQLException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM matrixgenerationconfiguration WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(0);
			String input = result.getString(1);
			String glossary = result.getString(2);
			int oto = result.getInt(3);
			matrixGenerationConfiguration = new MatrixGenerationConfiguration(id, input, glossary, oto);
		}
		this.closeConnection();
		return matrixGenerationConfiguration;
	}
}
