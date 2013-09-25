package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.LearnMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.BracketValidator;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;

public class PreprocessMatrixGenerationPresenter {

	public interface Display {
		Widget asWidget();
		Button getPreviousDescriptionButton();
		Button getNextDescriptionButton();
		Button getNextButton();
		ChangeAwareRichTextArea getTextArea();
		Label getDescriptionIDLabel();
		HTML getBracketCountsHTML();
	}

	private HandlerManager eventBus;
	private Display display;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private List<PreprocessedDescription> preprocessedDescriptions;
	private int currentPreprocessedDescription;
	private BracketColorizer bracketColorizer = new BracketColorizer();
	private BracketValidator bracketValidator = new BracketValidator();
	private Formatter formatter;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Unmatched brackets");

	public PreprocessMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.matrixGenerationService = matrixGenerationService;
		bind();
	}

	private void bind() {
		this.formatter = display.getTextArea().getFormatter();
		
		// cursor would jump once the new text is set with html tags inside after colorization
		// therefore we only colorize and check at the very end
		//https://groups.google.com/forum/#!topic/google-web-toolkit/YsTHsy0H9-8
		//https://code.google.com/p/google-web-toolkit/issues/detail?id=1127
		display.getTextArea().addValueChangeHandler(new ValueChangeHandler<String>() {
	        @Override
	        public void onValueChange(ValueChangeEvent<String> event) {
	        	String text = display.getTextArea().getText();
	        	updateBracketCounts(bracketValidator.getBracketCountDifferences(text));
	        	//setContent(text, bracketValidator.getBracketCountDifferences(text));
	        }
	    }); 
		
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
						String text = display.getTextArea().getText();
						if(bracketValidator.validate(text)) {
							preprocessedDescriptions.remove(currentPreprocessedDescription);
							if(preprocessedDescriptions.size() == 1)
								disableDescriptionsNavigation();
						}
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
						String text = display.getTextArea().getText();
						if(bracketValidator.validate(text)) {
							preprocessedDescriptions.remove(currentPreprocessedDescription);
							currentPreprocessedDescription--;
							if(preprocessedDescriptions.size() == 1)
								disableDescriptionsNavigation();
						}
						currentPreprocessedDescription++;
						if (currentPreprocessedDescription > preprocessedDescriptions.size() - 1)
							currentPreprocessedDescription = 0;
						setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
					}
				});
			}
		});
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				if(preprocessedDescriptions.size() == 0 || (preprocessedDescriptions.size() == 1 && 
						bracketValidator.validate(display.getTextArea().getText())))
					storeAndLeave();
				else {
					messagePresenter.setMessage("You have not corrected all the unmatched brackets.");
					messagePresenter.go();
				}
			}

			private void storeAndLeave() {
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

	private void disableDescriptionsNavigation() {
		display.getNextDescriptionButton().setEnabled(false);
		display.getPreviousDescriptionButton().setEnabled(false);
	}
	
	private void enableDescriptionNavigation() {
		display.getNextDescriptionButton().setEnabled(true);
		display.getPreviousDescriptionButton().setEnabled(true);
	}

	protected void store(AsyncCallback<Boolean> asyncCallback) {
		String target = preprocessedDescriptions.get(currentPreprocessedDescription).getTarget();
		String content = display.getTextArea().getText();
		matrixGenerationService.setDescription(Authentication.getInstance().getAuthenticationToken(), 
				target, content, asyncCallback);
	}
	
	public void go(final HasWidgets content, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		loadingPopup.start();
		matrixGenerationService.preprocess(Authentication.getInstance().getAuthenticationToken(), 
				matrixGenerationConfiguration, new AsyncCallback<List<PreprocessedDescription>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						loadingPopup.stop();
					}
					@Override
					public void onSuccess(List<PreprocessedDescription> result) {
						if(result.isEmpty())
							eventBus.fireEvent(new LearnMatrixGenerationEvent(matrixGenerationConfiguration));
						else 
							preprocessedDescriptions = result;
						display.getTextArea().setText("");
						display.getBracketCountsHTML().setHTML("");
						display.getDescriptionIDLabel().setText("");
						enableDescriptionNavigation();
						
						if(preprocessedDescriptions.size() == 1)
							disableDescriptionsNavigation();
						currentPreprocessedDescription = 0;
						
						loadingPopup.start();
						setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
						loadingPopup.stop();
						content.clear();
						content.add(display.asWidget());

						loadingPopup.stop();
					}
		});
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
							setContent(preprocessedDescription.getFileName(), result, preprocessedDescription.getBracketCounts());
						}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateBracketCounts(Map<Character, Integer> bracketCounts) {
		display.getBracketCountsHTML().setHTML(getBracketHTML(bracketCounts));
	}
	
	private void setContent(String descriptionId, String text, Map<Character, Integer> bracketCounts) {
		//doesn't work because the getCursorPos uses element.selectionRange. However the element is an iframe with html content for
		// the richtextarea, rather than a simpler html element (html textarea) that implemetns selectionRange...
		/*
		int pos = 0;
		try {
			pos = display.getTextArea().getCursorPos();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
		display.getDescriptionIDLabel().setText("Description: " + descriptionId);
		updateBracketCounts(bracketCounts);
		text = bracketColorizer.colorize(text);
		System.out.println(text);
		
		/*System.out.println(display.getTextArea().getFormatter().getForeColor());
		System.out.println(display.getTextArea().getFormatter().isBold());
		*/
		display.getTextArea().setHTML(text);
		
		/*
		System.out.println(display.getTextArea().getFormatter().getForeColor());
		System.out.println(display.getTextArea().getFormatter().isBold());
		
		formatter.setForeColor("black");
		formatter.toggleBold();
		
		System.out.println(display.getTextArea().getFormatter().getForeColor());
		System.out.println(display.getTextArea().getFormatter().isBold());
		*/
		
		/*try {
			display.getTextArea().setCursorPos(pos);
		} catch(Exception e) {
			e.printStackTrace();
		}*/

	}
	
	private String getBracketHTML(Map<Character, Integer> bracketCounts) {
		StringBuilder result = new StringBuilder();
		for(Character character : bracketCounts.keySet()) {
			int count = bracketCounts.get(character);
			if(count > 0)
				result.append(character + " +" + count + "<br>");
			else
				result.append(character + " " + count + "<br>");
		}
		return result.toString();
	}
}
