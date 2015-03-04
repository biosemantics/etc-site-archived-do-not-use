package edu.arizona.biosemantics.etcsite.client.content.home;

import java.util.Arrays;
import java.util.LinkedList;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.AuthenticationPresenter;
import edu.arizona.biosemantics.etcsite.client.common.AuthenticationToPlaceGoer;
import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.common.ResumeTaskToPlaceGoer;
import edu.arizona.biosemantics.etcsite.client.common.ToPlaceGoer;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.pipeline.PipelinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumeTaskPlaceMapper;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.pipeline.IPipelineServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.visualization.IVisualizationServiceAsync;

public class HomeActivity extends MyAbstractActivity implements IHomeContentView.Presenter {

	private IHomeContentView homeContentView;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private IVisualizationServiceAsync visualizationService;
	private IPipelineServiceAsync pipelineService;
	private AuthenticationToPlaceGoer authenticationToPlaceGoer;
	private ResumeTaskToPlaceGoer resumeTaskToPlaceGoer;

	@Inject
	public HomeActivity(IHomeContentView homeContentView, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IMatrixGenerationServiceAsync matrixGenerationService,
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			ITreeGenerationServiceAsync treeGenerationService,
			IVisualizationServiceAsync visualizationService,
			IPipelineServiceAsync pipelineService,
			PlaceController placeController,
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter, 
			AuthenticationToPlaceGoer authenticationToPlaceGoer, 
			ResumeTaskToPlaceGoer resumeTaskToPlaceGoer) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.homeContentView = homeContentView;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.treeGenerationService = treeGenerationService;
		this.visualizationService = visualizationService;
		this.pipelineService = pipelineService;
		this.authenticationToPlaceGoer = authenticationToPlaceGoer;
		this.resumeTaskToPlaceGoer = resumeTaskToPlaceGoer;
    }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		homeContentView.setPresenter(this);
		panel.setWidget(homeContentView.asWidget());
	}

	@Override
	public void onMatrixGeneration() {		
		this.authenticationToPlaceGoer.goTo(new MatrixGenerationInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onSemanticMarkup() {
		this.authenticationToPlaceGoer.goTo(new SemanticMarkupInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onTaxonomyComparison() {
		this.authenticationToPlaceGoer.goTo(new TaxonomyComparisonInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onVisualization() {
		placeController.goTo(new VisualizationPlace());
	}

	@Override
	public void onPipeline() {
		placeController.goTo(new PipelinePlace());
	}

	@Override
	public void onTreeGeneration() {
		this.authenticationToPlaceGoer.goTo(new TreeGenerationInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
