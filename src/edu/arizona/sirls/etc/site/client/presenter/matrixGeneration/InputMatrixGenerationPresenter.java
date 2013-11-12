package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.ManagableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.ManagableFileTreeView;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class InputMatrixGenerationPresenter /*implements IFileSelectClickHandlerListener*/ {
	
	public interface Display {
		Button getNextButton();
		TextBox getNameTextBox();
		Label getTaxonDescriptionFileNameLabel();
		Anchor getFormatRequirementsAnchor();
		FocusWidget getTaxonDescriptionFileButton();
		FocusWidget getFileManagerAnchor();
		Widget asWidget();
		ListBox getGlossaryListBox();
	}

	private HandlerManager eventBus;
	private Display display;
	private IFileServiceAsync fileService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Format Requirements");
	private StringBuilder taxonDescriptionFile = new StringBuilder();
	private StringBuilder taxonGlossaryFile = new StringBuilder();

	public InputMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		this.matrixGenerationService = matrixGenerationService;
		bind();
	}

	private void bind() {
		display.getFormatRequirementsAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messagePresenter.setMessage("Matrix Generation requires a set of XML files as input. The XML files have to be " +
						"<ul>" +
						"<li> valid against the specified XML <a target=\"_blank\" href=\"https://github.com/biosemantics/charaparser/blob/master/resources/io/iplant.xsd\">schema</a>" +
						"<li>  UTF-8 encoded" +
						"</ul>");
				messagePresenter.go();
			}
		});

		FileSelectDialogClickHandler fileSelectClickHandler = new FileSelectDialogClickHandler(
				FileFilter.DIRECTORY, 
				//FileFilter.TAXON_DESCRIPTION,
				display.getTaxonDescriptionFileNameLabel(), 
				taxonDescriptionFile, true);
		display.getTaxonDescriptionFileButton().addClickHandler(fileSelectClickHandler);
		ListBox glossaryListBox = display.getGlossaryListBox();		
		glossaryListBox.addItem("Hymenoptera");
		glossaryListBox.addItem("Porifera");
		glossaryListBox.addItem("Algea");
		glossaryListBox.addItem("Fossil");
		glossaryListBox.addItem("Plant");
		
		display.getFileManagerAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ManagableFileTreeView fileTreeView = new ManagableFileTreeView();
				ManagableFileTreePresenter fileTreePresenter = new ManagableFileTreePresenter(eventBus, 
						fileTreeView, fileService, true, FileFilter.ALL);
				TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(false, "File Manager");
				ScrollPanel scrollPanel = new ScrollPanel();
				scrollPanel.addStyleName("fileManagerScrollPanel");
				scrollPanel.setWidget(fileTreeView);
				dialogBox.setWidget(scrollPanel);
				dialogBox.center();
				dialogBox.setGlassEnabled(true);
		 		dialogBox.show();
		 		ManagableFileTreePresenter.setInputFileMultiple();
			}
		});
		
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				matrixGenerationService.start(Authentication.getInstance().getAuthenticationToken(), 
						display.getNameTextBox().getText(), 
						taxonDescriptionFile.toString(), 
						display.getGlossaryListBox().getItemText(display.getGlossaryListBox().getSelectedIndex()),
						new AsyncCallback<RPCResult<MatrixGenerationTaskRun>>() {
							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
							@Override
							public void onSuccess(RPCResult<MatrixGenerationTaskRun> result) {
								if(result.isSucceeded())
									eventBus.fireEvent(new MatrixGenerationEvent(result.getData()));
								//ConfigurationManager.getInstance().setMatrixGenerationConfiguration(result);
							}
				});
			}
		});
	}

	public void go(HasWidgets content) {
		this.display.getNameTextBox().setText("");
		this.display.getGlossaryListBox().setSelectedIndex(0);
		this.display.getTaxonDescriptionFileNameLabel().setText("");
		this.display.getNextButton().setEnabled(false);
		content.clear();
		content.add(this.display.asWidget());
	}
	
	public class FileSelectDialogClickHandler implements ClickHandler {

		private TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(true, "Select File");
		private SelectableFileTreeView view;
		private SelectableFileTreePresenter presenter;

		public FileSelectDialogClickHandler(FileFilter fileFilter, final HasText text, 
				final StringBuilder stringBuilder,
				final boolean requiredToContinue) {
			view = new SelectableFileTreeView();
			presenter = new SelectableFileTreePresenter(eventBus, view, fileService, fileFilter,
					new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							FileImageLabelTreeItem selection = presenter.getFileSelectionHandler().getSelection();
							if(selection != null) {
								text.setText(selection.getFileInfo().getFilePath());
								stringBuilder.setLength(0);
								stringBuilder.append(selection.getFileInfo().getFilePath());
								if(requiredToContinue)
									display.getNextButton().setEnabled(true);
							}
							dialogBox.hide();
						} 
					}, new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							dialogBox.hide();
						} 
			});
		}

		@Override
		public void onClick(ClickEvent event) {	
			presenter.getFileTreePresenter().refresh();
			dialogBox.setWidget(view);
			dialogBox.setAnimationEnabled(true);
			dialogBox.setGlassEnabled(true);
			dialogBox.center();
	 		dialogBox.show(); 
		}
	}
}
