package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MatrixGenerationConfigurationDAO extends AbstractDAO {

	private static MatrixGenerationConfigurationDAO instance;

	public MatrixGenerationConfigurationDAO() throws IOException, ClassNotFoundException {
		super();
	}

	public static MatrixGenerationConfigurationDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new MatrixGenerationConfigurationDAO();
		return instance;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(int id) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM matrixgenerationconfiguration WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(1);
			String input = result.getString(2);
			int glossaryId = result.getInt(3);
			int oto = result.getInt(4);
			String output = result.getString(5);
			int taskId = result.getInt(6);
			long created = result.getLong(7);
			Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryId);
			Task task = TaskDAO.getInstance().getTask(taskId);
			matrixGenerationConfiguration = new MatrixGenerationConfiguration(id, input, glossary, oto, output, task, created);
		}
		this.closeConnection();
		return matrixGenerationConfiguration;
	}

	public MatrixGenerationConfiguration addMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration result = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("INSERT INTO matrixgenerationconfiguration " +
				"('input', 'glossary', 'oto', 'output', 'task') VALUES ('" + matrixGenerationConfiguration.getInput() + 
				"', " + matrixGenerationConfiguration.getGlossary().getId() + 
				", " + matrixGenerationConfiguration.getOtoId() + 
				", '" + matrixGenerationConfiguration.getOutput() + "'" +
				", " + matrixGenerationConfiguration.getTask().getId() + ")");		
		ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            result = this.getMatrixGenerationConfiguration(generatedKeys.getInt(1));
        }
		this.closeConnection();
		return result;
	}

	public MatrixGenerationConfiguration getMatrixGenerationConfigurationFromTask(int taskId) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM matrixgenerationconfiguration WHERE task = " + taskId);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			int id = result.getInt(1);
			String input = result.getString(2);
			int glossaryId = result.getInt(3);
			int oto = result.getInt(4);
			String output = result.getString(5);
			taskId = result.getInt(6);
			long created = result.getLong(7);
			Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryId);
			Task task = TaskDAO.getInstance().getTask(taskId);
			matrixGenerationConfiguration = new MatrixGenerationConfiguration(id, input, glossary, oto, output, task, created);
		}
		this.closeConnection();
		return matrixGenerationConfiguration;
	}
}
