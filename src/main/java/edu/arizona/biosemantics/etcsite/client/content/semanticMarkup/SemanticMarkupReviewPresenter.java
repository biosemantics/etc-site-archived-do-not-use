package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.db.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;

public class SemanticMarkupReviewPresenter implements ISemanticMarkupReviewView.Presenter {

	private Task task;
	private ISemanticMarkupReviewView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	
	@Inject
	public SemanticMarkupReviewPresenter(ISemanticMarkupReviewView view, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			PlaceController placeController) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		initIFrameMessaging();
	}

	@Override
	public void setTask(Task task) {
		semanticMarkupService.review(Authentication.getInstance().getToken(), 
				task, new RPCCallback<Task>() {
			@Override
			public void onResult(Task task) {
				SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
				view.setFrameUrl(ServerSetup.getInstance().getSetup().getOtoLiteURL() + "&uploadID=" + configuration.getOtoUploadId() + 
						"&secret=" + configuration.getOtoSecret());
				SemanticMarkupReviewPresenter.this.task = task;
			}
		});
	}

	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onNext() {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.PARSE_TEXT, new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {
				placeController.goTo(new SemanticMarkupParsePlace(task));
			}
		});
	}

	public native void initIFrameMessaging() /*-{
		var thisPresenterReference = this;
		$wnd.onmessage = function(e) {
		    if (e.data == 'done') {
		    	//if simply this is used here, the reference in javascript will then be the $wnd object rather than the java 'this'. 
		    	//Therefore the reference is passed above already
		        thisPresenterReference.@edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPresenter::onNext()();
		    }
		}
	}-*/;
}
