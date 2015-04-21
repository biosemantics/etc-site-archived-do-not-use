package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;

public class SemanticMarkupCreatePresenter implements SemanticMarkupCreateView.Presenter{

	private IFileServiceAsync fileService;
	private ISemanticMarkupCreateView view;
	private PlaceController placeController;
	
	@Inject
	public SemanticMarkupCreatePresenter(ISemanticMarkupCreateView view, 
			PlaceController placeController, IFileServiceAsync fileService) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.fileService = fileService;
		getAllFolders();
	}
	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onNext() {
		Alerter.startLoading();
		placeController.goTo(new SemanticMarkupInputPlace());
		Alerter.stopLoading();
	}

	@Override
	public void onInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileManager() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getAllFolders() {
		fileService.getAllOwnedFolders(Authentication.getInstance().getToken(), new AsyncCallback<List<FileInfo>>() {
			
			@Override
			public void onSuccess(List<FileInfo> result) {
				view.setFolderNames(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
