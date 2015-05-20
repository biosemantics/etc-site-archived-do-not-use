package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class SemanticMarkupInputPresenter implements ISemanticMarkupInputView.Presenter {

	private ISemanticMarkupInputView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private String inputFile;

	@Inject
	public SemanticMarkupInputPresenter(ISemanticMarkupInputView view, 
			PlaceController 
			placeController, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
	}
	
	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onNext() {
		//error checking.
		if (view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		if (inputFile == null){
			Alerter.selectValidInputDirectory();
			return;
		}
		
		final MessageBox box = Alerter.startLoading();
		semanticMarkupService.start(Authentication.getInstance().getToken(), 
				view.getTaskName(), inputFile, view.getGlossaryName(), view.isEmptyGlossarySelected(), new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				switch(TaskStageEnum.valueOf(result.getTaskStage().getTaskStage())) {
					case LEARN_TERMS:
						placeController.goTo(new SemanticMarkupLearnPlace(result));
						break;
					default:
						placeController.goTo(new SemanticMarkupPreprocessPlace(result));
						break;
				}
				Alerter.stopLoading(box);
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToStartSemanticMarkup(caught);
				Alerter.stopLoading(box);
			}
		});
	}

	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setInput(shortendPath);
	}

}
