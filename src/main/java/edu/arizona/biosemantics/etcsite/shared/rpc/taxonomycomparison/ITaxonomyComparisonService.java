package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.HasTaskException;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.euler.alignment.shared.model.Articulation;
import edu.arizona.biosemantics.euler.alignment.shared.model.Collection;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;

@RemoteServiceRelativePath("taxonomyComparison")
public interface ITaxonomyComparisonService extends RemoteService, IHasTasksService {
	
	public Task runPossibleWorldGeneration(AuthenticationToken token, Task task, Collection collection) throws HasTaskException;

	public String getInputVisualization(AuthenticationToken token, Task task, Collection collection) throws HasTaskException;

	public PossibleWorldGenerationResult getPossibleWorldGenerationResult(AuthenticationToken token, Task task);
	
	public Task goToTaskStage(AuthenticationToken token, Task task, TaskStageEnum taskStageEnum);

	public Collection getCollection(AuthenticationToken token, Task task)
			throws HasTaskException;

	public void saveCollection(AuthenticationToken token, Task task, Collection collection)
			throws HasTaskException;
	
	public String exportArticulations(AuthenticationToken token, Task task, Collection collection) 
			throws HasTaskException;

	public List<String> getTaxonomies(AuthenticationToken token, FolderTreeItem folder) throws Exception;
	
	public List<Articulation> getMachineArticulations(AuthenticationToken token, Task task, Collection collection, double threshold) throws Exception;
	
	public Task startFromCleantax(AuthenticationToken token, String taskName, String input, String taxonGroup, 
			String ontology, String termReview1, String termReview2) throws HasTaskException;
	
	public Task startFromSerializedModels(AuthenticationToken token,
			String taskName, String inputFolderPath1, String inputFolderPath2, String modelAuthor1, String modelYear1, String modelAuthor2, 
			String modelYear2, String taxonGroup, String ontology, String termReview1, String termReview2) throws HasTaskException;

	public Boolean isValidCleanTaxInput(AuthenticationToken token, String inputFolderPath);

	public Boolean isValidModelInput(AuthenticationToken token,	String inputFolderPath);

	public boolean isConsistentInput(AuthenticationToken token, Task task, Collection collection) throws TaxonomyComparisonException;

	public void stopPossibleWorldGeneration(AuthenticationToken token, Task task) throws TaxonomyComparisonException;
		
}
