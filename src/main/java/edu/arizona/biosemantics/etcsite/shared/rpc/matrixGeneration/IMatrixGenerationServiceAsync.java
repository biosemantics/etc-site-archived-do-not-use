package edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public interface IMatrixGenerationServiceAsync extends IHasTasksServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String input, String inputTermReview, String inputOntology,
			String taxonGroup, boolean inheritValues, boolean generateAbsentPresent, AsyncCallback<Task> callback);
	
	public void process(AuthenticationToken authenticationToken,  Task task, AsyncCallback<Task> callback);
	
	public void review(AuthenticationToken authenticationToken, Task task, AsyncCallback<Model> callback);
	
	public void completeReview(AuthenticationToken authenticationToken, Task task, AsyncCallback<Task> callback);
	
	public void output(AuthenticationToken authenticationToken, Task task, AsyncCallback<Task> callback);
	
	public void save(AuthenticationToken authenticationToken, Model model, Task task, AsyncCallback<Void> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum review, AsyncCallback<Task> callback);

	public void checkInputValid(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);

	public void outputMatrix(AuthenticationToken token, Task task, Model model, MatrixFormat format, AsyncCallback<String> callback);
	
	public void loadMatrixFromProcessOutput(AuthenticationToken token, Task task, AsyncCallback<Model> callback);

	public void publish(AuthenticationToken token, Task task,  AsyncCallback<Void> callback);
}
