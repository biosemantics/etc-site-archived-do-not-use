package edu.arizona.sirls.etc.site.server.rpc;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Share;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShareDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.User;
import edu.arizona.sirls.etc.site.shared.rpc.db.UserDAO;

public class TaskService extends RemoteServiceServlet implements ITaskService {

	private static final long serialVersionUID = -3080921351813858330L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public List<Task> getAllTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				result = TaskDAO.getInstance().getUsersTasks(authenticationToken.getUsername());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<Task> getCreatedTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				result = TaskDAO.getInstance().getUsersTasks(authenticationToken.getUsername());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<Task> getSharedTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		return result;
	}

	@Override
	public Task addTask(AuthenticationToken authenticationToken, Task task) {
		Task result = null;
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				result = TaskDAO.getInstance().addTask(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<Task> getPastTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				result = TaskDAO.getInstance().getUsersPastTasks(authenticationToken.getUsername());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public boolean isResumable(AuthenticationToken authenticationToken, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				return TaskDAO.getInstance().getTask(task.getId()).isResumable();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean isComplete(AuthenticationToken authenticationToken, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				return TaskDAO.getInstance().getTask(task.getId()).isComplete();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean hasResumable(AuthenticationToken authenticationToken) {
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				return !this.getResumableTasks(authenticationToken).isEmpty();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Map<Integer, Task> getResumableTasks(AuthenticationToken authenticationToken) {
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				Map<Integer, Task> result = new LinkedHashMap<Integer, Task>();
				for(Task task : TaskDAO.getInstance().getUsersResumableTasks(authenticationToken.getUsername()))
					result.put(task.getId(), task);
				return result;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
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
	public RPCResult<Share> addShare(AuthenticationToken authenticationToken, Share share) {
		RPCResult<Share> result = new RPCResult<Share>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			try {
				Share shareResult = ShareDAO.getInstance().addShare(share);
				result = new RPCResult<Share>(true, shareResult);
			} catch (Exception e) {
				e.printStackTrace();
				result = new RPCResult<Share>(false, "Internal Server Error");
			}
		}
		return result;
	}

	@Override
	public RPCResult<List<Share>> getOwnedShares(AuthenticationToken authenticationToken) {
		RPCResult<List<Share>> result = new RPCResult<List<Share>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			try {
				ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
				List<Share> sharesResult = ShareDAO.getInstance().getSharesOfOwner(user);
				result = new RPCResult<List<Share>>(true, sharesResult);
			} catch (Exception e) {
				e.printStackTrace();
				result = new RPCResult<List<Share>>(false, "Internal Server Error");
			}
		}
		return result;
	}

	@Override
	public RPCResult<List<Share>> getInvitedShares(AuthenticationToken authenticationToken) {
		RPCResult<List<Share>> result = new RPCResult<List<Share>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			try {
				ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
				List<Share> sharesResult = ShareDAO.getInstance().getSharesOfInvitee(user);
				result = new RPCResult<List<Share>>(true, sharesResult);
			} catch (Exception e) {
				e.printStackTrace();
				result = new RPCResult<List<Share>>(false, "Internal Server Error");
			}
		}
		return result;
	}

}
