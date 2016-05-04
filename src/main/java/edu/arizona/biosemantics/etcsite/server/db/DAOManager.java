package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.util.List;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class DAOManager {

	private ConfigurationDAO configurationDAO;
	private SemanticMarkupDBDAO semanticMarkupDBDAO;
	private OntologizeConfigurationDAO ontologizeConfigurationDAO;
	private FilesInUseDAO filesInUseDAO;
	private FileTypeDAO fileTypeDAO;
	private TaxonGroupDAO taxonGroupDAO;
	private MatrixGenerationConfigurationDAO matrixGenerationConfigurationDAO;
	private PasswordResetRequestDAO passwordResetRequestDAO;
	private SemanticMarkupConfigurationDAO semanticMarkupConfigurationDAO;
	private ShareDAO shareDAO;
	private TaskConfigurationDAO taskConfigurationDAO;
	private TaskDAO taskDAO;
	private TasksOutputFilesDAO tasksOutputFilesDAO;
	private TaskStageDAO taskStageDAO;
	private TaskTypeDAO taskTypeDAO;
	private TaxonomyComparisonConfigurationDAO taxonomyComparisonConfigurationDAO;
	private TreeGenerationConfigurationDAO treeGenerationConfigurationDAO;
	private UserDAO userDAO;
	private VisualizationConfigurationDAO visualizationConfigurationDAO;
	private CaptchaDAO captchaDAO;
	
	@Inject
	public DAOManager() {
		configurationDAO = new ConfigurationDAO();
		userDAO = new UserDAO();
		semanticMarkupDBDAO = new SemanticMarkupDBDAO();
		taxonGroupDAO = new TaxonGroupDAO();
		passwordResetRequestDAO = new PasswordResetRequestDAO();
		tasksOutputFilesDAO = new TasksOutputFilesDAO();
		fileTypeDAO = new FileTypeDAO();
		taxonomyComparisonConfigurationDAO = new TaxonomyComparisonConfigurationDAO();
		treeGenerationConfigurationDAO = new TreeGenerationConfigurationDAO();
		visualizationConfigurationDAO = new VisualizationConfigurationDAO();
		ontologizeConfigurationDAO = new OntologizeConfigurationDAO();
		captchaDAO = new CaptchaDAO();
		
		taskTypeDAO = new TaskTypeDAO();
		taskStageDAO = new TaskStageDAO();
		taskDAO = new TaskDAO();
		shareDAO = new ShareDAO();
		filesInUseDAO = new FilesInUseDAO();
		matrixGenerationConfigurationDAO = new MatrixGenerationConfigurationDAO();
		semanticMarkupConfigurationDAO = new SemanticMarkupConfigurationDAO();
		taskConfigurationDAO = new TaskConfigurationDAO();
		
		taskTypeDAO.setFileTypeDAO(fileTypeDAO);
		taskStageDAO.setTaskTypeDAO(taskTypeDAO);
		taskDAO.setConfigurationDAO(configurationDAO);
		taskDAO.setFilesInUseDAO(filesInUseDAO);
		taskDAO.setMatrixGenerationConfigurationDAO(matrixGenerationConfigurationDAO);
		taskDAO.setSemanticMarkupConfigurationDAO(semanticMarkupConfigurationDAO);
		taskDAO.setTaxonomyComparisonConfigurationDAO(taxonomyComparisonConfigurationDAO);
		taskDAO.setOntologizeConfigurationDAO(ontologizeConfigurationDAO);
		taskDAO.setShareDAO(shareDAO);
		taskDAO.setTasksOutputFilesDAO(tasksOutputFilesDAO);
		taskDAO.setTaskStageDAO(taskStageDAO);
		taskDAO.setTaskTypeDAO(taskTypeDAO);
		taskDAO.setUserDAO(userDAO);
		shareDAO.setTaskDAO(taskDAO);
		shareDAO.setUserDAO(userDAO);
		filesInUseDAO.setTaskDAO(taskDAO);
		matrixGenerationConfigurationDAO.setConfigurationDAO(configurationDAO);
		matrixGenerationConfigurationDAO.setTaxonGroupDAO(taxonGroupDAO);
		semanticMarkupConfigurationDAO.setConfigurationDAO(configurationDAO);
		semanticMarkupConfigurationDAO.setTaxonGroupDAO(taxonGroupDAO);
		taskConfigurationDAO.setSemanticMarkupConfigurationDAO(semanticMarkupConfigurationDAO);
		taskConfigurationDAO.setTaxonomyComparisonConfigurationDAO(taxonomyComparisonConfigurationDAO);
		taskConfigurationDAO.setTreeGenerationConfigurationDAO(treeGenerationConfigurationDAO);
		taskConfigurationDAO.setVisualizationConfigurationDAO(visualizationConfigurationDAO);
		taskConfigurationDAO.setMatrixGenerationConfigurationDAO(matrixGenerationConfigurationDAO);
		treeGenerationConfigurationDAO.setConfigurationDAO(configurationDAO);
		taskDAO.setTreeGenerationConfigurationDAO(treeGenerationConfigurationDAO);
		taxonomyComparisonConfigurationDAO.setConfigurationDAO(configurationDAO);
		taxonomyComparisonConfigurationDAO.setTaxonGroupDAO(taxonGroupDAO);
		ontologizeConfigurationDAO.setConfigurationDAO(configurationDAO);
		ontologizeConfigurationDAO.setTaxonGroupDAO(taxonGroupDAO);
	}

	public ConfigurationDAO getConfigurationDAO() {
		return configurationDAO;
	}

	public SemanticMarkupDBDAO getSemanticMarkupDBDAO() {
		return semanticMarkupDBDAO;
	}

	public FilesInUseDAO getFilesInUseDAO() {
		return filesInUseDAO;
	}

	public FileTypeDAO getFileTypeDAO() {
		return fileTypeDAO;
	}

	public TaxonGroupDAO getTaxonGroupDAO() {
		return taxonGroupDAO;
	}

	public MatrixGenerationConfigurationDAO getMatrixGenerationConfigurationDAO() {
		return matrixGenerationConfigurationDAO;
	}

	public PasswordResetRequestDAO getPasswordResetRequestDAO() {
		return passwordResetRequestDAO;
	}

	public SemanticMarkupConfigurationDAO getSemanticMarkupConfigurationDAO() {
		return semanticMarkupConfigurationDAO;
	}

	public ShareDAO getShareDAO() {
		return shareDAO;
	}

	public TaskConfigurationDAO getTaskConfigurationDAO() {
		return taskConfigurationDAO;
	}

	public TaskDAO getTaskDAO() {
		return taskDAO;
	}

	public TasksOutputFilesDAO getTasksOutputFilesDAO() {
		return tasksOutputFilesDAO;
	}

	public TaskStageDAO getTaskStageDAO() {
		return taskStageDAO;
	}

	public TaskTypeDAO getTaskTypeDAO() {
		return taskTypeDAO;
	}

	public TaxonomyComparisonConfigurationDAO getTaxonomyComparisonConfigurationDAO() {
		return taxonomyComparisonConfigurationDAO;
	}

	public TreeGenerationConfigurationDAO getTreeGenerationConfigurationDAO() {
		return treeGenerationConfigurationDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public VisualizationConfigurationDAO getVisualizationConfigurationDAO() {
		return visualizationConfigurationDAO;
	}

	public CaptchaDAO getCaptchaDAO() {
		return captchaDAO;
	}

	public OntologizeConfigurationDAO getOntologizeConfigurationDAO() {
		return ontologizeConfigurationDAO;
	}
}