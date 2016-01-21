package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderTreeItem;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;

@RemoteServiceRelativePath("taxonomyComparison")
public interface ITaxonomyComparisonService extends RemoteService, IHasTasksService {

	public Task start(AuthenticationToken authenticationToken, String taskName, String input)
			throws TaxonomyComparisonException;
	
	public boolean isValidInput(AuthenticationToken token, String inputFile);
	
	public Task runMirGeneration(AuthenticationToken token, Task task, Model model) throws TaxonomyComparisonException;

	public String getInputVisualization(AuthenticationToken token, Task task, Model model) throws TaxonomyComparisonException;

	public RunOutput getMirGenerationResult(AuthenticationToken token, Task task);
	
	public Task goToTaskStage(AuthenticationToken token, Task task, TaskStageEnum taskStageEnum);

	public Model getModel(AuthenticationToken token, Task task)
			throws TaxonomyComparisonException;

	public void saveModel(AuthenticationToken token, Task task, Model model)
			throws TaxonomyComparisonException;
	
	public String exportArticulations(AuthenticationToken token, Task task, Model model) 
			throws TaxonomyComparisonException;

	public List<String> getTaxonomies(AuthenticationToken token, FolderTreeItem folder) throws Exception;
	
}
