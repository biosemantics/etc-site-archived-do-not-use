package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent.SaveHandler;

public class SemanticMarkupReviewPresenter implements ISemanticMarkupReviewView.Presenter {

	private Task task;
	private ISemanticMarkupReviewView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IFileServiceAsync fileService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private SaveHandler otoSaveHandler = new SaveHandler() {
		@Override
		public void onSave(SaveEvent event) {
			loadingPopup.start();
			semanticMarkupService.saveOto(Authentication.getInstance().getToken(), 
					task, new RPCCallback<String>() {
				@Override
				public void onResult(String result) {
					loadingPopup.stop();
					Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
							"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
							"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
				}
			});
		}
	};
	
	@Inject
	public SemanticMarkupReviewPresenter(ISemanticMarkupReviewView view, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IFileServiceAsync fileService,
			PlaceController placeController) {
		this.view = view;
		view.setPresenter(this);
		view.getOto().setSaveHandler(otoSaveHandler);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.fileService = fileService;
		initIFrameMessaging();
	}

	@Override
	public void setTask(Task task) {
		semanticMarkupService.review(Authentication.getInstance().getToken(), 
				task, new RPCCallback<Task>() {
			@Override
			public void onResult(Task task) {
				SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
				view.setReview(configuration.getOtoUploadId(), 
						configuration.getOtoSecret());
				//view.setFrameUrl(ServerSetup.getInstance().getSetup().getOtoLiteReviewURL() + "&uploadID=" + configuration.getOtoUploadId() + 
				//		"&secret=" + configuration.getOtoSecret());
				SemanticMarkupReviewPresenter.this.task = task;
			}
		});
	}

	@Override
	public ISemanticMarkupReviewView getView() {
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
