package edu.arizona.biosemantics.etcsite.server.rpc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.db.Share;
import edu.arizona.biosemantics.etcsite.shared.db.ShareDAO;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.db.TaskDAO;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaskService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class TaskService extends RemoteServiceServlet implements ITaskService {

	private static final long serialVersionUID = -3080921351813858330L;
	private IMatrixGenerationService matrixGenerationService = new MatrixGenerationService();
	private ISemanticMarkupService semanticMarkupService = new SemanticMarkupService();
	
	@Override
	public RPCResult<List<Task>> getAllTasks(AuthenticationToken authenticationToken) {		
		try {
			List<Task> result = TaskDAO.getInstance().getAllTasks(authenticationToken.getUsername());
			return new RPCResult<List<Task>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<List<Task>>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> getTask(AuthenticationToken authenticationToken, Task task) {		
		try {
			Task result = TaskDAO.getInstance().getTask(task.getId());
			return new RPCResult<Task>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<List<Task>> getOwnedTasks(AuthenticationToken authenticationToken) {		
		try {
			List<Task> result = TaskDAO.getInstance().getOwnedTasks(authenticationToken.getUsername());
			return new RPCResult<List<Task>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<List<Task>>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<List<Task>> getSharedWithTasks(AuthenticationToken authenticationToken) {		
		try {
			List<Task> result = TaskDAO.getInstance().getSharedWithTasks(authenticationToken.getUsername());
			return new RPCResult<List<Task>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<List<Task>>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<List<Task>> getCompletedTasks(AuthenticationToken authenticationToken) {		
		try {
			List<Task> result = TaskDAO.getInstance().getCompletedTasks(authenticationToken.getUsername());
			return new RPCResult<List<Task>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<List<Task>>(false, "Internal Server Error");
		}
	}
	

	@Override
	public RPCResult<Map<Integer, Task>> getResumableTasks(AuthenticationToken authenticationToken) {		
		try {
			Map<Integer, Task> result = new LinkedHashMap<Integer, Task>();
			for(Task task : TaskDAO.getInstance().getResumableTasks(authenticationToken.getUsername()))
				result.put(task.getId(), task);
			return new RPCResult<Map<Integer, Task>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Map<Integer, Task>>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Boolean> isResumable(AuthenticationToken authenticationToken, Task task) {	
		try {
			Boolean result = TaskDAO.getInstance().getTask(task.getId()).isResumable();
			return new RPCResult<Boolean>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Boolean>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Boolean> isComplete(AuthenticationToken authenticationToken, Task task) {	
		try {
			Boolean result =  TaskDAO.getInstance().getTask(task.getId()).isComplete();
			return new RPCResult<Boolean>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Boolean>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Boolean> hasResumable(AuthenticationToken authenticationToken) {		
		try {
			Boolean result = !TaskDAO.getInstance().getResumableTasks(authenticationToken.getUsername()).isEmpty();
			return new RPCResult<Boolean>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Boolean>(false, "Internal Server Error");
		}
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

				TaskDAO.getInstance().removeTask(task);
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
				result = TaskDAO.getInstance().getLatestResumableTask(authenticationToken.getUsername(), taskType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}*/
	
	@Override
	public RPCResult<Void> cancelTask(AuthenticationToken authenticationToken, Task task) {		
		try {
			switch(task.getTaskType().getTaskTypeEnum()) {
			case MATRIX_GENERATION:
				matrixGenerationService.cancel(authenticationToken, task);
				break;
			case SEMANTIC_MARKUP:
				semanticMarkupService.cancel(authenticationToken, task);
				break;
			case TAXONOMY_COMPARISON:
				break;
			case TREE_GENERATION:
				break;
			case VISUALIZATION:
				break;
			default:
				break;
			}
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Share> addShare(AuthenticationToken authenticationToken, Share share) {
		try {
			Share shareResult = ShareDAO.getInstance().addShare(share);
			return new RPCResult<Share>(true, shareResult);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Share>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<List<Share>> getOwnedShares(AuthenticationToken authenticationToken) {
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Share> sharesResult = ShareDAO.getInstance().getSharesOfOwner(user);
			return new RPCResult<List<Share>>(true, sharesResult);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<List<Share>>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<List<Share>> getInvitedShares(AuthenticationToken authenticationToken) {
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Share> sharesResult = ShareDAO.getInstance().getSharesOfInvitee(user);
			return new RPCResult<List<Share>>(true, sharesResult);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<List<Share>>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Set<ShortUser>> getInvitees(AuthenticationToken authenticationToken, Task task) {
		try {
			Set<ShortUser> invitees = ShareDAO.getInstance().getInvitees(task);
			return new RPCResult<Set<ShortUser>>(true, invitees);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Set<ShortUser>>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Share> addOrUpdateShare(AuthenticationToken authenticationToken, Share share) {
		try {
			Share shareResult = ShareDAO.getInstance().addOrUpdateShare(share);
			return new RPCResult<Share>(true, shareResult);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Share>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Share> updateShare(AuthenticationToken authenticationToken, Share share) {
		try {
			Share shareResult = ShareDAO.getInstance().updateShare(share);
			return new RPCResult<Share>(true, shareResult);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Share>(false, "Internal Server Error");
		}
	}
	

	@Override
	public RPCResult<Void> removeMeFromShare(AuthenticationToken authenticationToken, Task task) {
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Share> shares = ShareDAO.getInstance().getSharesOfInviteeForTask(user, task);
			for(Share share : shares) {
				share.getInvitees().remove(user);
				this.updateShare(authenticationToken, share);
			}
			return new RPCResult<Void>(true);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Map<Task, Set<ShortUser>>> getInviteesForOwnedTasks(AuthenticationToken authenticationToken) {	
		try {
			Map<Task, Set<ShortUser>> result = new HashMap<Task, Set<ShortUser>>();
			RPCResult<List<Task>> ownedTasks = this.getOwnedTasks(authenticationToken);
			if(ownedTasks.isSucceeded())
				for(Task task : ownedTasks.getData()) {
					RPCResult<Set<ShortUser>> invitees = this.getInvitees(authenticationToken, task);
					if(invitees.isSucceeded())
						result.put(task, invitees.getData());
				}
			return new RPCResult<Map<Task, Set<ShortUser>>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Map<Task, Set<ShortUser>>>(false, "Internal Server Error");
		}
	}
}
