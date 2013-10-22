package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.TreeGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;

public class TreeGenerationService extends RemoteServiceServlet implements ITreeGenerationService {

	@Override
	public TreeGenerationTaskRun getTreeGenerationTask(AuthenticationToken authenticationToken, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

}
