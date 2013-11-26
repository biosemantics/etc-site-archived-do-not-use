package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.matrixGeneration.review.ViewImpl;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class ReviewMatrixGenerationViewImpl extends MatrixGenerationViewImpl implements ReviewMatrixGenerationView {

	private static ReviewMatrixGenerationViewUiBinder uiBinder = GWT.create(ReviewMatrixGenerationViewUiBinder.class);

	@UiTemplate("ReviewMatrixGenerationView.ui.xml")
	interface ReviewMatrixGenerationViewUiBinder extends UiBinder<Widget, ReviewMatrixGenerationViewImpl> {
	}

	private Presenter presenter;

	@UiField(provided = true)
	ViewImpl view;
	
	public ReviewMatrixGenerationViewImpl(ViewImpl view) {
		super();
		this.view = view;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.REVIEW;
	}
	
	

}
