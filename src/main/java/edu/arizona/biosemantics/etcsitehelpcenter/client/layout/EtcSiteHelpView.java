package edu.arizona.biosemantics.etcsitehelpcenter.client.layout;

import java.util.HashMap;

import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;

import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;
import edu.arizona.biosemantics.etcsitehelpcenter.client.common.ImageLabel;

public class EtcSiteHelpView extends Composite implements IEtcSiteHelpView {

	private static CompleteHelpUiBinder uiBinder = GWT.create(CompleteHelpUiBinder.class);

	interface CompleteHelpUiBinder extends UiBinder<Widget, EtcSiteHelpView> {
	}
	
		
	private Presenter presenter;
	
	@UiField
	PanelGroup accordionPanel;
	
	@UiField ImageLabel homeMenu;
	@UiField ImageLabel taskManager;
	@UiField ImageLabel fileManager;
	@UiField ImageLabel account;
	@UiField ImageLabel textCapture;
	
	@UiField ListGroup textCaptureSubmenu;
	@UiField ListGroup ontologyBuildingSubmenu;
	@UiField ListGroup matrixGenerationSubmenu;
	@UiField ListGroup treeGenerationSubmenu;
	@UiField ListGroup taxonomyComparisonSubmenu;
	
	@UiField FocusPanel textCaptureMenu;
	@UiField FocusPanel ontologyBuildingMenu;
	@UiField FocusPanel matrixGenerationMenu;
	@UiField FocusPanel treeGenerationMenu;
	@UiField FocusPanel taxonomyComparisonMenu;

	@UiField ListGroupItem textCaptureInput;
	@UiField ListGroupItem textCaptureDefine;
	@UiField ListGroupItem textCapturePreprocess;
	@UiField ListGroupItem textCaptureLearn;
	@UiField ListGroupItem textCaptureReview;
	@UiField ListGroupItem textCaptureParse;
	@UiField ListGroupItem textCaptureOutput;

	@UiField ListGroupItem ontologyBuildingInput;
	@UiField ListGroupItem ontologyBuildingDefine;
	@UiField ListGroupItem ontologyBuildingBuild;
	@UiField ListGroupItem ontologyBuildingOutput;

	@UiField ListGroupItem matrixGenerationInput;
	@UiField ListGroupItem matrixGenerationDefine;
	@UiField ListGroupItem matrixGenerationProcess;
	@UiField ListGroupItem matrixGenerationReview;
	@UiField ListGroupItem matrixGenerationOutput;

	@UiField ListGroupItem treeGenerationInput;
	@UiField ListGroupItem treeGenerationDefine;
	@UiField ListGroupItem treeGenerationView;

	@UiField ListGroupItem taxonomyComparisonInput;
	@UiField ListGroupItem taxonomyComparisonDefine;
	@UiField ListGroupItem taxonomyComparisonAlign;
	
	
	class MenuClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			Widget source = (Widget)event.getSource();
			if(source.equals(homeMenu)){
				presenter.populateHelpContent(Help.GETTING_STARTED);
			}else if(source.equals(fileManager)){
				presenter.populateHelpContent(Help.FILE_MANAGER);
			}else if(source.equals(taskManager)){
				presenter.populateHelpContent(Help.TASK_MANAGER);
			}else if(source.equals(account)){
				presenter.populateHelpContent(Help.MY_ACCOUNT);
			}else if(source.equals(textCaptureDefine)) {
				presenter.populateHelpContent(Help.TEXT_CAPTURE_DEFINE);
			}else if(source.equals(textCaptureInput)){
				presenter.populateHelpContent(Help.TEXT_CAPTURE_INPUT);
			}else if(source.equals(textCapturePreprocess)){
				presenter.populateHelpContent(Help.TEXT_CAPTURE_PREPROCESS);
			}else if(source.equals(textCaptureLearn)){
				presenter.populateHelpContent(Help.TEXT_CAPTURE_LEARN);
			}else if(source.equals(textCaptureParse)){
				presenter.populateHelpContent(Help.TEXT_CAPTURE_PARSE);
			}else if(source.equals(textCaptureReview)){
				presenter.populateHelpContent(Help.TEXT_CAPTURE_REVIEW);
			}else if(source.equals(textCaptureOutput)){
				presenter.populateHelpContent(Help.TEXT_CAPTURE_OUTPUT);
			}else if(source.equals(ontologyBuildingDefine)){
				presenter.populateHelpContent(Help.ONTOLOGY_BUILDING_DEFINE);
			}else if(source.equals(ontologyBuildingInput)){
				presenter.populateHelpContent(Help.ONTOLOGY_BUILDING_INPUT);
			}else if(source.equals(ontologyBuildingBuild)){
				presenter.populateHelpContent(Help.ONTOLOGY_BUILDING_BUILD);
			}else if(source.equals(ontologyBuildingOutput)){
				presenter.populateHelpContent(Help.ONTOLOGY_BUILDING_OUTPUT);
			}else if(source.equals(matrixGenerationDefine)){
				presenter.populateHelpContent(Help.MATRIX_GENERATION_DEFINE);
			}else if(source.equals(matrixGenerationInput)){
				presenter.populateHelpContent(Help.MATRIX_GENERATION_INPUT);
			}else if(source.equals(matrixGenerationProcess)){
				presenter.populateHelpContent(Help.MATRIX_GENERATION_PROCESS);
			}else if(source.equals(matrixGenerationReview)){
				presenter.populateHelpContent(Help.MATRIX_GENERATION_REVIEW);
			}else if(source.equals(matrixGenerationOutput)){
				presenter.populateHelpContent(Help.MATRIX_GENERATION_OUTPUT);
			}else if(source.equals(treeGenerationDefine)){
				presenter.populateHelpContent(Help.TREE_GENERATION_DEFINE);
			}else if(source.equals(treeGenerationInput)){
				presenter.populateHelpContent(Help.TREE_GENERATION_INPUT);
			}else if(source.equals(treeGenerationView)){
				presenter.populateHelpContent(Help.TREE_GENERATION_VIEW);
			}else if(source.equals(taxonomyComparisonDefine)){
				presenter.populateHelpContent(Help.TAXONOMY_COMPARISON_DEFINE);
			}else if(source.equals(taxonomyComparisonInput)){
				presenter.populateHelpContent(Help.TAXONOMY_COMPARISON_INPUT);
			}else if(source.equals(taxonomyComparisonAlign)){
				presenter.populateHelpContent(Help.TAXONOMY_COMPARISON_ALIGN);
			} 
		}
		
	}
	
	class PanelShowHandler implements ShowHandler{
		Anchor associatedAnchor;
		
		public PanelShowHandler(Anchor associatedAnchor){
			this.associatedAnchor = associatedAnchor;
		}
		@Override
		public void onShow(ShowEvent showEvent) {
			associatedAnchor.setIcon(IconType.MINUS);
		}
	}
	
	class PanelHideHandler implements HideHandler{
		Anchor associatedAnchor;
		
		public PanelHideHandler(Anchor associatedAnchor){
			this.associatedAnchor = associatedAnchor;
		}
		@Override
		public void onHide(HideEvent hideEvent) {
			associatedAnchor.setIcon(IconType.PLUS);
		}
	}
	
	public EtcSiteHelpView() {
		initWidget(uiBinder.createAndBindUi(this));
		MenuClickHandler handler = new MenuClickHandler();
		homeMenu.addDomHandler(handler, ClickEvent.getType());
		taskManager.addDomHandler(handler, ClickEvent.getType());
		fileManager.addDomHandler(handler, ClickEvent.getType());
		account.addDomHandler(handler, ClickEvent.getType());
		textCaptureInput.addDomHandler(handler, ClickEvent.getType());
		textCaptureDefine.addDomHandler(handler, ClickEvent.getType());
		textCapturePreprocess.addDomHandler(handler, ClickEvent.getType());
		textCaptureLearn.addDomHandler(handler, ClickEvent.getType());
		textCaptureReview.addDomHandler(handler, ClickEvent.getType());
		textCaptureParse.addDomHandler(handler, ClickEvent.getType());
		textCaptureOutput.addDomHandler(handler, ClickEvent.getType());
		ontologyBuildingInput.addDomHandler(handler, ClickEvent.getType());
		ontologyBuildingDefine.addDomHandler(handler, ClickEvent.getType());
		ontologyBuildingBuild.addDomHandler(handler, ClickEvent.getType());
		ontologyBuildingOutput.addDomHandler(handler, ClickEvent.getType());
		matrixGenerationInput.addDomHandler(handler, ClickEvent.getType());
		matrixGenerationDefine.addDomHandler(handler, ClickEvent.getType());
		matrixGenerationProcess.addDomHandler(handler, ClickEvent.getType());
		matrixGenerationReview.addDomHandler(handler, ClickEvent.getType());
		matrixGenerationOutput.addDomHandler(handler, ClickEvent.getType());
		treeGenerationInput.addDomHandler(handler, ClickEvent.getType());
		treeGenerationDefine.addDomHandler(handler, ClickEvent.getType());
		treeGenerationView.addDomHandler(handler, ClickEvent.getType());
		taxonomyComparisonInput.addDomHandler(handler, ClickEvent.getType());
		taxonomyComparisonDefine.addDomHandler(handler, ClickEvent.getType());
		taxonomyComparisonAlign.addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void addContent(JsArray<HelpContent> contents){
		int count = 1;
		removeAccordionPanels();
		for(int i=0;i<contents.length();i++){
			Panel subPanel = new Panel();
			PanelHeader subPanelHeader = new PanelHeader();
			Heading panelHeading = new Heading(HeadingSize.H6);
			final Anchor headingAnchor = new Anchor();
			headingAnchor.setDataParent("#"+accordionPanel.getId());
			headingAnchor.setDataTarget("#collapse"+count);
			headingAnchor.setDataToggle(Toggle.COLLAPSE);
			headingAnchor.setIcon(IconType.PLUS);
			String title = contents.get(i).getTitle().replaceAll("</?b>", "");
			headingAnchor.setText(title);
			panelHeading.add(headingAnchor);
			subPanelHeader.add(panelHeading);
			PanelCollapse subPanelCollapse = new PanelCollapse();
			subPanelCollapse.addShowHandler(new PanelShowHandler(headingAnchor));
			subPanelCollapse.addHideHandler(new PanelHideHandler(headingAnchor));
			subPanelCollapse.setId("collapse" + count);
			PanelBody subPanelBody = new PanelBody();
			HTML htmlContent = new HTML(contents.get(i).getContent());
			subPanelBody.add(htmlContent);
			subPanelCollapse.add(subPanelBody);
			
			subPanel.add(subPanelHeader);
			subPanel.add(subPanelCollapse);
			
			accordionPanel.add(subPanel);
			if(count == 1){
				subPanelCollapse.setVisible(true);
			}
			count++;
		}
	}
	
	private void removeAccordionPanels() {
		int count = accordionPanel.getWidgetCount();
		while(count>0){
			accordionPanel.remove(count-1);
			count = accordionPanel.getWidgetCount();
		}
	}

	@UiHandler("textCaptureMenu")
	public void onMouseOverTextCapture(MouseOverEvent e){
		textCaptureSubmenu.setVisible(true);
	}
	
	
	@UiHandler("textCaptureMenu")
	public void onMouseOutTextCapture(MouseOutEvent e){
		textCaptureSubmenu.setVisible(false);
	}
	
	@UiHandler("matrixGenerationMenu")
	public void onMouseOverMatrixGeneration(MouseOverEvent e){
		matrixGenerationSubmenu.setVisible(true);
	}
	
	
	@UiHandler("matrixGenerationMenu")
	public void onMouseOutMatrixGeneration(MouseOutEvent e){
		matrixGenerationSubmenu.setVisible(false);
	}
	
	@UiHandler("treeGenerationMenu")
	public void onMouseOverTreeGeneration(MouseOverEvent e){
		treeGenerationSubmenu.setVisible(true);
	}
	
	
	@UiHandler("treeGenerationMenu")
	public void onMouseOutTreeGeneration(MouseOutEvent e){
		treeGenerationSubmenu.setVisible(false);
	}
	
	@UiHandler("taxonomyComparisonMenu")
	public void onMouseOverTaxonomyComparison(MouseOverEvent e){
		taxonomyComparisonSubmenu.setVisible(true);
	}
	
	
	@UiHandler("taxonomyComparisonMenu")
	public void onMouseOutTaxonomyComparison(MouseOutEvent e){
		taxonomyComparisonSubmenu.setVisible(false);
	}
	
	@UiHandler("ontologyBuildingMenu")
	public void onMouseOverOntologyBuilding(MouseOverEvent e){
		ontologyBuildingSubmenu.setVisible(true);
	}
	
	
	@UiHandler("ontologyBuildingMenu")
	public void onMouseOutOntologyBuilding(MouseOutEvent e){
		ontologyBuildingSubmenu.setVisible(false);
	}
		
}
