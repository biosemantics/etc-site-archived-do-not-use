package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("semanticMarkup")
public interface ISemanticMarkupService extends RemoteService, IHasTasksService {

	public Task start(AuthenticationToken authenticationToken, String taskName, String filePath, String taxonGroup, boolean emptyGlossary) throws SemanticMarkupException;
	
	public String checkValidInput(AuthenticationToken authenticationToken, String filePath) throws SemanticMarkupException;
	
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public LearnInvocation learn(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;

	public Task review(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;
	
	public void parse(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;
	
	public Task output(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;

	public Task goToTaskStage(AuthenticationToken authenticationToken, Task semanticMarkupTask, TaskStageEnum taskStage);

	public List<Description> getDescriptions(AuthenticationToken authenticationToken, String filePath);

	public Description getDescription(AuthenticationToken token, String filePath, int descriptionNumber);
	
	public void setDescription(AuthenticationToken authenticationToken, String filePath, int descriptionNumber, String description) throws SemanticMarkupException;
	
	public String saveOto(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException;
	
	public void renameTerm(AuthenticationToken token, Task task, String term, String newName);

	public void importOto(Task task, String termCategorization, String synonymy) throws SemanticMarkupException;
	
	public void sendToOto(AuthenticationToken token, Task task) throws Exception;

	public boolean isLargeInput(AuthenticationToken token, String inputFile);
	
}
