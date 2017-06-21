package edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService, IHasTasksService {

	public Task start(AuthenticationToken authenticationToken, String taskName, String input, String inputTermReview, String inputOntology,
			String taxonGroup, boolean inheritValues, boolean generateAbsentPresent) throws MatrixGenerationException;
	
	public Task process(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException;
	
	public Model review(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException;
	
	public Task completeReview(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException;
	
	public Task output(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException;
	
	public void save(AuthenticationToken authenticationToken, Model model, Task task) throws MatrixGenerationException;
	
	public Task goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum review);

	public String checkInputValid(AuthenticationToken authenticationToken,
			String filePath);
	
	public String outputMatrix(AuthenticationToken token, Task task, Model model, MatrixFormat format) throws MatrixGenerationException;

	public Model loadMatrixFromProcessOutput(AuthenticationToken token, Task task) throws MatrixGenerationException;

	public void publish(AuthenticationToken token, Task task) throws Exception;

}
