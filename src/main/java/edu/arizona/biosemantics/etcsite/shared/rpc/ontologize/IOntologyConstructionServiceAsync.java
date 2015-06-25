package edu.arizona.biosemantics.etcsite.shared.rpc.ontologize;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;

public interface IOntologyConstructionServiceAsync extends IHasTasksServiceAsync {

	void ontologize(AuthenticationToken token, Task task, AsyncCallback<Task> callback)
			throws SemanticMarkupException;

	void downloadOntologize(AuthenticationToken token, Task task, AsyncCallback<String> callback)
			throws SemanticMarkupException;
}
