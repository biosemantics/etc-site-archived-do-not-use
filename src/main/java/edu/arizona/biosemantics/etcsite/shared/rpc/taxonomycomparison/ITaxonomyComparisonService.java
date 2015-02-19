package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;

@RemoteServiceRelativePath("taxonomyComparison")
public interface ITaxonomyComparisonService extends RemoteService, IHasTasksService {

	public Task start(AuthenticationToken authenticationToken, String taskName, String input)
			throws TaxonomyComparisonException;
	
	public boolean isValidInput(AuthenticationToken token, String inputFile);

	public Model getInput(AuthenticationToken token, Task task) throws TaxonomyComparisonException;
	
	public Task runMirGeneration(AuthenticationToken token, Task task, Model model) throws TaxonomyComparisonException;

	public String getInputVisualization(AuthenticationToken token, Task task, Model model) throws TaxonomyComparisonException;

	public MIRGenerationResult getMirGenerationResult(AuthenticationToken token, Task task);
	
}
