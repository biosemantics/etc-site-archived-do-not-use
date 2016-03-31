package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.HasTaskException;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.euler.alignment.shared.model.Articulation;
import edu.arizona.biosemantics.euler.alignment.shared.model.Collection;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;

public interface ITaxonomyComparisonServiceAsync extends IHasTasksServiceAsync {
	
	public void runPossibleWorldGeneration(AuthenticationToken token, Task task,
			Collection collection, AsyncCallback<Task> callback);

	public void getInputVisualization(AuthenticationToken token, Task task,
			Collection collection, AsyncCallback<String> callback);

	public void getPossibleWorldGenerationResult(AuthenticationToken token, Task task, AsyncCallback<PossibleWorldGenerationResult> callback);

	public void goToTaskStage(AuthenticationToken token, Task task,
			TaskStageEnum taskStageEnum, AsyncCallback<Task> callback);
	

	public void getCollection(AuthenticationToken token, Task task, AsyncCallback<Collection> callback);

	public void saveCollection(AuthenticationToken token, Task task, Collection collection, AsyncCallback<Void> callback);

	public void exportArticulations(AuthenticationToken token, Task task, Collection collection, AsyncCallback<String> callback);
	
	public void getTaxonomies(AuthenticationToken token, FolderTreeItem folder, AsyncCallback<List<String>> callback);

	public void getMachineArticulations(AuthenticationToken token, Task task, Collection collection, double threshold,
			AsyncCallback<List<Articulation>> callback);

	public void startFromCleantax(AuthenticationToken token, String taskName, String input, String taxonGroup, 
			String ontology, String termReview1, String termReview2, AsyncCallback<Task> callback);

	public void startFromSerializedModels(AuthenticationToken token,
			String taskName, String inputFolderPath1, String inputFolderPath2, String modelAuthor1, String modelYear1, String modelAuthor2, 
			String modelYear2, String taxonGroup, String ontology, String termReview1, String termReview2, 
			AsyncCallback<Task> callback);

	public void isValidCleanTaxInput(AuthenticationToken token,
			String inputFolderPath, AsyncCallback<Boolean> callback);

	public void isValidModelInput(AuthenticationToken token,
			String inputFolderPath, AsyncCallback<Boolean> callback);
	
	public void isConsistentInput(AuthenticationToken token, Task task, Collection collection, AsyncCallback<Boolean> callback);
	
	public void stopPossibleWorldGeneration(AuthenticationToken token, Task task, AsyncCallback<Void> callback);

}
