package edu.arizona.biosemantics.etcsite.server.db;

public class DAOManager {

	private ConfigurationDAO configurationDAO;
	private DatasetPrefixDAO datasetPrefixDAO;
	private FilesInUseDAO filesInUseDAO;
	private FileTypeDAO fileTypeDAO;
	private GlossaryDAO glossaryDAO;
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
	
	public DAOManager() {
		configurationDAO = new ConfigurationDAO();
		userDAO = new UserDAO();
		datasetPrefixDAO = new DatasetPrefixDAO();
		glossaryDAO = new GlossaryDAO();
		passwordResetRequestDAO = new PasswordResetRequestDAO();
		tasksOutputFilesDAO = new TasksOutputFilesDAO();
		fileTypeDAO = new FileTypeDAO();
		taxonomyComparisonConfigurationDAO = new TaxonomyComparisonConfigurationDAO();
		treeGenerationConfigurationDAO = new TreeGenerationConfigurationDAO();
		visualizationConfigurationDAO = new VisualizationConfigurationDAO();
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
		taskDAO.setShareDAO(shareDAO);
		taskDAO.setTasksOutputFilesDAO(tasksOutputFilesDAO);
		taskDAO.setTaskStageDAO(taskStageDAO);
		taskDAO.setTaskTypeDAO(taskTypeDAO);
		taskDAO.setUserDAO(userDAO);
		shareDAO.setTaskDAO(taskDAO);
		shareDAO.setUserDAO(userDAO);
		filesInUseDAO.setTaskDAO(taskDAO);
		matrixGenerationConfigurationDAO.setConfigurationDAO(configurationDAO);
		semanticMarkupConfigurationDAO.setConfigurationDAO(configurationDAO);
		semanticMarkupConfigurationDAO.setGlossaryDAO(glossaryDAO);
		taskConfigurationDAO.setSemanticMarkupConfigurationDAO(semanticMarkupConfigurationDAO);
		taskConfigurationDAO.setTaxonomyComparisonConfigurationDAO(taxonomyComparisonConfigurationDAO);
		taskConfigurationDAO.setTreeGenerationConfigurationDAO(treeGenerationConfigurationDAO);
		taskConfigurationDAO.setVisualizationConfigurationDAO(visualizationConfigurationDAO);
		taskConfigurationDAO.setMatrixGenerationConfigurationDAO(matrixGenerationConfigurationDAO);
	}

	public ConfigurationDAO getConfigurationDAO() {
		return configurationDAO;
	}

	public DatasetPrefixDAO getDatasetPrefixDAO() {
		return datasetPrefixDAO;
	}

	public FilesInUseDAO getFilesInUseDAO() {
		return filesInUseDAO;
	}

	public FileTypeDAO getFileTypeDAO() {
		return fileTypeDAO;
	}

	public GlossaryDAO getGlossaryDAO() {
		return glossaryDAO;
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
}