package edu.arizona.biosemantics.etcsite.shared.rpc.ontologize;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;

@RemoteServiceRelativePath("ontologize")
public interface IOntologizeService extends RemoteService, IHasTasksService {

	Collection build(AuthenticationToken token, Task task)
			throws OntologizeException;

	String downloadOntologize(AuthenticationToken token, Task task)
			throws Exception;

	Task startWithOntologySelection(AuthenticationToken token, String taskName, String inputFile, String taxonGroup, 
			String ontologyFile) throws OntologizeException;

	boolean isValidInput(AuthenticationToken token, String inputFolderPath) throws OntologizeException;
	
	Task goToTaskStage(AuthenticationToken token, Task task, TaskStageEnum taskStageEnum) throws OntologizeException;

	Task startWithOntologyCreation(AuthenticationToken token, String taskName, String input,
			String taxonGroup, String ontologyPrefix)
			throws OntologizeException;

	boolean isValidOntology(AuthenticationToken authenticationToken, String ontologyPath);
	
	Task output(AuthenticationToken token, Task task) throws Exception;
	
	void addInput(AuthenticationToken token, Task task, String inputFile) throws OntologizeException;
	
}
