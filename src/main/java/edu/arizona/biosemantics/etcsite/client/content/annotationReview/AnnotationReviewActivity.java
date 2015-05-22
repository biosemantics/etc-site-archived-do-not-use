package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public class AnnotationReviewActivity extends MyAbstractActivity {

	private IAnnotationReviewView.Presenter annotationReviewPresenter;

	@Inject
	public AnnotationReviewActivity(IAnnotationReviewView.Presenter annotationReviewPresenter, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.annotationReviewPresenter = annotationReviewPresenter;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(annotationReviewPresenter.getView());
	}

	@Override
	public void update() {
		
	}
}
