package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationService;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;

public class VisualizationService extends RemoteServiceServlet implements IVisualizationService {

	@Override
	public VisualizationConfiguration getVisualizationConfiguration(AuthenticationToken authenticationToken, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

}
