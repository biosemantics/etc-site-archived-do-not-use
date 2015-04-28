package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;

public class MatrixGenerationConfigurationDAO {

	private ConfigurationDAO configurationDAO;
		
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(int configurationId) {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		try(Query query = new Query("SELECT * FROM etcsite_matrixgenerationconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configurationId);
			ResultSet result = query.execute();
			while(result.next()) {
				matrixGenerationConfiguration = createMatrixGenerationConfiguration(result);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get matrix generation configuration", e);
		}
		return matrixGenerationConfiguration;
	}

	private MatrixGenerationConfiguration createMatrixGenerationConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		String output = result.getString(3);
		boolean inheritValues = result.getBoolean(4);
		boolean generateAbsentPresent = result.getBoolean(5);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		return  new MatrixGenerationConfiguration(configuration, input, output, inheritValues, generateAbsentPresent);
	}

	public MatrixGenerationConfiguration addMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		MatrixGenerationConfiguration result = null;
		try (Query query = new Query("INSERT INTO `etcsite_configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = configurationDAO.getConfiguration(generatedKeys.getInt(1));
				try (Query matrixGenerationQuery = new Query("INSERT INTO `etcsite_matrixgenerationconfigurations` " +
						"(`configuration`, `input`, `output`, `inheritvalues`, `generateabsentpresent`) VALUES (?, ?, ?, ?, ?)")) {
					matrixGenerationQuery.setParameter(1, configuration.getId());
					matrixGenerationQuery.setParameter(2, matrixGenerationConfiguration.getInput());
					matrixGenerationQuery.setParameter(3, matrixGenerationConfiguration.getOutput());
					matrixGenerationQuery.setParameter(4, matrixGenerationConfiguration.isInheritValues());
					matrixGenerationQuery.setParameter(5, matrixGenerationConfiguration.isGenerateAbsentPresent());
					matrixGenerationQuery.execute();
				}
				result = this.getMatrixGenerationConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert matrix generation configuration", e);
		}
		return result;
	}

	public void updateMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		String input = matrixGenerationConfiguration.getInput();
		String output = matrixGenerationConfiguration.getOutput();
		boolean inheritValues = matrixGenerationConfiguration.isInheritValues();
		boolean generateAbsentPresent = matrixGenerationConfiguration.isGenerateAbsentPresent();
		try (Query query = new Query("UPDATE etcsite_matrixgenerationconfigurations SET input = ?, output = ?, "
				+ "inheritvalues = ?, generateabsentpresent = ? WHERE configuration = ?")) {
			query.setParameter(1, input);
			query.setParameter(2, output);
			query.setParameter(3, inheritValues);
			query.setParameter(4, generateAbsentPresent);
			query.setParameter(5, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update matrix generation configuration", e);
		}
	}

	public void remove(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		try(Query query = new Query("DELETE FROM etcsite_matrixgenerationconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove matrix generation configuration", e);
		}
	}


	
}
