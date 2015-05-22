package edu.arizona.biosemantics.etcsite.shared.rpc.pipeline;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;

@RemoteServiceRelativePath("pipeline")
public interface IPipelineService extends RemoteService, IHasTasksService {

}
