package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MyWindow;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;
import edu.arizona.biosemantics.oto2.oto.client.event.DownloadEvent.DownloadHandler; 
import edu.arizona.biosemantics.oto2.oto.client.event.DownloadEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.ImportEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class SemanticMarkupReviewPresenter implements ISemanticMarkupReviewView.Presenter {

	private Task task;
	private ISemanticMarkupReviewView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private ICollectionServiceAsync otoCollectionService;
	private IFileServiceAsync fileService;
	private EventBus otoEventBus;
	private IUserServiceAsync userService;
	private Collection collection;
	private Timer saveTimer;
	
	@Inject
	public SemanticMarkupReviewPresenter(final ISemanticMarkupReviewView view, 
			final ISemanticMarkupServiceAsync semanticMarkupService, ICollectionServiceAsync otoCollectionService, 
			final IUserServiceAsync userService,
			IFileServiceAsync fileService,
			PlaceController placeController, 
			final ImportOtoPresenter importOtoPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.otoEventBus = view.getOto().getEventBus();
		otoEventBus.addHandler(LoadEvent.TYPE, new LoadEvent.LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				SemanticMarkupReviewPresenter.this.collection = event.getCollection();
			}
		});
		otoEventBus.addHandler(DownloadEvent.TYPE, new DownloadHandler() {
			@Override
			public void onDownload(DownloadEvent event) {
				final MessageBox box = Alerter.startLoading();
				final MyWindow window = MyWindow.open(null, "_blank", null);
				semanticMarkupService.saveOto(Authentication.getInstance().getToken(), 
						task, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						Alerter.stopLoading(box);
						window.setUrl("download.dld?target=" + URL.encodeQueryString(result) + 
								"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));
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
		this.userService = userService;
		this.otoCollectionService = otoCollectionService;
	}

	private void createSaveTimer() {
		removeSaveTimer();
		saveTimer = new Timer() {
			private ConfirmMessageBox saveTermReviewBox;

			@Override
			public void run() {
				if(saveTermReviewBox == null) {
					saveTermReviewBox = Alerter.confirmSaveTermReview();
					saveTermReviewBox.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							otoEventBus.fireEvent(new SaveEvent());
						}
					});
				}
		    }
		};
		saveTimer.scheduleRepeating(900000);	
	}
	
	private void removeSaveTimer() {
		if(saveTimer != null)
			saveTimer.cancel();
	}
	
	@Override
	public void setTask(final Task task) {
	    final MessageBox box = Alerter.startLoading();
		userService.hasLinkedOTOAccount(Authentication.getInstance().getToken(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToReview(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(final Boolean hasLinkedOTOAccount) {
				semanticMarkupService.review(Authentication.getInstance().getToken(), 
						task, new AsyncCallback<Task>() {
					@Override
					public void onSuccess(final Task task) {
						SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
						otoCollectionService.get(configuration.getOtoUploadId(), configuration.getOtoSecret(), new AsyncCallback<Collection>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToGetOtoCollection(caught);
								Alerter.stopLoading(box);
							}
							@Override
							public void onSuccess(Collection result) {
								SemanticMarkupReviewPresenter.this.collection = result;
								//don't want to initialize from history when coming back to the task again -> false
								view.getOto().setUser(Authentication.getInstance().getFirstName() + " " + 
											Authentication.getInstance().getLastName() + " (" + 
											Authentication.getInstance().getEmail() + ")");
								otoEventBus.fireEvent(new LoadEvent(result, false));
								SemanticMarkupReviewPresenter.this.task = task;
								Alerter.stopLoading(box);
								createSaveTimer();
							}
						});
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToReview(caught);
						Alerter.stopLoading(box);
					}
				});
			}
		});
	}

	@Override
	public ISemanticMarkupReviewView getView() {
		return view;
	}

	@Override
	public void onNext() {
		removeSaveTimer();
		final MessageBox box = Alerter.startLoading();
		this.otoCollectionService.update(collection, false, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSaveOto(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Void result) {
				semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.PARSE_TEXT, new AsyncCallback<Task>() {
					@Override
					public void onSuccess(Task result) {
						Alerter.stopLoading(box);
						placeController.goTo(new SemanticMarkupParsePlace(task));
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToGoToTaskStage(caught);
						Alerter.stopLoading(box);
					}
				});
			}
		});
	}

	@Override
	public void onSave() {
		createSaveTimer();
		final MessageBox box = Alerter.startLoading();
		this.otoCollectionService.update(collection, false, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSaveOto(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Void result) {
				Alerter.savedSuccessfully();
				Alerter.stopLoading(box);
			}
		});
	}	
}
