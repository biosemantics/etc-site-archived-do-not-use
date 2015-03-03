package edu.arizona.biosemantics.etcsite.shared.rpc.visualization;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;

@RemoteServiceRelativePath("visualization")
public interface IVisualizationService extends RemoteService, IHasTasksService {


}
