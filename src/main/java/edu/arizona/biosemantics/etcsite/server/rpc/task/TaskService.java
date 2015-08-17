package edu.arizona.biosemantics.etcsite.server.rpc.task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.ontologize.OntologizeService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.SemanticMarkupService;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.TaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.server.rpc.treegeneration.TreeGenerationService;
import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskService;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationService;

public class TaskService extends RemoteServiceServlet implements ITaskService {

	private static final long serialVersionUID = -3080921351813858330L;
	private IMatrixGenerationService matrixGenerationService;
	private ISemanticMarkupService semanticMarkupService;
	private ITreeGenerationService treeGenerationService;
	private ITaxonomyComparisonService taxonomyComparisonService;
	private IOntologizeService ontologizeService;
	private DAOManager daoManager;
		
	@Inject
	public TaskService(IMatrixGenerationService matrixGenerationService,
			ISemanticMarkupService semanticMarkupService,
			ITreeGenerationService treeGenerationService,
			ITaxonomyComparisonService taxonomyComparisonService,
			IOntologizeService ontologizeService, DAOManager daoManager) {
		super();
		this.matrixGenerationService = matrixGenerationService;
		this.semanticMarkupService = semanticMarkupService;
		this.treeGenerationService = treeGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.ontologizeService = ontologizeService;
		this.daoManager = daoManager;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public List<Task> getAllTasks(AuthenticationToken authenticationToken) {
		return daoManager.getTaskDAO().getAllTasks(authenticationToken.getUserId());
	}
	
	@Override
	public Task getTask(AuthenticationToken authenticationToken, Task task) {		
		return daoManager.getTaskDAO().getTask(task.getId());
	}

	@Override
	public List<Task> getOwnedTasks(AuthenticationToken authenticationToken) {		
		return daoManager.getTaskDAO().getOwnedTasks(authenticationToken.getUserId());
	}

	@Override
	public List<Task> getSharedWithTasks(AuthenticationToken authenticationToken) {		
		return daoManager.getTaskDAO().getSharedWithTasks(authenticationToken.getUserId());
	}

	@Override
	public List<Task> getCompletedTasks(AuthenticationToken authenticationToken) {		
		return daoManager.getTaskDAO().getCompletedTasks(authenticationToken.getUserId());
	}

	@Override
	public Map<Integer, Task> getResumableOrFailedTasks(AuthenticationToken authenticationToken) {		
		log(LogLevel.DEBUG, "Get Resumable or Failed Tasks");
		Map<Integer, Task> result = new LinkedHashMap<Integer, Task>();
		for(Task task : daoManager.getTaskDAO().getResumableTasks(authenticationToken.getUserId()))
			result.put(task.getId(), task);
		for(Task task : daoManager.getTaskDAO().getFailedAndIncompletedTasks(authenticationToken.getUserId())) {
			result.put(task.getId(), task);
			//only report failed tasks once to client
			task.setComplete(true);
			daoManager.getTaskDAO().updateTask(task);
		}
		return result;	
	}

	@Override
	public boolean isResumable(AuthenticationToken authenticationToken, Task task) {	
		return daoManager.getTaskDAO().getTask(task.getId()).isResumable();
	}

	@Override
	public boolean isComplete(AuthenticationToken authenticationToken, Task task) {	
		return daoManager.getTaskDAO().getTask(task.getId()).isComplete();
	}

	@Override
	public boolean hasResumable(AuthenticationToken authenticationToken) {		
		return daoManager.getTaskDAO().getResumableTasks(authenticationToken.getUserId()).isEmpty();
	}


	/*
	@Override
	public void cancelTask(AuthenticationToken authenticationToken, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				switch(task.getTaskStage().getTaskType().getTaskTypeEnum()) {
				case MATRIX_GENERATION:
					MatrixGenerationConfigurationDAO.getInstance().remove(matrixGenerationService.getMatrixGenerationConfiguration(authenticationToken, task));
					break;
				case TAXONOMY_COMPARISON:
					TaxonomyComparisonConfigurationDAO.getInstance().remove(matrixGenerationService.getMatrixGenerationConfiguration(authenticationToken, task));
					break;
				case TREE_GENERATION:
					TreeGenerationConfigurationDAO.getInstance().remove(matrixGenerationService.getMatrixGenerationConfiguration(authenticationToken, task));
					break;
				case VISUALIZATION:
					VisualizationConfigurationDAO.getInstance().remove(matrixGenerationService.getMatrixGenerationConfiguration(authenticationToken, task));
					break;
				default:
					break;
				}

				daoManager.getTaskDAO().removeTask(task);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	*/
	


	/*@Override
	public Task getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType) {
		Task result = null;
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			try {
				result = daoManager.getTaskDAO().getLatestResumableTask(authenticationToken.getUsername(), taskType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}*/
	
	@Override
	public void cancelTask(AuthenticationToken authenticationToken, Task task) throws Exception {		
		switch(task.getTaskType().getTaskTypeEnum()) {
		case ONTOLOGIZE:
			ontologizeService.cancel(authenticationToken, task);
			break;
		case MATRIX_GENERATION:
			matrixGenerationService.cancel(authenticationToken, task);
			break;
		case SEMANTIC_MARKUP:
			semanticMarkupService.cancel(authenticationToken, task);
			break;
		case TAXONOMY_COMPARISON:
			taxonomyComparisonService.cancel(authenticationToken, task);
			break;
		case TREE_GENERATION:
			treeGenerationService.cancel(authenticationToken, task);
			break;
		case VISUALIZATION:
			break;
		default:
			break;
		}
	}

	@Override
	public Share addShare(AuthenticationToken authenticationToken, Share share) {
		return daoManager.getShareDAO().addShare(share);
	}

	@Override
	public List<Share> getOwnedShares(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		return  daoManager.getShareDAO().getSharesOfOwner(user);
	}

	@Override
	public List<Share> getInvitedShares(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		return daoManager.getShareDAO().getSharesOfInvitee(user);
	}

	@Override
	public Set<ShortUser> getInvitees(AuthenticationToken authenticationToken, Task task) {
		return daoManager.getShareDAO().getInvitees(task);
	}

	@Override
	public Share addOrUpdateShare(AuthenticationToken authenticationToken, Share share) {
		return daoManager.getShareDAO().addOrUpdateShare(share);
	}
	
	@Override
	public Share updateShare(AuthenticationToken authenticationToken, Share share) {
		return daoManager.getShareDAO().updateShare(share);
	}

	@Override
	public void removeMeFromShare(AuthenticationToken authenticationToken, Task task) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Share> shares = daoManager.getShareDAO().getSharesOfInviteeForTask(user, task);
		for(Share share : shares) {
			share.getInvitees().remove(user);
			this.updateShare(authenticationToken, share);
		}
	}

	@Override
	public Map<Task, Set<ShortUser>> getInviteesForOwnedTasks(AuthenticationToken authenticationToken) {	
		Map<Task, Set<ShortUser>> result = new HashMap<Task, Set<ShortUser>>();
		List<Task> ownedTasks = this.getOwnedTasks(authenticationToken);
		for(Task task : ownedTasks) {
			Set<ShortUser> invitees = this.getInvitees(authenticationToken, task);
			result.put(task, invitees);
		}
		return result;
	}
}
