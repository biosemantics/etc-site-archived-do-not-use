package edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.treegeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

@RemoteServiceRelativePath("treeGeneration")
public interface ITreeGenerationService extends RemoteService, IHasTasksService {

	public Boolean isValidInput(AuthenticationToken token, String inputFile);

	public Task start(AuthenticationToken token, String taskName, String inputFile) throws TreeGenerationException;
	
	public TaxonMatrix view(AuthenticationToken authenticationToken, Task task) throws TreeGenerationException;

	public Task goToTaskStage(AuthenticationToken token, Task task, TaskStageEnum view);
	
}
