package edu.arizona.biosemantics.etcsitehelpcenter.client.layout;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpServiceAsync;

public class EtcSiteHelpPresenter implements IEtcSiteHelpView.Presenter {

	private IEtcSiteHelpView view;
	private IHelpServiceAsync helpService;
	
	//private PlaceController placeController;

	@Inject
	public EtcSiteHelpPresenter(IEtcSiteHelpView view, IHelpServiceAsync helpService) {
		this.view = view;
		this.helpService = helpService;
		view.setPresenter(this);
		String helpPlace = Window.Location.getParameter("HelpPlace");
		if(helpPlace != null && helpPlace != ""){
			populateHelpContent(getHelpOfPlace(helpPlace));
		}else{
			populateHelpContent(Help.GETTING_STARTED);
		}
	}
	
	private Help getHelpOfPlace(String helpPlace) {
		switch(helpPlace){
		case "HomePlace":
		case "AboutPlace":
		case "NewsPlace":
		case "GettingStartedPlace":
			return Help.GETTING_STARTED;
		case "FileManagerPlace":
			return Help.FILE_MANAGER;
		case "TaskManagerPlace":
			return Help.TASK_MANAGER;
		case "SettingsPlace":
			return Help.MY_ACCOUNT;
		case "SemanticMarkupInputPlace":
			return Help.TEXT_CAPTURE_INPUT;
		case "SemanticMarkupDefinePlace":
			return Help.TEXT_CAPTURE_DEFINE;
		case "SemanticMarkupLearnPlace":
			return Help.TEXT_CAPTURE_LEARN;
		case "SemanticMarkupParsePlace":
			return Help.TEXT_CAPTURE_PARSE;
		case "SemanticMarkupPreprocessPlace":
			return Help.TEXT_CAPTURE_PREPROCESS;
		case "SemanticMarkupReviewPlace":
			return Help.TEXT_CAPTURE_REVIEW;
		case "SemanticMarkupOutputPlace":
			return Help.TEXT_CAPTURE_OUTPUT;
		case "MatrixGenerationInputPlace":
			return Help.MATRIX_GENERATION_INPUT;
		case "MatrixGenerationDefinePlace":
			return Help.MATRIX_GENERATION_DEFINE;
		case "MatrixGenerationProcessPlace":
			return Help.MATRIX_GENERATION_PROCESS;
		case "MatrixGenerationReviewPlace":
			return Help.MATRIX_GENERATION_REVIEW;
		case "MatrixGenerationOutputPlace":
			return Help.MATRIX_GENERATION_OUTPUT;
		case "OntologizeInputPlace":
			return Help.ONTOLOGY_BUILDING_INPUT;
		case "OntologizeDefinePlace":
			return Help.ONTOLOGY_BUILDING_DEFINE;
		case "OntologizeBuildPlace":
			return Help.ONTOLOGY_BUILDING_BUILD;
		case "OntologizeOutputPlace":
			return Help.ONTOLOGY_BUILDING_OUTPUT;
		case "TaxonomyComparisonInputPlace":
			return Help.TAXONOMY_COMPARISON_INPUT;
		case "TaxonomyComparisonDefinePlace":
			return Help.TAXONOMY_COMPARISON_DEFINE;
		case "TaxonomyComparisonAlignPlace":
			return Help.TAXONOMY_COMPARISON_ALIGN;
		case "TreeGenerationDefinePlace":
			return Help.TREE_GENERATION_DEFINE;
		case "TreeGenerationInputPlace":
			return Help.TREE_GENERATION_INPUT;
		case "TreeGenerationViewPlace":
			return Help.TREE_GENERATION_VIEW;
		default:
			return Help.GETTING_STARTED;
		}
	}

	public void populateHelpContent(Help help){
		helpService.getHelp(help, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				setHelpContent(JsonUtils.<JsArray<HelpContent>>safeEval(result));
			}
			
		});
	}

	
	protected void setHelpContent(JsArray<HelpContent> contents) {
		// TODO Auto-generated method stub
		view.addContent(contents);
	}


	@Override
	public IEtcSiteHelpView getView() {
		return view;
	}

}
