package edu.arizona.biosemantics.etcsite.server.db;

import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;

public class TaskConfigurationDAO {

	private SemanticMarkupConfigurationDAO semanticMarkupConfigurationDAO;
	private MatrixGenerationConfigurationDAO matrixGenerationConfigurationDAO;
	private TreeGenerationConfigurationDAO treeGenerationConfigurationDAO;
	private TaxonomyComparisonConfigurationDAO taxonomyComparisonConfigurationDAO;
	private VisualizationConfigurationDAO visualizationConfigurationDAO;
		
	public void setSemanticMarkupConfigurationDAO(
			SemanticMarkupConfigurationDAO semanticMarkupConfigurationDAO) {
		this.semanticMarkupConfigurationDAO = semanticMarkupConfigurationDAO;
	}
	
	public void setTreeGenerationConfigurationDAO(
			TreeGenerationConfigurationDAO treeGenerationConfigurationDAO) {
		this.treeGenerationConfigurationDAO = treeGenerationConfigurationDAO;
	}

	public void setTaxonomyComparisonConfigurationDAO(
			TaxonomyComparisonConfigurationDAO taxonomyComparisonConfigurationDAO) {
		this.taxonomyComparisonConfigurationDAO = taxonomyComparisonConfigurationDAO;
	}

	public void setVisualizationConfigurationDAO(
			VisualizationConfigurationDAO visualizationConfigurationDAO) {
		this.visualizationConfigurationDAO = visualizationConfigurationDAO;
	}
	
	public void setMatrixGenerationConfigurationDAO(
			MatrixGenerationConfigurationDAO matrixGenerationConfigurationDAO) {
		this.matrixGenerationConfigurationDAO = matrixGenerationConfigurationDAO;
	}

	public AbstractTaskConfiguration getTaskConfiguration(Configuration configuration) {
		AbstractTaskConfiguration taskConfiguration = 
				semanticMarkupConfigurationDAO.getSemanticMarkupConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration = matrixGenerationConfigurationDAO.getMatrixGenerationConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration =	treeGenerationConfigurationDAO.getTreeGenerationConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration =	taxonomyComparisonConfigurationDAO.getTaxonomyComparisonConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration =	visualizationConfigurationDAO.getVisualizationConfiguration(configuration.getId());		
		return taskConfiguration;
	}
	
}
