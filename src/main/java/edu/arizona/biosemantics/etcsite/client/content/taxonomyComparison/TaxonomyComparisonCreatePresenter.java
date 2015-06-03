package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.InputValidator;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.MyUploaderConstants;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView.Presenter;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationCreateView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;

public class TaxonomyComparisonCreatePresenter implements TaxonomyComparisonCreateView.Presenter{

	private ITaxonomyComparisonCreateView view;
	private Presenter fileManagerDialogPresenter;
	private edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.Presenter inputCreatePresenter;

	@Inject
	public TaxonomyComparisonCreatePresenter(final PlaceController placeController, 
			ITaxonomyComparisonCreateView view, 
			@Named("TaxonomyComparison") IInputCreateView.Presenter inputCreatePresenter,
			final ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		
		this.inputCreatePresenter = inputCreatePresenter;
		this.inputCreatePresenter.disableCreateFiles();
		inputCreatePresenter.setInputValidator(new InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				taxonomyComparisonService.isValidInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(!result) {
							Alerter.invalidInputDirectory();
							Alerter.stopLoading(box);
						} else {
							placeController.goTo(new TaxonomyComparisonInputPlace());
							Alerter.stopLoading(box);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToIsValidInput(caught);
						Alerter.stopLoading(box);
					}
				});
			}
		});
		inputCreatePresenter.setUploadFileType(FileTypeEnum.PLAIN_TEXT);
	}
	
	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onFileManager() {
		fileManagerDialogPresenter.show();
	}

	@Override
	public String getInputFolderPath() {
		return inputCreatePresenter.getInputFolderPath();
	}

	@Override
	public String getInputFolderShortenedPath() {
		return inputCreatePresenter.getInputFolderShortenedPath();
	}

	@Override
	public void refresh() {
		this.inputCreatePresenter.refreshFolders();
	}
}
