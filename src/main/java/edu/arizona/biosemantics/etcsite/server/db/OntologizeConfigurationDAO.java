package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;

public class OntologizeConfigurationDAO {
	
	private ConfigurationDAO configurationDAO;
	private TaxonGroupDAO taxonGroupDAO;
	
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}
	
	public void setTaxonGroupDAO(TaxonGroupDAO taxonGroupDAO) {
		this.taxonGroupDAO = taxonGroupDAO;
	}
	
	public OntologizeConfiguration getOntologizeConfiguration(int configurationId) {
		OntologizeConfiguration ontologizeConfiguration = null;
		try(Query query = new Query("SELECT * FROM etcsite_ontologizeconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configurationId);
			ResultSet result = query.execute();
			while(result.next()) {
				ontologizeConfiguration = createOntologizeConfiguration(result);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get ontologize configuration", e);
		}
		return ontologizeConfiguration;
	}

	private OntologizeConfiguration createOntologizeConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		int taxonGroupId = result.getInt(3);
		String ontology = result.getString(4);
		int ontologizeCollectionId = result.getInt(5);
		String ontologizeCollectionSecret = result.getString(6);
		String output = result.getString(7);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		TaxonGroup taxonGroup = taxonGroupDAO.getTaxonGroup(taxonGroupId);
		return  new OntologizeConfiguration(configuration, input, taxonGroup, ontology, ontologizeCollectionId, 
				ontologizeCollectionSecret, output);
	}

	public OntologizeConfiguration addOntologizeConfiguration(OntologizeConfiguration ontologizeConfiguration) {
		OntologizeConfiguration result = null;
		try (Query query = new Query("INSERT INTO `etcsite_configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = configurationDAO.getConfiguration(generatedKeys.getInt(1));
				try (Query ontologiyeQuery = new Query("INSERT INTO `etcsite_ontologizeconfigurations` " +
						"(`configuration`, `input`, `taxon_group`, `ontology`, `ontologize_collection_id`, "
						+ "`ontologize_collection_secret`, `output`) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
					ontologiyeQuery.setParameter(1, configuration.getId());
					ontologiyeQuery.setParameter(2, ontologizeConfiguration.getInput());
					ontologiyeQuery.setParameter(3, ontologizeConfiguration.getTaxonGroup().getId());
					ontologiyeQuery.setParameter(4, ontologizeConfiguration.getOntologyFile());
					ontologiyeQuery.setParameter(5, ontologizeConfiguration.getOntologizeCollectionId());
					ontologiyeQuery.setParameter(6, ontologizeConfiguration.getOntologizeCollectionSecret());
					ontologiyeQuery.setParameter(7, ontologizeConfiguration.getOutput());
					ontologiyeQuery.execute();
				}
				result = this.getOntologizeConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert ontologiye configuration", e);
		}
		return result;
	}

	public void remove(OntologizeConfiguration ontologizeConfiguration) {
		Configuration configuration = ontologizeConfiguration.getConfiguration();
		try(Query query = new Query("DELETE FROM etcsite_ontologizeconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove ontologize configuration", e);
		}
	}

}
