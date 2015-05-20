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
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;

public class SemanticMarkupReviewPresenter implements ISemanticMarkupReviewView.Presenter {

	private Task task;
	private ISemanticMarkupReviewView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IFileServiceAsync fileService;
	private EventBus otoEventBus;
	
	@Inject
	public SemanticMarkupReviewPresenter(final ISemanticMarkupReviewView view, 
			final ISemanticMarkupServiceAsync semanticMarkupService,
			IFileServiceAsync fileService,
			PlaceController placeController, 
			final ImportOtoPresenter importOtoPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.otoEventBus = view.getOto().getEventBus();
		otoEventBus.addHandler(SaveEvent.TYPE, new SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				final MessageBox box = Alerter.startLoading();
				semanticMarkupService.saveOto(Authentication.getInstance().getToken(), 
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
		otoEventBus.addHandler(ImportEvent.TYPE, new ImportEvent.ImportHandler() {
			@Override
			public void onImport(ImportEvent event) {
				MessageBox confirm = Alerter.confirmOtoImport();
				confirm.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						importOtoPresenter.setTask(task);
						importOtoPresenter.setOto(view.getOto());
						importOtoPresenter.show();
					}	
				});
			}
		});
		otoEventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				semanticMarkupService.renameTerm(Authentication.getInstance().getToken(), 
						task, event.getOldName(), event.getNewName(), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								// don't really want to report issues here to user
							}
							@Override
							public void onSuccess(Void result) {
								// don't really want to report susccess here to user
							}
				});
			}
		});
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.fileService = fileService;
	}

	@Override
	public void setTask(Task task) {
		semanticMarkupService.review(Authentication.getInstance().getToken(), 
				task, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task task) {
				SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
				view.setReview(configuration.getOtoUploadId(), 
						configuration.getOtoSecret());
				view.setEnabledSendToOto(!configuration.isOtoCreatedDataset());
				SemanticMarkupReviewPresenter.this.task = task;
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToReview(caught);
			}
		});
	}

	@Override
	public ISemanticMarkupReviewView getView() {
		return view;
	}

	@Override
	public void onNext() {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.PARSE_TEXT, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new SemanticMarkupParsePlace(task));
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGoToTaskStage(caught);
			}
		});
	}

	@Override
	public void onSendToOto() {
		final MessageBox messageBox = Alerter.startLoading();
		semanticMarkupService.sendToOto(Authentication.getInstance().getToken(), task, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSendToOto(caught);
				Alerter.stopLoading(messageBox);
			}
			@Override
			public void onSuccess(Void result) {
				Alerter.contributedSuccessfullyToOTO();
				Alerter.stopLoading(messageBox);
			}
		});
	}
}
