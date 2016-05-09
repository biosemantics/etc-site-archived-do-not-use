package edu.arizona.biosemantics.etcsite.client.help;

import java.util.HashMap;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupLearnPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupParsePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPreprocessPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.help.IHelpView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpServiceAsync;


public class HelpActivity extends MyAbstractActivity implements Presenter {

		private IHelpView helpView;
		private IHelpServiceAsync helpService;
		private PlaceController placeController;
		private AcceptsOneWidget panel;

		@Inject
		public HelpActivity(IHelpView helpView, IHelpServiceAsync helpService, PlaceController placeController,
				IAuthenticationServiceAsync authenticationService, 
				ILoginView.Presenter loginPresenter, 
				IRegisterView.Presenter registerPresenter,
				IResetPasswordView.Presenter resetPasswordPresenter){
			super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
			this.helpView = helpView;
			this.helpService = helpService;
			this.placeController = placeController;
			helpView.setPresenter(this);
		}
		
		private void setHelpContentWithPlace() {
			Place place = placeController.getWhere();
			setHelpContent(getHelpOfPlace(place.toString()));
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			panel.setWidget(flowLayoutContainer);
			flowLayoutContainer.add(helpView.asWidget());
		}

		public void setHelpContent(Help help){
			helpService.getHelp(help, new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					Alerter.showAlert("Get Help", "Failed to get help content");
				}
				@Override
				public void onSuccess(String result) {
					setHelpContent(JsonUtils.<JsArray<HelpContent>>safeEval(result));
					helpView.onResize();
				}
				
			});	
		}
		
		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			this.panel = panel;
			setHelpContentWithPlace();
		}

		@Override
		public void update() {
			setHelpContentWithPlace();	
		}

		protected void setHelpContent(JsArray<HelpContent> contents) {
			helpView.setContent(contents);
		}


		@Override
		public IHelpView getView() {
			return helpView;
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

	}



