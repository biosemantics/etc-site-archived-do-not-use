package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;
import java.util.Map;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.BracketValidator;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;

public class SemanticMarkupPreprocessPresenter implements ISemanticMarkupPreprocessView.Presenter {

	private Task task;
	private ISemanticMarkupPreprocessView view;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private List<PreprocessedDescription> preprocessedDescriptions;
	private int currentPreprocessedDescription;
	private BracketColorizer bracketColorizer;
	private BracketValidator bracketValidator;
	private PlaceController placeController;
	private MessagePresenter messagePresenter;
	
	@Inject
	public SemanticMarkupPreprocessPresenter(ISemanticMarkupPreprocessView view, 
			PlaceController placeController,
			ISemanticMarkupServiceAsync semanticMarkupService, 
			BracketColorizer bracketColorizer, BracketValidator bracketValidator, 
			MessagePresenter messagePresenter) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.bracketColorizer = bracketColorizer;
		this.bracketValidator = bracketValidator;
		this.messagePresenter = messagePresenter;
	}

	@Override
	public void setTask(final Task task) {
		this.task = task;
		semanticMarkupService.preprocess(Authentication.getInstance().getToken(), task, new RPCCallback<List<PreprocessedDescription>>() {
			@Override
			public void onResult(List<PreprocessedDescription> result) {
                if(result.isEmpty()) {
                    semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.LEARN_TERMS, new RPCCallback<Task>() {
						@Override
						public void onResult(Task result) {
							placeController.goTo(new SemanticMarkupLearnPlace(task));
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
			messagePresenter.showMessage("Unmatched Brackets", "You have not corrected all the unmatched brackets.");
		}
	}

	private void storeAndLeave() {
		store(new RPCCallback<Void>() {
			@Override
			public void onResult(Void result) {	
				semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, 
						TaskStageEnum.LEARN_TERMS, new RPCCallback<Task>() {
							@Override
							public void onResult(Task task) {
								placeController.goTo(new SemanticMarkupLearnPlace(task));
							} 
				});
			}
		});
	}

	@Override
	public void onNextDescription() {
		store(new RPCCallback<Void>() {
			@Override
			public void onResult(Void result) {
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
		});
	}

	@Override
	public void onPreviousDescription() {
		store(new RPCCallback<Void>() {
			@Override
			public void onResult(Void result) {
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
		try {
			semanticMarkupService.getDescription(Authentication.getInstance().getToken(), 
					preprocessedDescription.getFilePath(), new RPCCallback<String>() {
						@Override
						public void onResult(String result) {
							setText(preprocessedDescription.getFileName(), 
									result, preprocessedDescription.getBracketCounts());
						}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setText(String descriptionId, String text, Map<Character, Integer> bracketCounts) {
		//doesn't work because the getCursorPos uses element.selectionRange. However the element is an iframe with html content for
		// the richtextarea, rather than a simpler html element (html textarea) that implemetns selectionRange...
		/*
		int pos = 0;
		try {
			pos = display.getTextArea().getCursorPos();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
		view.setDescriptionIDLabel("Description: " + descriptionId);
		updateBracketCounts(bracketCounts);
		text = bracketColorizer.colorize(text);
		System.out.println(text);
		
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
	
	protected void store(RPCCallback<Void> callback) {
		String target = preprocessedDescriptions.get(currentPreprocessedDescription).getFilePath();
		String content = view.getHTML();
		semanticMarkupService.setDescription(Authentication.getInstance().getToken(), 
				target, content, callback);
	}
}
