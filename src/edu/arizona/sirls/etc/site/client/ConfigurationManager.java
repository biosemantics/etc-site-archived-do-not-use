package edu.arizona.sirls.etc.site.client;

import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;

public class ConfigurationManager {

	private static ConfigurationManager instance;

	public static ConfigurationManager getInstance() {
		if(instance == null)
			instance = new ConfigurationManager();
		return instance;
	}
	
	private ConfigurationManager() { }
	
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
	private TreeGenerationConfiguration treeGenerationConfiguration;
	private TaxonomyComparisonConfiguration taxonomyComparisonConfiguration;
	private VisualizationConfiguration visualizationConfiguration;
	

	public boolean hasMatrixGenerationConfiguration() {
		return this.matrixGenerationConfiguration != null;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration() {
		return matrixGenerationConfiguration;
	}

	public void setMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
	}

	public void newMatrixGenerationConfiguration() {
		matrixGenerationConfiguration = new MatrixGenerationConfiguration();
	}

	public void removeMatrixGenerationConfiguration() {
		this.matrixGenerationConfiguration = null;
	}

	public TreeGenerationConfiguration getTreeGenerationConfiguration() {
		return this.treeGenerationConfiguration;
	}

	public TaxonomyComparisonConfiguration getTaxonomyComparisonConfiguration() {
		return this.taxonomyComparisonConfiguration;
	}
	
	public VisualizationConfiguration getVisualizationConfiguration() {
		return this.visualizationConfiguration;
	}
	
	
}
