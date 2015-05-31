package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.oto2.oto.client.event.ImportEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.DownloadEvent;

public class SemanticMarkupToOntologiesPresenter implements ISemanticMarkupToOntologiesView.Presenter {

	private Task task;
	private ISemanticMarkupToOntologiesView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private EventBus ontologizeEventBus;
	
	@Inject
	public SemanticMarkupToOntologiesPresenter(final ISemanticMarkupToOntologiesView view, 
			final ISemanticMarkupServiceAsync semanticMarkupService,
			PlaceController placeController, 
			final ImportOtoPresenter importOtoPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.ontologizeEventBus = view.getOntologize().getEventBus();
		ontologizeEventBus.addHandler(DownloadEvent.TYPE, new DownloadEvent.Handler() {
			@Override
			public void onDownload(DownloadEvent event) {
				final MessageBox box = Alerter.startLoading();
				semanticMarkupService.downloadOntologize(Authentication.getInstance().getToken(), 
						task, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						Alerter.stopLoading(box);
						Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
								"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToSaveOto(caught);
					}
				});
			}
		});
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
	}

	@Override
	public void setTask(Task task) {
		SemanticMarkupToOntologiesPresenter.this.task = task;
		SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
		if(configuration.getOntologizeUploadId() == 0) {
			final MessageBox box = Alerter.startLoading();
			semanticMarkupService.ontologize(Authentication.getInstance().getToken(), 
					task, new AsyncCallback<Task>() {
				@Override
				public void onSuccess(Task task) {
					SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
					view.setOntologize(configuration.getOntologizeUploadId(), configuration.getOntologizeSecret());
					Alerter.stopLoading(box);
					SemanticMarkupToOntologiesPresenter.this.task = task;
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToOntologize(caught);
					Alerter.stopLoading(box);
				}
			});
		} else {
			view.setOntologize(configuration.getOntologizeUploadId(), 	configuration.getOntologizeSecret());
		}
	}

	@Override
	public ISemanticMarkupToOntologiesView getView() {
		return view;
	}

	@Override
	public void onNext() {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.OUTPUT, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new SemanticMarkupOutputPlace(task));
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGoToTaskStage(caught);
			}
		});
	}
}
