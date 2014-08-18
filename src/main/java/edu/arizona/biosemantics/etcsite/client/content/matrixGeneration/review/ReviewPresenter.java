package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class ReviewPresenter implements IReviewView.Presenter {

	private IReviewView view;
	private MatrixReviewView matrixReviewView;
	private TaxonMatrix taxonMatrix;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private MessagePresenter messagePresenter = new MessagePresenter();
	private Task task;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	@Inject
	public ReviewPresenter(IReviewView view, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.matrixGenerationService = matrixGenerationService;
		this.view = view;
	}
	
	@Override
	public void refresh(Task task) {
		this.task = task;
		loadingPopup.start();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new RPCCallback<TaxonMatrix>() { 
			public void onResult(TaxonMatrix result) {
				taxonMatrix = result;
				matrixReviewView = new MatrixReviewView(result);
				view.setMatrixReviewView(matrixReviewView);
				loadingPopup.stop();
				if (taxonMatrix.getCharacterCount() == 0 || (taxonMatrix.getCharacterCount() == 1 && taxonMatrix.getCharacter(0).getName().equals(""))){
					messagePresenter.showOkBox("Error", 	"Note: No data was generated for this matrix. This could be due to insufficient or invalid input. <br/><br/>"
							+ 								"If you feel this is an error, please <a href=\"https://github.com/biosemantics/matrix-generation/issues\" target=\"_blank\">report the issue on github</a>. (Remember to include your ETC <br/>"
							+ 								"username and the name of the folder containing your input files!)");
				}
			}
		}); 
		
	}
	
	@Override
	public IReviewView getView() {
		return view;
	}

	@Override
	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}

}

