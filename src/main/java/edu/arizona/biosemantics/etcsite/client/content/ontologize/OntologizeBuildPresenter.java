package edu.arizona.biosemantics.etcsite.client.content.ontologize;

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
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.client.event.DownloadEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;

public class OntologizeBuildPresenter implements IOntologizeBuildView.Presenter {

	private Task task;
	private IOntologizeBuildView view;
	private PlaceController placeController;
	private IOntologizeServiceAsync ontologizeService;
	private EventBus ontologizeEventBus;
	
	@Inject
	public OntologizeBuildPresenter(final IOntologizeBuildView view, 
			final IOntologizeServiceAsync ontologizeService,
			PlaceController placeController) {
		this.view = view;
		view.setPresenter(this);
		this.ontologizeEventBus = view.getOntologize().getEventBus();
		ontologizeEventBus.addHandler(DownloadEvent.TYPE, new DownloadEvent.Handler() {
			@Override
			public void onDownload(DownloadEvent event) {
				//final MessageBox box = Alerter.startLoading();
				/*ontologizeService.downloadOntologize(Authentication.getInstance().getToken(), 
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
				});*/
			}
		});
		this.placeController = placeController;
		this.ontologizeService = ontologizeService;
	}

	@Override
	public void setTask(Task task) {
		OntologizeBuildPresenter.this.task = task;
		final MessageBox box = Alerter.startLoading();
		ontologizeService.build(Authentication.getInstance().getToken(), 
				task, new AsyncCallback<Collection>() {
			@Override
			public void onSuccess(Collection collection) {
				view.setOntologize(collection.getId(), collection.getSecret());
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToOntologize(caught);
				Alerter.stopLoading(box);
			}
		});
	}

	@Override
	public IOntologizeBuildView getView() {
		return view;
	}

	@Override
	public void onNext() {
		ontologizeService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.OUTPUT, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new OntologizeOutputPlace(task));
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGoToTaskStage(caught);
			}
		});
	}
}
