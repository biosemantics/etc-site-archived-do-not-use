package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;
import java.util.Map;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.core.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.BracketValidator;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;

public class SemanticMarkupPreprocessPresenter implements ISemanticMarkupPreprocessView.Presenter {

	private Task task;
	private ISemanticMarkupPreprocessView view;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private List<PreprocessedDescription> preprocessedDescriptions;
	private int currentPreprocessedDescription;
	private BracketColorizer bracketColorizer;
	private BracketValidator bracketValidator;
	private PlaceController placeController;
	
	@Inject
	public SemanticMarkupPreprocessPresenter(ISemanticMarkupPreprocessView view, 
			PlaceController placeController,
			ISemanticMarkupServiceAsync semanticMarkupService, 
			BracketColorizer bracketColorizer, BracketValidator bracketValidator) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.bracketColorizer = bracketColorizer;
		this.bracketValidator = bracketValidator;
	}

	@Override
	public void setTask(final Task task) {
		this.task = task;
		semanticMarkupService.preprocess(Authentication.getInstance().getToken(), task, new AsyncCallback<List<PreprocessedDescription>>() {
			@Override
			public void onSuccess(List<PreprocessedDescription> result) {
                if(result.isEmpty()) {
                    semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, 
                    		TaskStageEnum.LEARN_TERMS, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {
							placeController.goTo(new SemanticMarkupLearnPlace(task));
						}
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToGoToTaskStage(caught);
						}
                    });
                    return;
                } else 
                	preprocessedDescriptions = result;
                	
                view.setHTML("");
                view.setBracketCounts("");
                view.setDescriptionIDLabel("");
                setEnabledDescriptionsNavigation(true);
                if(preprocessedDescriptions.size() == 1)
                	setEnabledDescriptionsNavigation(false);
                currentPreprocessedDescription = 0;
                setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
			}

			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof SemanticMarkupException) {
					placeController.goTo(new TaskManagerPlace());
				}
			}
		});
	}

	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onNext() {
		if(preprocessedDescriptions.size() == 0 || (preprocessedDescriptions.size() == 1 && 
				bracketValidator.validate(view.getHTML())))
			storeAndLeave();
		else {
			Alerter.unmatchedBrackets();
		}
	}

	private void storeAndLeave() {
		store(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {	
				semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, 
						TaskStageEnum.LEARN_TERMS, new AsyncCallback<Task>() {
							@Override
							public void onSuccess(Task task) {
								placeController.goTo(new SemanticMarkupLearnPlace(task));
							}

							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToGoToTaskStage(caught);
							} 
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSetDescription(caught);
			}
		});
	}

	@Override
	public void onNextDescription() {
		store(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				String text = view.getHTML();
				if(bracketValidator.validate(text)) {
					preprocessedDescriptions.remove(currentPreprocessedDescription);
					currentPreprocessedDescription--;
					if(preprocessedDescriptions.size() == 1)
						setEnabledDescriptionsNavigation(false);
				}
				currentPreprocessedDescription++;
				if (currentPreprocessedDescription > preprocessedDescriptions.size() - 1)
					currentPreprocessedDescription = 0;
				setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSetDescription(caught);
			}
		});
	}

	@Override
	public void onPreviousDescription() {
		store(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				String text = view.getHTML();
				if(bracketValidator.validate(text)) {
					preprocessedDescriptions.remove(currentPreprocessedDescription);
					if(preprocessedDescriptions.size() == 1)
						setEnabledDescriptionsNavigation(false);
				}
				currentPreprocessedDescription--;
				if(currentPreprocessedDescription < 0)
					currentPreprocessedDescription = preprocessedDescriptions.size() - 1;
				setPreprocessedDescription(preprocessedDescriptions.get(currentPreprocessedDescription));
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSetDescription(caught);
			}
		});
	}

	@Override
	public void onValueChange() {
		String text = view.getHTML();
    	updateBracketCounts(bracketValidator.getBracketCountDifferences(text));
	}
	
	private void updateBracketCounts(Map<Character, Integer> bracketCounts) {
		view.setBracketCounts(getBracketHTML(bracketCounts));
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

	private void setEnabledDescriptionsNavigation(boolean value) {
		view.setEnabledNextDescriptionButton(value);
		view.setEnabledPreviousDescriptionButton(value);
	}

	private void setPreprocessedDescription(final PreprocessedDescription preprocessedDescription) {
		semanticMarkupService.getDescription(Authentication.getInstance().getToken(), 
				preprocessedDescription.getFilePath(), preprocessedDescription.getDescriptionNumber(), new AsyncCallback<Description>() {
					@Override
					public void onSuccess(Description result) {
						setText(preprocessedDescription.getFileName(), 
								result, preprocessedDescription.getBracketCounts());
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToGetDescription(caught);
					}
		});
	}
	
	private void setText(String filename, Description description, Map<Character, Integer> bracketCounts) {
		//doesn't work because the getCursorPos uses element.selectionRange. However the element is an iframe with html content for
		// the richtextarea, rather than a simpler html element (html textarea) that implemetns selectionRange...
		/*
		int pos = 0;
		try {
			pos = display.getTextArea().getCursorPos();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
		String text = description.getContent();
		view.setDescriptionIDLabel("Description: " + filename);
		updateBracketCounts(bracketCounts);
		text = bracketColorizer.colorize(text);
		
		/*System.out.println(display.getTextArea().getFormatter().getForeColor());
		System.out.println(display.getTextArea().getFormatter().isBold());
		*/
		view.setHTML(text);
		
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
	
	protected void store(AsyncCallback<Void> callback) {
		String filePath = preprocessedDescriptions.get(currentPreprocessedDescription).getFilePath();
		int descriptionNumber = preprocessedDescriptions.get(currentPreprocessedDescription).getDescriptionNumber();
		String content = view.getHTML();
		semanticMarkupService.setDescription(Authentication.getInstance().getToken(), 
				filePath, descriptionNumber, content, callback);
	}
}
