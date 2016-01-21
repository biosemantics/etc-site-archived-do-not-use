package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.model.semanticmarkup.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.oto2.oto.client.Oto;
//import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;

public class ImportOtoPresenter implements IImportOtoView.Presenter {
	
	private IImportOtoView view;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private Task task;
	private Oto oto;
	
	@Inject
	public ImportOtoPresenter(IImportOtoView view, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.view = view;
		view.setPresenter(this);
		this.semanticMarkupService = semanticMarkupService;
	}


	@Override
	public IImportOtoView getView() {
		return view;
	}

	@Override
	public void onImport(String termCategorization, String synonymy) {
		final MessageBox box = Alerter.startLoading();
		semanticMarkupService.importOto(task, termCategorization, synonymy, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToImportOto(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Void result) {
				Alerter.stopLoading(box);

				SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
				oto.loadCollection(configuration.getOtoUploadId(), configuration.getOtoSecret(), false);
			}
		});
	}

	public void setTask(Task task) {
		this.task = task;
	}


	public void show() {
		view.show();
	}
	
	public void hide() {
		view.hide();
	}


	public void setOto(Oto oto) {
		this.oto = oto;
	}

}
