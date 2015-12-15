package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;


public class SemanticMarkupConfigurationDAO {

	private TaxonGroupDAO taxonGroupDAO;
	private ConfigurationDAO configurationDAO;
	
	public void setTaxonGroupDAO(TaxonGroupDAO taxonGroupDAO) {
		this.taxonGroupDAO = taxonGroupDAO;
	}

	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}
	
	public SemanticMarkupConfiguration getSemanticMarkupConfiguration(int configurationId) {
		SemanticMarkupConfiguration semanticMarkupConfiguration = null;
		try(Query query = new Query("SELECT * FROM etcsite_semanticmarkupconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configurationId);
			ResultSet result = query.execute();
			while(result.next()) {
				semanticMarkupConfiguration = createSemanticMarkupConfiguration(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get semantic markup configuration", e);
		}
		return semanticMarkupConfiguration;
	}

	private SemanticMarkupConfiguration createSemanticMarkupConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		int numberOfInputFiles = result.getInt(3);
		int taxonGroupId = result.getInt(4);
		boolean useEmptyGlossary = result.getBoolean(5);
		int otoUploadId = result.getInt(6);
		String otoSecret = result.getString(7);
		boolean otoCreatedDataset = result.getBoolean(8);
		String output = result.getString(9);
		String outputTermReview = result.getString(10);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		TaxonGroup taxonGroup = taxonGroupDAO.getTaxonGroup(taxonGroupId);
		return new SemanticMarkupConfiguration(configuration, input, numberOfInputFiles, taxonGroup, useEmptyGlossary, 
				otoUploadId, otoSecret, otoCreatedDataset, output, outputTermReview);
	}

	public SemanticMarkupConfiguration addSemanticMarkupConfiguration(SemanticMarkupConfiguration semanticMarkupConfiguration) {
		SemanticMarkupConfiguration result = null;
		try(Query query = new Query("INSERT INTO `etcsite_configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = configurationDAO.getConfiguration(generatedKeys.getInt(1));
				try(Query semanticMarkupQuery = new Query("INSERT INTO `etcsite_semanticmarkupconfigurations` " +
						"(`configuration`, `input`, `numberofinputfiles`, `taxon_group`, `use_empty_glossary`,  `oto_uploadid`, "
						+ "`oto_secret`, `oto_created_dataset`, `output`)" +
						" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
					semanticMarkupQuery.setParameter(1, configuration.getId());
					semanticMarkupQuery.setParameter(2, semanticMarkupConfiguration.getInput());
					semanticMarkupQuery.setParameter(3, semanticMarkupConfiguration.getNumberOfInputFiles());
					semanticMarkupQuery.setParameter(4, semanticMarkupConfiguration.getTaxonGroup().getId());
					semanticMarkupQuery.setParameter(5, semanticMarkupConfiguration.isUseEmptyGlossary());
					semanticMarkupQuery.setParameter(6, semanticMarkupConfiguration.getOtoUploadId());
					semanticMarkupQuery.setParameter(7, semanticMarkupConfiguration.getOtoSecret());
					semanticMarkupQuery.setParameter(8, semanticMarkupConfiguration.isOtoCreatedDataset());
					semanticMarkupQuery.setParameter(9, semanticMarkupConfiguration.getOutput());
					
					semanticMarkupQuery.execute();
				}
				result = this.getSemanticMarkupConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert semantic markup configuration", e);
		}
		return result;
	}

	public void updateSemanticMarkupConfiguration(SemanticMarkupConfiguration semanticMarkupConfiguration) {
		Configuration configuration = semanticMarkupConfiguration.getConfiguration();
		int glossaryId = semanticMarkupConfiguration.getTaxonGroup().getId();
		boolean useEmptyGlossary = semanticMarkupConfiguration.isUseEmptyGlossary();
		String input = semanticMarkupConfiguration.getInput();
		String output = semanticMarkupConfiguration.getOutput();
		int otoUploadId = semanticMarkupConfiguration.getOtoUploadId();
		String otoSecret = semanticMarkupConfiguration.getOtoSecret();
		boolean otoCreatedDataset = semanticMarkupConfiguration.isOtoCreatedDataset();
		int numberOfInputFiles = semanticMarkupConfiguration.getNumberOfInputFiles();
		try (Query query = new Query("UPDATE etcsite_semanticmarkupconfigurations SET input = ?, numberofinputfiles = ?, "
				+ "taxon_group = ?, use_empty_glossary = ?, " +
				"oto_uploadid = ?, oto_secret = ?, oto_created_dataset = ?,"
				+ " output = ? WHERE configuration = ?")) {
			query.setParameter(1, input);
			query.setParameter(2, numberOfInputFiles);
			query.setParameter(3, glossaryId);
			query.setParameter(4, useEmptyGlossary);
			query.setParameter(5, otoUploadId);
			query.setParameter(6, otoSecret);
			query.setParameter(7, otoCreatedDataset);
			query.setParameter(8, output);
			query.setParameter(9, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update semantic markup configuration", e);
		}
	}

	public void remove(SemanticMarkupConfiguration semanticMarkupConfiguration) {
		Configuration configuration = semanticMarkupConfiguration.getConfiguration();
		try (Query query = new Query("DELETE FROM etcsite_semanticmarkupconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove semantic markup configuration", e);
		}
	}
}
