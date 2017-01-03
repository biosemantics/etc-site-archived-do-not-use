package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;

public class SemanticMarkupOutputPresenter implements ISemanticMarkupOutputView.Presenter {

	private ISemanticMarkupOutputView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private FilePathShortener filePathShortener;
	protected Task task;
	private IUserServiceAsync userService;

	@Inject
	public SemanticMarkupOutputPresenter(ISemanticMarkupOutputView view, 
			PlaceController placeController, 
			ISemanticMarkupServiceAsync semanticMarkupService, 
			IUserServiceAsync userService,
			FilePathShortener filePathShortener) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.userService = userService;
		this.filePathShortener = filePathShortener;
	}
	
	@Override
	public void onFileManager() {
		placeController.goTo(new FileManagerPlace());
	}

	@Override
	public ISemanticMarkupOutputView getView() {
		return view;
	}

	@Override
	public void setTask(final Task task) {
		final MessageBox box = Alerter.startLoading();
		userService.hasLinkedOTOAccount(Authentication.getInstance().getToken(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetAccount(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(final Boolean hasLinkedOTOAccount) {
				semanticMarkupService.output(Authentication.getInstance().getToken(), task, 
						new AsyncCallback<Task>() {
					@Override
					public void onSuccess(Task result) {
						SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
						view.setOutput(configuration.getOutput(), 
								filePathShortener.shortenOutput(configuration.getOutput(), result, Authentication.getInstance().getUserId()), 
								configuration.getOutputTermReview());
						//OTO account is linked
						//not yet created a dataset for this task on OTO
						//a charaparser parse and not micropie
						view.setEnabledSendToOto(!configuration.isOtoCreatedDataset() && hasLinkedOTOAccount && 
								!configuration.getTaxonGroup().getName().equalsIgnoreCase("Bacteria"));
						SemanticMarkupOutputPresenter.this.task = task;
						Alerter.stopLoading(box);
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToOutput(caught);
						Alerter.stopLoading(box);
					}
				});
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
				view.setEnabledSendToOto(false);
			}
		});
	}

	@Override
	public void onContinueMatrixGeneration(String text) {
		Task task = new Task();
		MatrixGenerationConfiguration config = new MatrixGenerationConfiguration();
		config.setInput(text);
		task.setTaskConfiguration(config);
		placeController.goTo(new MatrixGenerationDefinePlace(task));
	}
	
	public void onMyAccount() {
		placeController.goTo(new SettingsPlace());
	}

}
