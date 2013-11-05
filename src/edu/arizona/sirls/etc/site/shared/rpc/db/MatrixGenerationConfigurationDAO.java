package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class MatrixGenerationConfigurationDAO {

	private static MatrixGenerationConfigurationDAO instance;

	public static MatrixGenerationConfigurationDAO getInstance() {
		if(instance == null)
			instance = new MatrixGenerationConfigurationDAO();
		return instance;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(int configurationId) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		Query query = new Query("SELECT * FROM matrixgenerationconfigurations WHERE configuration = ?");
		query.setParameter(1, configurationId);
		ResultSet result = query.execute();
		while(result.next()) {
			matrixGenerationConfiguration = createMatrixGenerationConfiguration(result);
		}
		query.close();
		return matrixGenerationConfiguration;
	}

	private MatrixGenerationConfiguration createMatrixGenerationConfiguration(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		int numberOfInputFiles = result.getInt(3);
		int glossaryId = result.getInt(4);
		int oto = result.getInt(5);
		String output = result.getString(6);
		Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(configurationId);
		Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryId);
		return  new MatrixGenerationConfiguration(configuration, input, numberOfInputFiles, glossary, oto, output);
	}

	public MatrixGenerationConfiguration addMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration result = null;
		Query query = new Query("INSERT INTO `configurations` (`id`) VALUES(NULL)");
		query.execute();
		ResultSet generatedKeys = query.getGeneratedKeys();
		while(generatedKeys.next()) {
			Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(generatedKeys.getInt(1));
			Query matrixGenerationQuery = new Query("INSERT INTO `matrixgenerationconfigurations` " +
					"(`configuration`, `input`, `numberofinputfiles`, `glossary`, `oto`, `output`) VALUES (?, ?, ?, ?, ?)");
			matrixGenerationQuery.setParameter(1, configuration.getId());
			matrixGenerationQuery.setParameter(2, matrixGenerationConfiguration.getInput());
			matrixGenerationQuery.setParameter(3, matrixGenerationConfiguration.getNumberOfInputFiles());
			matrixGenerationQuery.setParameter(4, matrixGenerationConfiguration.getGlossary().getId());
			matrixGenerationQuery.setParameter(5, matrixGenerationConfiguration.getOtoId());
			matrixGenerationQuery.setParameter(6, matrixGenerationConfiguration.getOutput());
			
			matrixGenerationQuery.execute();
			matrixGenerationQuery.close();
			result = this.getMatrixGenerationConfiguration(generatedKeys.getInt(1));
		}
		return result;
	}

	public void updateMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		int glossaryId = matrixGenerationConfiguration.getGlossary().getId();
		String input = matrixGenerationConfiguration.getInput();
		String output = matrixGenerationConfiguration.getOutput();
		int otoId = matrixGenerationConfiguration.getOtoId();
		int numberOfInputFiles = matrixGenerationConfiguration.getNumberOfInputFiles();
		Query query = new Query("UPDATE matrixgenerationconfigurations SET input = ?, numberofinputfiles = ?, glossary = ?, oto = ?, output = ? WHERE configuration = ?");
		query.setParameter(1, input);
		query.setParameter(2, numberOfInputFiles);
		query.setParameter(3, glossaryId);
		query.setParameter(4, otoId);
		query.setParameter(5, output);
		query.setParameter(6, configuration.getId());
		query.executeAndClose();
	}

	public void remove(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		Query query = new Query("DELETE FROM matrixgenerationconfigurations WHERE configuration = ?");
		query.setParameter(1, configuration.getId());
		query.executeAndClose();
	}
}
