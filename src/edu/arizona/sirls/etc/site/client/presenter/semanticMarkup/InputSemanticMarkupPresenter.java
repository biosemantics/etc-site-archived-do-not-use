package edu.arizona.sirls.etc.site.client.presenter.semanticMarkup;

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
import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileSelectDialogClickHandler;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.ManagableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.ManagableFileTreeView;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class InputSemanticMarkupPresenter {
	
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
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Format Requirements");
	private StringBuilder taxonDescriptionFile = new StringBuilder();
	private StringBuilder taxonGlossaryFile = new StringBuilder();

	public InputSemanticMarkupPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		this.semanticMarkupService = semanticMarkupService;
		bind();
	}

	private void bind() {
		display.getFormatRequirementsAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messagePresenter.setMessage("Matrix Generation requires a set of XML files as input. The XML files have to be " +
						"<ul>" +
						"<li> valid against the specified XML <a target=\"_blank\" href=\"https://raw.github.com/biosemantics/schemas/master/iplantInputTreatment.xsd\">schema</a>" +
						"<li>  UTF-8 encoded" +
						"</ul>");
				messagePresenter.go();
			}
		});

		FileSelectDialogClickHandler fileSelectClickHandler = new FileSelectDialogClickHandler(eventBus, fileService,
				FileFilter.DIRECTORY, 
				//FileFilter.TAXON_DESCRIPTION,
				display.getTaxonDescriptionFileNameLabel(), 
				taxonDescriptionFile, true, display.getNextButton());
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
				fileTreePresenter.refresh();
				TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(false, "File Manager");
				ScrollPanel scrollPanel = new ScrollPanel();
				scrollPanel.addStyleName("fileManagerScrollPanel");
				scrollPanel.setWidget(fileTreeView);
				dialogBox.setWidget(scrollPanel);
				dialogBox.center();
				dialogBox.setGlassEnabled(true);
		 		dialogBox.show();
		 		ManagableFileTreePresenter.setInputFileMultiple();
		 		fileTreePresenter.refresh();
			}
		});
		
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				semanticMarkupService.start(Authentication.getInstance().getAuthenticationToken(), 
						display.getNameTextBox().getText(), 
						taxonDescriptionFile.toString(), 
						display.getGlossaryListBox().getItemText(display.getGlossaryListBox().getSelectedIndex()),
						new AsyncCallback<RPCResult<Task>>() {
							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
							@Override
							public void onSuccess(RPCResult<Task> result) {
								if(result.isSucceeded())
									eventBus.fireEvent(new SemanticMarkupEvent(result.getData()));
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
}
