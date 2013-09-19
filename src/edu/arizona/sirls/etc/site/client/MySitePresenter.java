package edu.arizona.sirls.etc.site.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
import edu.arizona.sirls.etc.site.client.event.FileManagerEventHandler;
import edu.arizona.sirls.etc.site.client.event.HelpEvent;
import edu.arizona.sirls.etc.site.client.event.HelpEventHandler;
import edu.arizona.sirls.etc.site.client.event.HomeEvent;
import edu.arizona.sirls.etc.site.client.event.HomeEventHandler;
import edu.arizona.sirls.etc.site.client.event.LoginEvent;
import edu.arizona.sirls.etc.site.client.event.LoginEventHandler;
import edu.arizona.sirls.etc.site.client.event.LogoutEvent;
import edu.arizona.sirls.etc.site.client.event.LogoutEventHandler;
import edu.arizona.sirls.etc.site.client.event.SettingsEvent;
import edu.arizona.sirls.etc.site.client.event.SettingsEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaxonomyComparisonEvent;
import edu.arizona.sirls.etc.site.client.event.TaxonomyComparisonEventHandler;
import edu.arizona.sirls.etc.site.client.event.TreeGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.TreeGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.VisualizationEvent;
import edu.arizona.sirls.etc.site.client.event.VisualizationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.InputMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.InputMatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.LearnMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.LearnMatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.MatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.OutputMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.OutputMatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ParseMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ParseMatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.PreprocessMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.PreprocessMatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ReviewMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ReviewMatrixGenerationEventHandler;
import edu.arizona.sirls.etc.site.client.presenter.FooterPresenter;
import edu.arizona.sirls.etc.site.client.presenter.HelpPresenter;
import edu.arizona.sirls.etc.site.client.presenter.LoggedInHeaderPresenter;
import edu.arizona.sirls.etc.site.client.presenter.LoggedOutHeaderPresenter;
import edu.arizona.sirls.etc.site.client.presenter.LoginPresenter;
import edu.arizona.sirls.etc.site.client.presenter.MainMenuPresenter;
import edu.arizona.sirls.etc.site.client.presenter.SettingsPresenter;
import edu.arizona.sirls.etc.site.client.presenter.StartMenuPresenter;
import edu.arizona.sirls.etc.site.client.presenter.StartPresenter;
import edu.arizona.sirls.etc.site.client.presenter.TaskManagerPresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileManagerPresenter;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.InputMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.LearnMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.OutputMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ParseMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.PreprocessMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ReviewMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.taxonomyComparison.TaxonomyComparisonPresenter;
import edu.arizona.sirls.etc.site.client.presenter.treeGeneration.TreeGenerationPresenter;
import edu.arizona.sirls.etc.site.client.presenter.visualization.VisualizationPresenter;
import edu.arizona.sirls.etc.site.client.view.FooterView;
import edu.arizona.sirls.etc.site.client.view.HelpView;
import edu.arizona.sirls.etc.site.client.view.LoggedInHeaderView;
import edu.arizona.sirls.etc.site.client.view.LoggedOutHeaderView;
import edu.arizona.sirls.etc.site.client.view.LoginView;
import edu.arizona.sirls.etc.site.client.view.MainMenuView;
import edu.arizona.sirls.etc.site.client.view.SettingsView;
import edu.arizona.sirls.etc.site.client.view.StartMenuView;
import edu.arizona.sirls.etc.site.client.view.StartView;
import edu.arizona.sirls.etc.site.client.view.TaskManagerView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileManagerView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.InputMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.LearnMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.OutputMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.ParseMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.PreprocessMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.ReviewMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.taxonomyComparison.TaxonomyComparisonView;
import edu.arizona.sirls.etc.site.client.view.treeGeneration.TreeGenerationView;
import edu.arizona.sirls.etc.site.client.view.visualization.VisualizationView;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.PreprocessedDescription;

public class MySitePresenter implements SitePresenter, ValueChangeHandler<String> {

	private final IAuthenticationServiceAsync authenticationService = GWT.create(IAuthenticationService.class);
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private final IFileFormatServiceAsync fileFormatService = GWT.create(IFileFormatService.class);
	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	private final ITaskServiceAsync taskService = GWT.create(ITaskService.class);
	private final IMatrixGenerationServiceAsync matrixGenerationService = GWT.create(IMatrixGenerationService.class);
	
	private HandlerManager eventBus;
	
	private HasWidgets footer;
	private HasWidgets content;
	private HasWidgets header;
	private HasWidgets menu;
	
	private MatrixGenerationJob matrixGenerationJob;

	protected MainMenuPresenter mainMenuPresenter;
	protected StartMenuPresenter startMenuPresenter;
	private FooterPresenter footerPresenter;
	protected LoggedInHeaderPresenter loggedInHeaderPresenter;
	protected LoggedOutHeaderPresenter loggedOutHeaderPresenter;
	protected VisualizationPresenter visualizationPresenter;
	protected TaxonomyComparisonPresenter taxonomyComparisonPresenter;
	protected TreeGenerationPresenter treeGenerationPresenter;
	protected OutputMatrixGenerationPresenter outputMatrixGenerationPresenter;
	protected ParseMatrixGenerationPresenter parseMatrixGenerationPresenter;
	protected ReviewMatrixGenerationPresenter reviewMatrixGenerationPresenter;
	protected LearnMatrixGenerationPresenter learnMatrixGenerationPresenter;
	protected PreprocessMatrixGenerationPresenter preprocessMatrixGenerationPresenter;
	protected InputMatrixGenerationPresenter inputMatrixGenerationPresenter;
	protected SettingsPresenter settingsPresenter;
	protected FileManagerPresenter fileManagerPresenter;
	protected TaskManagerPresenter taskManagerPresenter;
	protected LoginPresenter loginPresenter;
	protected HelpPresenter helpPresenter;
	protected StartPresenter startPresenter;
	

	public MySitePresenter(HandlerManager eventBus) {
		this.eventBus = eventBus;
		bind();
	}
	
	private void bind() {
	    History.addValueChangeHandler(this);

	    eventBus.addHandler(LogoutEvent.TYPE,
	        new LogoutEventHandler() {
	          public void onLogout(LogoutEvent event) {
	            logout();
	          }
	        });
	    
	    eventBus.addHandler(LoginEvent.TYPE,
		        new LoginEventHandler() {
		          public void onLogin(LoginEvent event) {
		            login(event.getUsername(), event.getSessionID());
		          }
		        });
	    
	    eventBus.addHandler(HomeEvent.TYPE,
		    	new HomeEventHandler() {
		          public void onHome(HomeEvent event) {
		        	  addToHistory(HistoryState.START, false, event);
		          }
		        });
	    
	    eventBus.addHandler(TaskManagerEvent.TYPE,
		    	new TaskManagerEventHandler() {
		          public void onTaskManager(TaskManagerEvent event) {
		        	  addToHistory(HistoryState.TASK_MANAGER, true, event);
		          }
		        });
	    
	    eventBus.addHandler(FileManagerEvent.TYPE,
		    	new FileManagerEventHandler() {
		          public void onFileManager(FileManagerEvent event) {
		        	  addToHistory(HistoryState.FILE_MANAGER, true, event);
		          }
		        });
	    
	    eventBus.addHandler(HelpEvent.TYPE,
		    	new HelpEventHandler() {
		          public void onHelp(HelpEvent event) {
		        	  addToHistory(HistoryState.HELP, false, event);
		          }
		        });
	    
	    eventBus.addHandler(SettingsEvent.TYPE,
		    	new SettingsEventHandler() {
		          public void onSettings(SettingsEvent event) {
		        	  addToHistory(HistoryState.SETTINGS, true, event);
		          }
		        });
	    
	    eventBus.addHandler(MatrixGenerationEvent.TYPE,
	    	new MatrixGenerationEventHandler() {
	          public void onMatrixGeneration(MatrixGenerationEvent event) {
	        	  matrixGenerationJob = new MatrixGenerationJob();
	        	  addToHistory(HistoryState.INPUT_MATRIX_GENERATION, true, event);
	          }
	        });
	    
		eventBus.addHandler(InputMatrixGenerationEvent.TYPE,
				new InputMatrixGenerationEventHandler() {
					@Override
					public void onInput(
							InputMatrixGenerationEvent event) {
						addToHistory(HistoryState.INPUT_MATRIX_GENERATION, true, event);
					}
				});
		
		eventBus.addHandler(PreprocessMatrixGenerationEvent.TYPE,
				new PreprocessMatrixGenerationEventHandler() {
					@Override
					public void onPreprocess(
							final PreprocessMatrixGenerationEvent event) {
						matrixGenerationService.preprocess(Authentication.getInstance().getAuthenticationToken(), 
								matrixGenerationJob, new AsyncCallback<List<PreprocessedDescription>>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(List<PreprocessedDescription> result) {
										matrixGenerationJob.setPreprocessedDescriptions(result);
										if(result.isEmpty())
											addToHistory(HistoryState.LEARN_MATRIX_GENERATION, true, event);
										else
											addToHistory(HistoryState.PREPROCESS_MATRIX_GENERATION, true, event);
									}
						});
						//show some loading screen, while result is not there yet.
						//addToHistory(HistoryState.PREPROCESS_MATRIX_GENERATION, true, event);
					}
				});
		
		eventBus.addHandler(LearnMatrixGenerationEvent.TYPE,
				new LearnMatrixGenerationEventHandler() {
					@Override
					public void onLearn(LearnMatrixGenerationEvent event) {
						addToHistory(HistoryState.LEARN_MATRIX_GENERATION, true, event);
					}
				});
		
		eventBus.addHandler(ParseMatrixGenerationEvent.TYPE,
				new ParseMatrixGenerationEventHandler() {
					@Override
					public void onParse(ParseMatrixGenerationEvent event) {
						addToHistory(HistoryState.PARSE_MATRIX_GENERATION, true, event);
					}
				});
		
		eventBus.addHandler(ReviewMatrixGenerationEvent.TYPE,
				new ReviewMatrixGenerationEventHandler() {
					@Override
					public void onReview(ReviewMatrixGenerationEvent event) {
						addToHistory(HistoryState.REVIEW_MATRIX_GENERATION, true, event);
					}
				});
		
		eventBus.addHandler(OutputMatrixGenerationEvent.TYPE,
				new OutputMatrixGenerationEventHandler() {
					@Override
					public void onOutput(OutputMatrixGenerationEvent event) {
						addToHistory(HistoryState.OUTPUT_MATRIX_GENERATION, true, event);
					}
				});
	    
	    eventBus.addHandler(TreeGenerationEvent.TYPE,
		    	new TreeGenerationEventHandler() {
		          public void onTreeGeneration(TreeGenerationEvent event) {
		        	  addToHistory(HistoryState.TREE_GENERATION, true, event);
		          }
		        });
	    
	    eventBus.addHandler(TaxonomyComparisonEvent.TYPE,
		    	new TaxonomyComparisonEventHandler() {
		          public void onTaxonomyComparison(TaxonomyComparisonEvent event) {
		        	  addToHistory(HistoryState.TAXONOMY_COMPARISON, true, event);
		          }
		        });
	    
	    eventBus.addHandler(VisualizationEvent.TYPE,
		    	new VisualizationEventHandler() {
		          public void onVisualization(VisualizationEvent event) {
		        	  addToHistory(HistoryState.VISUALIZATION, true, event);
		          }
		        });
	}
	
	protected void addToHistory(HistoryState historyState, boolean requiresLogin, GwtEvent<?> event) {
		if(!requiresLogin || Authentication.getInstance().isSet())
			History.newItem(historyState.toString());
		else
			presentLogin(event);
	}

	private void logout() {
		Authentication.getInstance().destory();
		this.presentHeader();
	}
	
	protected void login(String username, String sessionID) {
		Authentication.getInstance().setUsername(username);
		Authentication.getInstance().setSessionID(sessionID);
		this.presentHeader();
	}
	
	@Override
	public void go(HasWidgets header, HasWidgets menu, HasWidgets content, HasWidgets footer) {
	    this.header = header;
	    this.menu = menu;
	    this.content = content;
	    this.footer = footer;
	    
	    if ("".equals(History.getToken())) {
	      History.newItem(HistoryState.START.toString());
	    }
	    else {
	      History.fireCurrentHistoryState();
	    }
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		HistoryState historyState = HistoryState.valueOf(event.getValue());
		presentHeader();
		presentMenu(historyState);
		presentContent(historyState);
		presentFooter();
	}
	
	private void presentLogin(final GwtEvent<?> event) {
		GWT.runAsync(new RunAsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			public void onSuccess() {
				if (loginPresenter == null) 
					loginPresenter = new LoginPresenter(eventBus, new LoginView(), 
							authenticationService, 
							"You have to login to use this functionality");
				loginPresenter.setTarget(event);
				loginPresenter.go();
			}
		});
	}

	private void presentMenu(HistoryState historyState) {
		if (historyState != null) {
			switch(historyState) {
				case START:
					showStartMenu();
					break;
				default:
					if(Authentication.getInstance().isSet()) {
						GWT.runAsync(new RunAsyncCallback() {
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
							public void onSuccess() {					
								if (mainMenuPresenter == null) {
									mainMenuPresenter = new MainMenuPresenter(eventBus, new MainMenuView());
								}
								mainMenuPresenter.go(menu);
							}
						});
					} else {
						showStartMenu();
					}
			}
		}
	}

	private void showStartMenu() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			public void onSuccess() {					
				if (startMenuPresenter == null) {
					startMenuPresenter = new StartMenuPresenter(eventBus, new StartMenuView());
				}
				startMenuPresenter.go(menu);
			}
		});
	}

	private void presentContent(HistoryState historyState) {
		if (historyState != null) {
			//switch on non-/authentication requiring states first
			switch(historyState) {
			case START:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
					}
					public void onSuccess() {
						// lazily initialize our views, and keep them around to
						// be reused
						//							
						if (startPresenter == null) {
							startPresenter = new StartPresenter(eventBus, new StartView());
						}
						startPresenter.go(content);
					}
				});
				break;
			case HELP:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (helpPresenter == null) {
							helpPresenter = new HelpPresenter(eventBus, new HelpView());
						}
						helpPresenter.go(content);
					}
				});
				break;
			case TASK_MANAGER:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (taskManagerPresenter == null) {
							taskManagerPresenter = new TaskManagerPresenter(eventBus, 
									new TaskManagerView(), taskService);
						}
						taskManagerPresenter.go(content);
					}
				});
				break;
			case FILE_MANAGER:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (fileManagerPresenter == null) {
							fileManagerPresenter = new FileManagerPresenter(eventBus, new FileManagerView(), fileService);
						}
						fileManagerPresenter.go(content);
					}
				});
				break;
			case SETTINGS:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (settingsPresenter == null) {
							settingsPresenter = new SettingsPresenter(eventBus, new SettingsView());
						}
						settingsPresenter.go(content);
					}
				});
				break;
			case INPUT_MATRIX_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (inputMatrixGenerationPresenter == null) {
							inputMatrixGenerationPresenter = new InputMatrixGenerationPresenter(
									eventBus, new InputMatrixGenerationView(), fileService);
						}
						inputMatrixGenerationPresenter.go(content, matrixGenerationJob);
					}
				});
				break;
			case PREPROCESS_MATRIX_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (preprocessMatrixGenerationPresenter == null) {
							preprocessMatrixGenerationPresenter = 									
									new PreprocessMatrixGenerationPresenter(eventBus, 
											new PreprocessMatrixGenerationView(), matrixGenerationService);
						}
						preprocessMatrixGenerationPresenter.go(content, matrixGenerationJob);
					}
				});
				break;
			case LEARN_MATRIX_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (learnMatrixGenerationPresenter == null) {
							learnMatrixGenerationPresenter = new 
									LearnMatrixGenerationPresenter(eventBus, 
											new LearnMatrixGenerationView(), matrixGenerationService);
						}
						learnMatrixGenerationPresenter.go(content, matrixGenerationJob);
					}
				});
				break;
			case REVIEW_MATRIX_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (reviewMatrixGenerationPresenter == null) {
							reviewMatrixGenerationPresenter = 
									new ReviewMatrixGenerationPresenter(eventBus, 
											new ReviewMatrixGenerationView());
						}
						reviewMatrixGenerationPresenter.go(content, matrixGenerationJob);
					}
				});
				break;
			case PARSE_MATRIX_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (parseMatrixGenerationPresenter == null) {
							parseMatrixGenerationPresenter = new ParseMatrixGenerationPresenter(
									eventBus, new ParseMatrixGenerationView());
						}
						parseMatrixGenerationPresenter.go(content, matrixGenerationJob);
					}
				});
				break;
			case OUTPUT_MATRIX_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (outputMatrixGenerationPresenter == null) {
							outputMatrixGenerationPresenter = new 
									OutputMatrixGenerationPresenter(eventBus, 
											new OutputMatrixGenerationView(), fileService);
						}
						outputMatrixGenerationPresenter.go(content, matrixGenerationJob);
					}
				});
				break;
			case TREE_GENERATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (treeGenerationPresenter == null) {
							treeGenerationPresenter = new TreeGenerationPresenter(eventBus, 
									new TreeGenerationView());
						}
						treeGenerationPresenter.go(content);
					}
				});
				break;
			case TAXONOMY_COMPARISON:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (taxonomyComparisonPresenter == null) {
							taxonomyComparisonPresenter = new TaxonomyComparisonPresenter(eventBus, 
									new TaxonomyComparisonView());
						}
						taxonomyComparisonPresenter.go(content);
					}
				});
				break;
			case VISUALIZATION:
				GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					public void onSuccess() {
						if (visualizationPresenter == null) {
							visualizationPresenter = new VisualizationPresenter(eventBus, 
									new VisualizationView());
						}
						visualizationPresenter.go(content);
					}
				});
				break;
			default:
			}
		}
	}

	private void presentHeader() {
		this.authenticationService.isValidSession(Authentication.getInstance().getAuthenticationToken(), 
				new AsyncCallback<AuthenticationResult>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(AuthenticationResult result) {
						if(result.getResult()) {
							if(loggedInHeaderPresenter == null)
								loggedInHeaderPresenter = new LoggedInHeaderPresenter(eventBus, new LoggedInHeaderView());
							loggedInHeaderPresenter.go(header);
						} else {
							if(loggedOutHeaderPresenter == null)
								loggedOutHeaderPresenter = new LoggedOutHeaderPresenter(eventBus, new LoggedOutHeaderView(), 
										authenticationService);
							loggedOutHeaderPresenter.go(header);
						}
					}
		});
	}

	private void presentFooter() {
		if(footerPresenter == null)
			footerPresenter = new FooterPresenter(eventBus, new FooterView());
		footerPresenter.go(footer);
	}
	
}
