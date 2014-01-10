package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SemanticMarkupConfigurationDAO {

	private static SemanticMarkupConfigurationDAO instance;

	public static SemanticMarkupConfigurationDAO getInstance() {
		if(instance == null)
			instance = new SemanticMarkupConfigurationDAO();
		return instance;
	}
	
	public SemanticMarkupConfiguration getSemanticMarkupConfiguration(int configurationId) throws SQLException, ClassNotFoundException, IOException {
		SemanticMarkupConfiguration semanticMarkupConfiguration = null;
		Query query = new Query("SELECT * FROM semanticmarkupconfigurations WHERE configuration = ?");
		query.setParameter(1, configurationId);
		ResultSet result = query.execute();
		while(result.next()) {
			semanticMarkupConfiguration = createSemanticMarkupConfiguration(result);
		}
		query.close();
		return semanticMarkupConfiguration;
	}

	private SemanticMarkupConfiguration createSemanticMarkupConfiguration(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		int numberOfInputFiles = result.getInt(3);
		int glossaryId = result.getInt(4);
		int otoUploadId = result.getInt(5);
		String otoSecret = result.getString(6);
		String output = result.getString(7);
		Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(configurationId);
		Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryId);
		return  new SemanticMarkupConfiguration(configuration, input, numberOfInputFiles, glossary, otoUploadId, otoSecret, output);
	}

	public SemanticMarkupConfiguration addSemanticMarkupConfiguration(SemanticMarkupConfiguration semanticMarkupConfiguration) throws SQLException, ClassNotFoundException, IOException {
		SemanticMarkupConfiguration result = null;
		Query query = new Query("INSERT INTO `configurations` (`id`) VALUES(NULL)");
		query.execute();
		ResultSet generatedKeys = query.getGeneratedKeys();
		while(generatedKeys.next()) {
			Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(generatedKeys.getInt(1));
			Query semanticMarkupQuery = new Query("INSERT INTO `semanticmarkupconfigurations` " +
					"(`configuration`, `input`, `numberofinputfiles`, `glossary`, `oto_uploadid`, `oto_secret`, `output`) VALUES (?, ?, ?, ?, ?, ?, ?)");
			semanticMarkupQuery.setParameter(1, configuration.getId());
			semanticMarkupQuery.setParameter(2, semanticMarkupConfiguration.getInput());
			semanticMarkupQuery.setParameter(3, semanticMarkupConfiguration.getNumberOfInputFiles());
			semanticMarkupQuery.setParameter(4, semanticMarkupConfiguration.getGlossary().getId());
			semanticMarkupQuery.setParameter(5, semanticMarkupConfiguration.getOtoUploadId());
			semanticMarkupQuery.setParameter(6, semanticMarkupConfiguration.getOtoSecret());
			semanticMarkupQuery.setParameter(7, semanticMarkupConfiguration.getOutput());
			
			semanticMarkupQuery.execute();
			semanticMarkupQuery.close();
			result = this.getSemanticMarkupConfiguration(generatedKeys.getInt(1));
		}
		return result;
	}

	public void updateSemanticMarkupConfiguration(SemanticMarkupConfiguration semanticMarkupConfiguration) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = semanticMarkupConfiguration.getConfiguration();
		int glossaryId = semanticMarkupConfiguration.getGlossary().getId();
		String input = semanticMarkupConfiguration.getInput();
		String output = semanticMarkupConfiguration.getOutput();
		int otoUploadId = semanticMarkupConfiguration.getOtoUploadId();
		String otoSecret = semanticMarkupConfiguration.getOtoSecret();
		int numberOfInputFiles = semanticMarkupConfiguration.getNumberOfInputFiles();
		Query query = new Query("UPDATE semanticmarkupconfigurations SET input = ?, numberofinputfiles = ?, glossary = ?, oto_uploadid = ?, oto_secret = ?, output = ? WHERE configuration = ?");
		query.setParameter(1, input);
		query.setParameter(2, numberOfInputFiles);
		query.setParameter(3, glossaryId);
		query.setParameter(4, otoUploadId);
		query.setParameter(5, otoSecret);
		query.setParameter(6, output);
		query.setParameter(7, configuration.getId());
		query.executeAndClose();
	}

	public void remove(SemanticMarkupConfiguration semanticMarkupConfiguration) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = semanticMarkupConfiguration.getConfiguration();
		Query query = new Query("DELETE FROM semanticmarkupconfigurations WHERE configuration = ?");
		query.setParameter(1, configuration.getId());
		query.executeAndClose();
	}
}
