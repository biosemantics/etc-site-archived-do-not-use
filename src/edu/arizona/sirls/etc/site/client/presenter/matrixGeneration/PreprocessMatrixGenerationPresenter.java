package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.LearnMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.file.BracketValidator;

public class PreprocessMatrixGenerationPresenter {

	public interface Display {
		Widget asWidget();
		Button getPreviousDescriptionButton();
		Button getNextDescriptionButton();
		Button getNextButton();
		ChangeAwareRichTextArea getTextArea();
		Label getBracketCountsLabel();
	}

	private HandlerManager eventBus;
	private Display display;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private List<PreprocessedDescription> preprocessedDescriptions;
	private int currentPreprocessedDescription;
	private BracketColorizer bracketColorizer = new BracketColorizer();
	private BracketValidator bracketValidator = new BracketValidator();
	private String oldText;

	public PreprocessMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.matrixGenerationService = matrixGenerationService;
		bind();
	}

	private void bind() {
		display.getTextArea().addValueChangeHandler(new ValueChangeHandler<String>() {
	        @Override
	        public void onValueChange(ValueChangeEvent<String> event) {
	            System.out.println("value changed " + event.getValue());
	        }
	    });
		/*display.getTextArea().addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				String text = display.getTextArea().getText();
				if(!oldText.equals(text))
					setContent(text, bracketValidator.getBracketCountDifferences(text));
			}
		});
		display.getTextArea().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String text = display.getTextArea().getText();
				if(!oldText.equals(text))
					setContent(text, bracketValidator.getBracketCountDifferences(text));
			}
		});*/
		
		display.getPreviousDescriptionButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadingPopup.start();
			store(new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						navigate();
						loadingPopup.stop();
					}
					@Override
					public void onSuccess(Boolean result) {
						navigate();
						loadingPopup.stop();
					}
					private void navigate() {
						currentPreprocessedDescription--;
						if(currentPreprocessedDescription < 0)
							currentPreprocessedDescription = preprocessedDescriptions.size() - 1;
						setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
					}
				});
			}
		});
		display.getNextDescriptionButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadingPopup.start();
				store(new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						navigate();
						loadingPopup.stop();
					}
					@Override
					public void onSuccess(Boolean result) {
						navigate();
						loadingPopup.stop();
					}
					private void navigate() {
						currentPreprocessedDescription++;
						if (currentPreprocessedDescription > preprocessedDescriptions
								.size() - 1)
							currentPreprocessedDescription = 0;
						setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
					}
				});
			}
		});
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				store(new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(Boolean result) {	
						eventBus.fireEvent(new LearnMatrixGenerationEvent());
					}
				});
			}
		});
	}

	protected void store(AsyncCallback<Boolean> asyncCallback) {
		String target = preprocessedDescriptions.get(currentPreprocessedDescription).getTarget();
		String content = display.getTextArea().getText();
		matrixGenerationService.setDescription(Authentication.getInstance().getAuthenticationToken(), 
				target, content, asyncCallback);
	}
	
	public void go(HasWidgets content, MatrixGenerationJob matrixGenerationJob) {
		display.getTextArea().setText("");
		display.getBracketCountsLabel().setText("");
		
		this.preprocessedDescriptions = matrixGenerationJob.getPreprocessedDescriptions();
		this.currentPreprocessedDescription = 0;
		
		loadingPopup.start();
		this.setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
		loadingPopup.stop();
		content.clear();
		content.add(display.asWidget());
	}

	
	private void setPreprocessedDescription(final PreprocessedDescription preprocessedDescription) {
		try {
			matrixGenerationService.getDescription(Authentication.getInstance().getAuthenticationToken(), 
					preprocessedDescription.getTarget(), new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(String result) {
							setContent(result, preprocessedDescription.getBracketCounts());
						}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setContent(String text, Map<Character, Integer> bracketCounts) {
		display.getBracketCountsLabel().setText(getBracketText(bracketCounts));
		text = bracketColorizer.colorize(text);
		display.getTextArea().setHTML(text);
		this.oldText = text;
	}
	
	private String getBracketText(Map<Character, Integer> bracketCounts) {
		StringBuilder result = new StringBuilder();
		for(Character character : bracketCounts.keySet()) {
			int count = bracketCounts.get(character);
			if(count > 0)
				result.append(character + " +" + count + "\n");
			else
				result.append(character + " " + count + "\n");
		}
		return result.toString();
	}
}
