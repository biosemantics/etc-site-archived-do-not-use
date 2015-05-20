package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;

public class TreeGenerationInputPresenter implements ITreeGenerationInputView.Presenter {

	private ITreeGenerationInputView view;
	private PlaceController placeController;
	private ITreeGenerationServiceAsync treeGenerationService;
	private String inputFile;
	
	@Inject
	public TreeGenerationInputPresenter(ITreeGenerationInputView view, 
			ITreeGenerationServiceAsync treeGenerationService,
			PlaceController placeController
			) {
		this.view = view;
		view.setPresenter(this);;
		this.treeGenerationService = treeGenerationService;
		this.placeController = placeController;
	}
	

	@Override
	public void onNext() {
		if (inputFile == null || inputFile.equals("")){
			Alerter.selectValidInputDirectory();
			return;
		}
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		
		final MessageBox box = Alerter.startLoading();
		treeGenerationService.start(Authentication.getInstance().getToken(), 
			view.getTaskName(), inputFile, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new TreeGenerationViewPlace(result));
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToStartTreeGeneration(caught);
			}
		});
	}
				

	@Override
	public ITreeGenerationInputView getView() {
		return view;
	}
	
	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setFilePath(shortendPath);
	}

}
