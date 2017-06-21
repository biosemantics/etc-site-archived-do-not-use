package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MyWindow;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.DownloadEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetColorsEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class MatrixGenerationReviewPresenter implements IMatrixGenerationReviewView.Presenter {

	private Task task;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private IMatrixGenerationReviewView view;
	private PlaceController placeController;
	private Model model;
	private boolean unsavedChanges = false;

	@Inject
	public MatrixGenerationReviewPresenter(final IMatrixGenerationReviewView view, 
			final IMatrixGenerationServiceAsync matrixGenerationService,
			PlaceController placeController) {
		this.view = view;
		this.view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
		
		addMatrixReviewEventHandlers();
	}
	
	private void addMatrixReviewEventHandlers() {
		view.getMatrixReviewView().getFullModelBus().addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
			@Override
			public void onSet(SetValueEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
			@Override
			public void onAdd(AddCharacterEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
			@Override
			public void onAdd(AddTaxonEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
			@Override
			public void onMerge(MergeCharactersEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
			@Override
			public void onModify(ModifyCharacterEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
			@Override
			public void onModify(ModifyTaxonEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(ModifyOrganEvent.TYPE, new ModifyOrganEvent.ModifyOrganEventHandler() {
			@Override
			public void onModify(ModifyOrganEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(RemoveCharacterEvent.TYPE, new RemoveCharacterEvent.RemoveCharacterEventHandler() {
			@Override
			public void onRemove(RemoveCharacterEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(RemoveTaxaEvent.TYPE, new RemoveTaxaEvent.RemoveTaxonEventHandler() {
			@Override
			public void onRemove(RemoveTaxaEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetCharacterColorEvent.TYPE, new SetCharacterColorEvent.SetCharacterColorEventHandler() {
			@Override
			public void onSet(SetCharacterColorEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetTaxonColorEvent.TYPE, new SetTaxonColorEvent.SetTaxonColorEventHandler() {
			@Override
			public void onSet(SetTaxonColorEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetCharacterCommentEvent.TYPE, new SetCharacterCommentEvent.SetCharacterCommentEventHandler() {
			@Override
			public void onSet(SetCharacterCommentEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetTaxonCommentEvent.TYPE, new SetTaxonCommentEvent.SetTaxonCommentEventHandler() {
			@Override
			public void onSet(SetTaxonCommentEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetColorsEvent.TYPE, new SetColorsEvent.SetColorsEventHandler() {
			@Override
			public void onSet(SetColorsEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetValueColorEvent.TYPE, new SetValueColorEvent.SetValueColorEventHandler() {
			@Override
			public void onSet(SetValueColorEvent event) {
				unsavedChanges = true;
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SetValueCommentEvent.TYPE, new SetValueCommentEvent.SetValueCommentEventHandler() {
			@Override
			public void onSet(SetValueCommentEvent event) {
				unsavedChanges = true;
			}
		});
		
		
		view.getMatrixReviewView().getFullModelBus().addHandler(DownloadEvent.TYPE, new DownloadEvent.DownloadHandler() {
			@Override
			public void onDownload(DownloadEvent event) {
				download(event.getModel(), event.getFormat());
			}
		});
		view.getMatrixReviewView().getFullModelBus().addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {				
				final MessageBox box = Alerter.startLoading();
				matrixGenerationService.save(Authentication.getInstance().getToken(), event.getModel(), task, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToSaveMatrix(caught);
						Alerter.stopLoading(box);
					}
					@Override
					public void onSuccess(Void result) {
						Alerter.stopLoading(box);
						unsavedChanges = false;
					}
				});
			}
		});
	}

	protected void download(Model model, MatrixFormat format) {
		final MessageBox box = Alerter.startLoading();
		final MyWindow window = MyWindow.open(null, "_blank", null);
		if(format==null) format = MatrixFormat.CSV;
		matrixGenerationService.outputMatrix(Authentication.getInstance().getToken(), 
				task, model, format, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Alerter.stopLoading(box);
				window.setUrl("download.dld?target=" + URL.encodeQueryString(result) + 
						"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
						"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));
				/*Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
						"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
						"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
				*/
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToOutputMatrix(caught);
				Alerter.stopLoading(box);
			}
		});
	}

	@Override
	public void onNext() {
		if(task != null) {
			if(unsavedChanges) {
				MessageBox confirm = Alerter.confirmSaveMatrix();
				confirm.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						final MessageBox box = Alerter.startLoading();
						matrixGenerationService.save(Authentication.getInstance().getToken(), model, task, new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) { 
								unsavedChanges = false;
								completeReview(box);
							}
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToSaveMatrix(caught);
								Alerter.stopLoading(box);
							}
						});
					}
				});
				confirm.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						unsavedChanges = false; // user decided he doesn't care
						final MessageBox box = Alerter.startLoading();
						completeReview(box);
					}
					
				});
			} else {
				completeReview(null);
			}
		}
	}
	
	private void completeReview(final MessageBox box) {
		matrixGenerationService.completeReview(Authentication.getInstance().getToken(), 
				task, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				if(box != null)
					Alerter.stopLoading(box);
				placeController.goTo(new MatrixGenerationOutputPlace(result));
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToCompleteReview(caught);
				if(box != null)
					Alerter.stopLoading(box);
			}
		});
	}

	@Override
	public IMatrixGenerationReviewView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		this.task = task;
		final MessageBox box = Alerter.startLoading();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new AsyncCallback<Model>() { 
			public void onSuccess(Model result) {
				model = result;
				TaxonMatrix taxonMatrix = model.getTaxonMatrix();
				view.setFullModel(model);
				Alerter.stopLoading(box);
				if (taxonMatrix.getCharacterCount() == 0 || (taxonMatrix.getCharacterCount() == 1 && taxonMatrix.getFlatCharacters().get(0).getName().equals(""))){
					Alerter.matrixGeneratedEmpty();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToReview(caught);
				Alerter.stopLoading(box);
			}
		}); 
	}
	
	@Override
	public Model getTaxonMatrix() {
		return model;
	}

	@Override
	public void onSave() {
		final MessageBox box = Alerter.startLoading();
		matrixGenerationService.save(Authentication.getInstance().getToken(), model, task, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSaveMatrix(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Void result) {
				Alerter.stopLoading(box);	
				unsavedChanges = false;
			}
		});
	}

	@Override
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	@Override
	public void setUnsavedChanges(boolean value) {
		this.unsavedChanges = value;
	}

	@Override
	public void onExport() {
		download(model, MatrixFormat.CSV);
	}
}
