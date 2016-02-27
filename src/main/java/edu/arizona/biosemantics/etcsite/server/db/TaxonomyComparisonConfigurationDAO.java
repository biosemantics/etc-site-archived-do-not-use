package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;

public class TaxonomyComparisonConfigurationDAO {
	
	private ConfigurationDAO configurationDAO;
	private TaxonGroupDAO taxonGroupDAO;
	
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}
		
	public void setTaxonGroupDAO(TaxonGroupDAO taxonGroupDAO) {
		this.taxonGroupDAO = taxonGroupDAO;
	}

	public TaxonomyComparisonConfiguration getTaxonomyComparisonConfiguration(int id) {
		TaxonomyComparisonConfiguration taxonomyComparisonConfiguration = null;
		try(Query query = new Query("SELECT * FROM etcsite_taxonomycomparisonconfigurations WHERE configuration = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				taxonomyComparisonConfiguration = createTaxonomyComparisonConfiguration(result);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get taxonomy comparison configuration", e);
		}
		return taxonomyComparisonConfiguration;
	}

	private TaxonomyComparisonConfiguration createTaxonomyComparisonConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		int taxonGroupId = result.getInt(2);
		String cleanTaxInput = result.getString(3);
		String modelInput1 = result.getString(4);
		String modelInput2 = result.getString(5);
		String termReview1 = result.getString(6);
		String termReview2 = result.getString(7);
		String ontology = result.getString(8);
		String output = result.getString(9);
		int alignmentId = result.getInt(10);
		String alignmentSecret = result.getString(11);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		TaxonGroup taxonGroup = taxonGroupDAO.getTaxonGroup(taxonGroupId);
		return new TaxonomyComparisonConfiguration(configuration, taxonGroup, cleanTaxInput, modelInput1, modelInput2, 
				termReview1, termReview2, ontology, output, alignmentId, alignmentSecret);
	}

	
	public TaxonomyComparisonConfiguration addTaxonomyComparisonConfiguration(
			TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		TaxonomyComparisonConfiguration result = null;
		try (Query query = new Query("INSERT INTO `etcsite_configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = configurationDAO.getConfiguration(generatedKeys.getInt(1));
				try (Query taxonomyComparisonQuery = new Query("INSERT INTO `etcsite_taxonomycomparisonconfigurations` " +
						"(`configuration`, `taxon_group`, `cleantax_input`, `model_input1`, `model_input2`, "
						+ "`term_review1`, `term_review2`, `ontology`, `output`, `alignment_collection_id`, "
						+ "`alignment_collection_secret`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
					taxonomyComparisonQuery.setParameter(1, configuration.getId());
					taxonomyComparisonQuery.setParameter(2, taxonomyComparisonConfiguration.getTaxonGroup().getId());
					taxonomyComparisonQuery.setParameter(3, taxonomyComparisonConfiguration.getCleanTaxInput());
					taxonomyComparisonQuery.setParameter(4, taxonomyComparisonConfiguration.getModelInput1());
					taxonomyComparisonQuery.setParameter(5, taxonomyComparisonConfiguration.getModelInput2());
					taxonomyComparisonQuery.setParameter(6, taxonomyComparisonConfiguration.getTermReview1());
					taxonomyComparisonQuery.setParameter(7, taxonomyComparisonConfiguration.getTermReview2());
					taxonomyComparisonQuery.setParameter(8, taxonomyComparisonConfiguration.getOntology());
					taxonomyComparisonQuery.setParameter(9, taxonomyComparisonConfiguration.getOutput());
					taxonomyComparisonQuery.setParameter(10, taxonomyComparisonConfiguration.getAlignmentId());
					taxonomyComparisonQuery.setParameter(11, taxonomyComparisonConfiguration.getAlignmentSecret());
					taxonomyComparisonQuery.execute();
				}
				result = this.getTaxonomyComparisonConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert taxonomy comparison configuration", e);
		}
		return result;
	}
	
	public void updateTaxonomyComparisonQueryConfiguration(TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		Configuration configuration = taxonomyComparisonConfiguration.getConfiguration();
		int taxonGroup = taxonomyComparisonConfiguration.getTaxonGroup().getId();
		String cleanTaxInput = taxonomyComparisonConfiguration.getCleanTaxInput();
		String modelInput1 = taxonomyComparisonConfiguration.getModelInput1();
		String modelInput2 = taxonomyComparisonConfiguration.getModelInput2();
		String termReview1 = taxonomyComparisonConfiguration.getTermReview1();
		String termReview2 = taxonomyComparisonConfiguration.getTermReview2();
		String ontology = taxonomyComparisonConfiguration.getOntology();
		String output = taxonomyComparisonConfiguration.getOutput();
		int alignmentId = taxonomyComparisonConfiguration.getAlignmentId();
		String alignmentSecret = taxonomyComparisonConfiguration.getAlignmentSecret();
		try (Query query = new Query("UPDATE etcsite_taxonomycomparisonconfigurations SET taxon_group = ?,"
				+ "cleantax_input = ?, model_input1 = ?, model_input2 = ?, term_review1 = ?, term_review2 = ?, ontology = ?, output = ?, "
				+ "alignment_collection_id = ?, alignment_collection_secret = ? WHERE configuration = ?")) {
			query.setParameter(1, taxonGroup);
			query.setParameter(2, cleanTaxInput);
			query.setParameter(3, modelInput1);
			query.setParameter(4, modelInput2);
			query.setParameter(5, termReview1);
			query.setParameter(6, termReview2);
			query.setParameter(7, ontology);
			query.setParameter(8, output);
			query.setParameter(9, alignmentId);
			query.setParameter(10, alignmentSecret);
			query.setParameter(11, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update taxonomy comparison configuration", e);
		}
	}

	public void remove(TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		Configuration configuration = taxonomyComparisonConfiguration.getConfiguration();
		try(Query query = new Query("DELETE FROM etcsite_taxonomycomparisonconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove taxonomy comparison configuration", e);
		}
	}


}
