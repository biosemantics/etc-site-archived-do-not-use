package edu.arizona.sirls.etc.site.client.presenter.semanticMarkup;

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
import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.SemanticMarkupConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.BracketValidator;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.TaskStageEnum;

public class PreprocessSemanticMarkupPresenter {

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
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private Task task;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private List<PreprocessedDescription> preprocessedDescriptions;
	private int currentPreprocessedDescription;
	private BracketColorizer bracketColorizer = new BracketColorizer();
	private BracketValidator bracketValidator = new BracketValidator();
	private Formatter formatter;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Unmatched brackets");

	public PreprocessSemanticMarkupPresenter(HandlerManager eventBus,
			Display display, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.eventBus = eventBus;
		this.display = display;
		this.semanticMarkupService = semanticMarkupService;
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
				store(new AsyncCallback<RPCResult<Void>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						navigate();
						loadingPopup.stop();
					}
					@Override
					public void onSuccess(RPCResult<Void> result) {
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
				store(new AsyncCallback<RPCResult<Void>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						navigate();
						loadingPopup.stop();
					}
					@Override
					public void onSuccess(RPCResult<Void> result) {
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
				store(new AsyncCallback<RPCResult<Void>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(RPCResult<Void> result) {	
						semanticMarkupService.goToTaskStage(Authentication.getInstance().getAuthenticationToken(), task, 
								TaskStageEnum.LEARN_TERMS, new AsyncCallback<RPCResult<Task>>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(RPCResult<Task> taskResult) {
										if(taskResult.isSucceeded())
											eventBus.fireEvent(new SemanticMarkupEvent(taskResult.getData()));
									} 
						});
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

	protected void store(AsyncCallback<RPCResult<Void>> asyncCallback) {
		String target = preprocessedDescriptions.get(currentPreprocessedDescription).getFilePath();
		String content = display.getTextArea().getText();
		semanticMarkupService.setDescription(Authentication.getInstance().getAuthenticationToken(), 
				target, content, asyncCallback);
	}
	
	public void go(final HasWidgets content, final Task task) {
		this.task = task;
		loadingPopup.start();
		semanticMarkupService.preprocess(Authentication.getInstance().getAuthenticationToken(), 
				task, new AsyncCallback<RPCResult<List<PreprocessedDescription>>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						loadingPopup.stop();
					}
					@Override
					public void onSuccess(RPCResult<List<PreprocessedDescription>> result) {
						if(result.isSucceeded()) {
							if(result.getData().isEmpty()) {
								loadingPopup.stop();
								semanticMarkupService.goToTaskStage(Authentication.getInstance().getAuthenticationToken(), task, 
										TaskStageEnum.LEARN_TERMS, new AsyncCallback<RPCResult<Task>>() {
											@Override
											public void onFailure(Throwable caught) {
												caught.printStackTrace();
											}
											@Override
											public void onSuccess(RPCResult<Task> taskResult) {
												eventBus.fireEvent(new SemanticMarkupEvent(taskResult.getData()));
											}
								});
								return;
							} else 
								preprocessedDescriptions = result.getData();
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
					}
		});
	}


	private void setPreprocessedDescription(final PreprocessedDescription preprocessedDescription) {
		try {
			semanticMarkupService.getDescription(Authentication.getInstance().getAuthenticationToken(), 
					preprocessedDescription.getFilePath(), new AsyncCallback<RPCResult<String>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(RPCResult<String> result) {
							if(result.isSucceeded())
								setContent(preprocessedDescription.getFileName(), result.getData(), preprocessedDescription.getBracketCounts());
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
