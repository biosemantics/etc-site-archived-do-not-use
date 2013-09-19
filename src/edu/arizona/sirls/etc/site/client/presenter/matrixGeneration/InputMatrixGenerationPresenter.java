package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.matrixGeneration.PreprocessMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.ManagableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.fileManager.ManagableFileTreeView;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IHasPath;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.TaxonDescriptionFile;
import edu.arizona.sirls.etc.site.shared.rpc.TaxonGlossaryFile;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class InputMatrixGenerationPresenter /*implements IFileSelectClickHandlerListener*/ {
	
	public interface Display {
		Button getNextButton();
		Label getTaxonGlossaryFileNameLabel();
		Label getTaxonDescriptionFileNameLabel();
		Anchor getFormatRequirementsAnchor();
		FocusWidget getTaxonDescriptionFileButton();
		FocusWidget getTaxonGlossaryFileButton();
		FocusWidget getFileManagerAnchor();
		Widget asWidget();
	}

	private HandlerManager eventBus;
	private Display display;
	private MatrixGenerationJob matrixGenerationJob;
	private IFileServiceAsync fileService;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Format Requirements");
	private StringBuilder taxonDescriptionFile = new StringBuilder();
	private StringBuilder taxonGlossaryFile = new StringBuilder();

	public InputMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
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
		
		fileSelectClickHandler = new FileSelectDialogClickHandler(FileFilter.ALL,//FileFilter.GLOSSARY, 
				display.getTaxonGlossaryFileNameLabel(), taxonGlossaryFile, false);
		display.getTaxonGlossaryFileButton().addClickHandler(fileSelectClickHandler);
		display.getFileManagerAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ManagableFileTreeView fileTreeView = new ManagableFileTreeView();
				ManagableFileTreePresenter fileTreePresenter = new ManagableFileTreePresenter(eventBus, 
						fileTreeView, fileService, true, FileFilter.ALL);
				TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(false, "File Manager");
				dialogBox.setWidget(fileTreeView);
				dialogBox.center();
				dialogBox.setGlassEnabled(true);
		 		dialogBox.show();
			}
		});
		
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				matrixGenerationJob.setTaxonDescriptionFile(taxonDescriptionFile.toString());
				matrixGenerationJob.setTaxonGlossaryFile(taxonGlossaryFile.toString());
				eventBus.fireEvent(new PreprocessMatrixGenerationEvent());
			}
		});
	}

	public void go(HasWidgets content, MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
		this.display.getTaxonGlossaryFileNameLabel().setText("");
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
							String target = presenter.getFileSelectionHandler().getTarget();
							if(target != null) {
								text.setText(target);
								stringBuilder.setLength(0);
								stringBuilder.append(target);
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
			dialogBox.setWidget(view);
			dialogBox.setAnimationEnabled(true);
			dialogBox.setGlassEnabled(true);
			dialogBox.center();
	 		dialogBox.show(); 
		}
	}
}
