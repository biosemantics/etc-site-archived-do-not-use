package edu.arizona.biosemantics.etcsite.client.content.settings;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.widget.client.TextButton;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User.EmailPreference;

public class SettingsView extends Composite implements ISettingsView {

	private static SettingsViewUiBinder uiBinder = GWT.create(SettingsViewUiBinder.class);

	@UiTemplate("SettingsView.ui.xml")
	interface SettingsViewUiBinder extends UiBinder<Widget, SettingsView> {}
	
	@UiField
	SimplePanel panel;
	
	private Presenter presenter;
	
	private ShortUser user;
    
	private DisclosurePanel Otoexplanation = new DisclosurePanel("MOre");  

	private TextField bioportalApiKey = new TextField();
	private TextField bioportalUserId = new TextField();
	private TextField affiliation = new TextField();
	private TextField email = new TextField();
	private TextField lastName = new TextField();
	private TextField firstName = new TextField();
	private Button saveButton = new Button("Save");

	private PasswordField confirmPassword = new PasswordField();
	private PasswordField newPassword = new PasswordField();
	private PasswordField currentPassword = new PasswordField();
	private Button changePasswordButton = new Button("Save");
    private Button saveBioportalButton = new Button("save");
    private Button emailNotificationButton = new Button("save");
	
	
	private VerticalLayoutContainer otoVertical;
	private Button otoSaveButton = new Button("Save");

	private CheckBox otoShareCheckBox = new CheckBox();
	private FieldLabel otoShareFieldLabel = new FieldLabel(otoShareCheckBox, "Share Terms with Community");
	private TextField otoAccountEmail = new TextField();
	private FieldLabel otoAccountEmailFieldLabel = new FieldLabel(otoAccountEmail, "Linked OTO Account");
	
	private Radio hasOTOAccount = new Radio();
	private Radio hasNoOTOAccount = new Radio();
	private ToggleGroup hasOTOAccountGroup = new ToggleGroup();
    private HorizontalPanel hasOTOAccountPanel = new HorizontalPanel();
	private FieldLabel hasOtoAccountFieldLabel = new FieldLabel(hasOTOAccountPanel, "Already have an OTO Account?");
	
	private FieldSet newOTOAccountFieldSet = new FieldSet();
	private TextField otoNewEmail = new TextField();
	private FieldLabel otoNewEmailFieldLabel = new FieldLabel(otoNewEmail, "OTO Email");
	private PasswordField otoNewPassword = new PasswordField();
	private FieldLabel otoNewPasswordFieldLabel = new FieldLabel(otoNewPassword, "OTO Password");
	private PasswordField otoNewPasswordConfirm = new PasswordField();
	private FieldLabel otoNewPasswordConfirmFieldLabel = new FieldLabel(otoNewPasswordConfirm, "OTO Password");
	private Button otoNewCreateButton = new Button("Create");
	private Button otoNewAccountGoogleButton = new Button("Create OTO Account using Google");
	
	private FieldSet existingOTOAccountFieldSet = new FieldSet();
	private TextField otoExistingEmail = new TextField();
	private FieldLabel otoExistingEmailFieldLabel = new FieldLabel(otoExistingEmail, "OTO Email");
	private PasswordField otoExistingPassword = new PasswordField();
	private FieldLabel otoExistingPasswordFieldLabel = new FieldLabel(otoExistingPassword, "OTO Password");
	private Button otoExistingLinkButton = new Button("Link");
	private Button otoExistingAccountGoogleButton = new Button("Link OTO Account using Google");
	

	private CheckBox semanticMarkupEmail = new CheckBox();
	private CheckBox matrixGenerationEmail = new CheckBox();
	private CheckBox treeGenerationEmail = new CheckBox();
	private CheckBox taxonomyComparisonEmail = new CheckBox();
	//private TextButton saveEmailPreferencesButton = new TextButton("Save");


	private VerticalLayoutContainer otoVerticalInner;
	
	public SettingsView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		VerticalLayoutContainer vertical = new VerticalLayoutContainer();
		
		FieldSet userInfoFieldSet = new FieldSet();
	    userInfoFieldSet.setHeading("User Information");
	    userInfoFieldSet.setCollapsible(true);
	    VerticalLayoutContainer userInfoVertical = new VerticalLayoutContainer();
	    userInfoFieldSet.add(userInfoVertical);
	    
	    FieldSet profileFieldSet = new FieldSet();
	    profileFieldSet.setHeading("Profile");
	    profileFieldSet.setCollapsible(true);
	    VerticalLayoutContainer profileVertical = new VerticalLayoutContainer();
	    profileFieldSet.add(profileVertical);
	    profileVertical.add(new Label("Note: * denotes required fields."), new VerticalLayoutData(1, 25));
	    
	    firstName.setAllowBlank(false);
	    lastName.setAllowBlank(false);
	    FieldLabel firstNameFieldLabel = new FieldLabel(firstName, "First Name *");
	    firstNameFieldLabel.setLabelWidth(200);
	    profileVertical.add(firstNameFieldLabel, new VerticalLayoutData(1, -1));
	    
	    FieldLabel lastNameFieldLabel = new FieldLabel(lastName, "Last Name *");
	    lastNameFieldLabel.setLabelWidth(200);
	    profileVertical.add(lastNameFieldLabel, new VerticalLayoutData(1, -1));
		
	    email.setEnabled(false);
	    email.setAllowBlank(false);
	    FieldLabel emailFieldLabel = new FieldLabel(email, "Email *");
	    emailFieldLabel.setLabelWidth(200);
	    profileVertical.add(emailFieldLabel, new VerticalLayoutData(1, -1));
	    
	    FieldLabel affiliationFieldLabel = new FieldLabel(affiliation, "Affiliation");
	    affiliationFieldLabel.setLabelWidth(200);
	    profileVertical.add(affiliationFieldLabel, new VerticalLayoutData(1, -1));
	    
	    profileVertical.add(saveButton, new VerticalLayoutData(1, -1));
	    
	    FieldSet passwordFieldSet = new FieldSet();
	    passwordFieldSet.setHeading("Change Password");
	    passwordFieldSet.setCollapsible(true);
	    VerticalLayoutContainer passwordVertical = new VerticalLayoutContainer();
	    FieldLabel currentPasswordFieldLabel =  new FieldLabel(currentPassword, "Current Password");
	    currentPasswordFieldLabel.setLabelWidth(200);
	    passwordVertical.add(currentPasswordFieldLabel, new VerticalLayoutData(1, -1));
	    FieldLabel newPasswordFieldLabel =  new FieldLabel(newPassword, "New Password");
	    newPasswordFieldLabel.setLabelWidth(200);
	    passwordVertical.add(newPasswordFieldLabel, new VerticalLayoutData(1, -1));
	    FieldLabel confirmPasswordFieldLabel =  new FieldLabel(confirmPassword, "Confirm Password");
	    confirmPasswordFieldLabel.setLabelWidth(200);
	    passwordVertical.add(confirmPasswordFieldLabel, new VerticalLayoutData(1, -1));
	    passwordFieldSet.add(passwordVertical);	  
	    passwordVertical.add(changePasswordButton, new VerticalLayoutData(1, -1));
	    
	    userInfoVertical.add(profileFieldSet, new VerticalLayoutData(1, -1));
	    
	    userInfoVertical.add(passwordFieldSet, new VerticalLayoutData(1, -1));
	    userInfoVertical.forceLayout();
	    
	    
	    
	    FieldSet bioportNotificationFieldSet = new FieldSet();
	    VerticalLayoutContainer bioportNotificationVertical = new VerticalLayoutContainer();
	    bioportNotificationFieldSet.setHeading("BioPortal connection");
	    bioportNotificationFieldSet.add(bioportNotificationVertical);
	    bioportNotificationFieldSet.setCollapsible(true);
	    bioportNotificationVertical.add(new Label("Note: Needed for submitting ontology terms to BioPortal."),new VerticalLayoutData(1, 25));
	    
	    FieldLabel bioportalFieldLabel = new FieldLabel(bioportalUserId, "BioPortal User Id");
	    bioportalFieldLabel.setLabelWidth(200);
	    bioportNotificationVertical.add(bioportalFieldLabel, new VerticalLayoutData(1, -1));
	    
	    FieldLabel bioportalApiKeyFieldLabel = new FieldLabel(bioportalApiKey, "BioPortal API Key");
	    bioportalApiKeyFieldLabel.setLabelWidth(200);
	    bioportNotificationVertical.add(bioportalApiKeyFieldLabel, new VerticalLayoutData(1, 28));
	    bioportNotificationVertical.add(saveBioportalButton, new VerticalLayoutData(1, -1));
	    
	    
	    FieldSet emailNotificationFieldSet = new FieldSet();
	    emailNotificationFieldSet.setHeading("Email Notification");
	    VerticalLayoutContainer emailNotificationVertical = new VerticalLayoutContainer();
	    emailNotificationFieldSet.setCollapsible(true);
	    emailNotificationFieldSet.add(emailNotificationVertical);
	    emailNotificationVertical.add(new Label("Note: Uncheck a box to disable the email notification function of a tool."),new VerticalLayoutData(1, 25));
	    FieldLabel textCaptureFieldLabel = new FieldLabel(this.semanticMarkupEmail, "Text Capture");
	    textCaptureFieldLabel.setLabelWidth(200);
	    semanticMarkupEmail.setBoxLabel("");
	    emailNotificationVertical.add(textCaptureFieldLabel, new VerticalLayoutData(1, -1));
	    FieldLabel matrixGenerationFieldLabel = new FieldLabel(this.matrixGenerationEmail, "Matrix Generation");
	    matrixGenerationFieldLabel.setLabelWidth(200);
	    matrixGenerationEmail.setBoxLabel("");
	    emailNotificationVertical.add(matrixGenerationFieldLabel, new VerticalLayoutData(1, -1));
	    FieldLabel treeGenerationFieldLabel = new FieldLabel(this.treeGenerationEmail, "Key Generation");
	    emailNotificationVertical.add(treeGenerationFieldLabel, new VerticalLayoutData(1, -1));
	    treeGenerationFieldLabel.setLabelWidth(200);
	    treeGenerationEmail.setBoxLabel("");
	    FieldLabel taxonomyComparisonFieldLabel = new FieldLabel(this.taxonomyComparisonEmail, "Taxonomy Comparison");
	    taxonomyComparisonFieldLabel.setLabelWidth(200);
	    taxonomyComparisonEmail.setBoxLabel("");
	    emailNotificationVertical.add(taxonomyComparisonFieldLabel, new VerticalLayoutData(1, -1));
	    emailNotificationVertical.add(emailNotificationButton, new VerticalLayoutData(1, -1));
	    //userInfoVertical.add(bioportNotificationFieldSet, new VerticalLayoutData(1, -1));
	    //userInfoVertical.add(emailNotificationFieldSet, new VerticalLayoutData(1, -1));
	    
	    
	    	    
	    /*FieldSet emailPreferencesFieldSet = new FieldSet();
	    emailPreferencesFieldSet.setHeadingText("Email Notification");
	    emailPreferencesFieldSet.setCollapsible(true); 
	    VerticalLayoutContainer emailPreferencesVertical = new VerticalLayoutContainer();
	    emailPreferencesFieldSet.add(emailPreferencesVertical);
	    emailPreferencesVertical.add(new FieldLabel(this.matrixGenerationEmail, "Text Capture Task"), new VerticalLayoutData(1, -1));
	    emailPreferencesVertical.add(new FieldLabel(this.matrixGenerationEmail, "Matrix Generation Task"), new VerticalLayoutData(1, -1));
	    emailPreferencesVertical.add(new FieldLabel(this.taxonomyComparisonEmail, "Taxonomy Comparison Task"), new VerticalLayoutData(1, -1));
	    emailPreferencesVertical.add(saveEmailPreferencesButton, new VerticalLayoutData(1, -1));
	    */
	    
	    
	    FieldSet otoFieldSet = new FieldSet();
	    otoFieldSet.setHeading("Link to Ontology Term Organizer (OTO)");
	    otoFieldSet.setCollapsible(true);
	    otoVertical = new VerticalLayoutContainer();
	    otoVerticalInner = new VerticalLayoutContainer();
	    otoVerticalInner.add(otoShareFieldLabel, new VerticalLayoutData(1, -1));
	    otoShareFieldLabel.setLabelWidth(200);
	    otoFieldSet.add(otoVertical);
	    HorizontalLayoutContainer otonote = new HorizontalLayoutContainer();
	    Anchor otolink = new Anchor("http://biosemantics.arizona.edu/OTO",false,"http://biosemantics.arizona.edu/OTO","_blank");
	    otonote.add(new Label("OTO is a community consensus-promoting term categorization application ( "), new HorizontalLayoutData(-1, -1));
	    otonote.add(otolink, new HorizontalLayoutData(-1, -1));
	    otonote.add(new Label("). The terms categorized on ETC site can be"),new HorizontalLayoutData(-1, -1));
	    
	    otoVertical.add(otonote,new VerticalLayoutData(1, 14));
	    
	    otoVertical.add(new Label(" directly imported into OTO with you as the owner of the term set. You and others can co-categorize the terms on OTO and help grow the consensus-based glossary/ontology for your group and improve the performance of biodiversity software applications including ETC tools."),new VerticalLayoutData(-1, 40));
	    //)+ otolink +"). The terms categorized on ETC site can be directly imported into OTO with you as the owner of the term set. You and others can co-categorize the terms on OTO and help grow the consensus-based glossary/ontology for your group and improve the performance of biodiversity software applications including ETC tools."),new VerticalLayoutData(-1, 50));
	    otoVertical.add(otoVerticalInner, new VerticalLayoutData(1, -1));
	    otoVertical.add(otoSaveButton, new VerticalLayoutData(1, -1));
	    otoShareCheckBox.setBoxLabel("");
	    
	    VerticalLayoutData layoutData = new VerticalLayoutData();
	    layoutData.setMargins(new Margins(10));
	    vertical.add(userInfoFieldSet, layoutData);
	    vertical.add(emailNotificationFieldSet, layoutData);
	    vertical.add(bioportNotificationFieldSet, layoutData);
	    vertical.add(otoFieldSet, layoutData);

	    //vertical.add(emailPreferencesFieldSet, layoutData);
	    panel.add(vertical);


	    hasOTOAccount.setBoxLabel("Yes");
	    hasNoOTOAccount.setBoxLabel("No");
	    hasOTOAccountPanel.add(hasOTOAccount);
	    hasOTOAccountPanel.add(hasNoOTOAccount);
	    hasOTOAccountGroup.add(hasOTOAccount);
	    hasOTOAccountGroup.add(hasNoOTOAccount);
	    hasOtoAccountFieldLabel.setLabelWidth(200);
	    
	    existingOTOAccountFieldSet.setHeading(SafeHtmlUtils.fromString("OTO Account Login"));
	    HtmlLayoutContainer choiceContatiner = new HtmlLayoutContainer(SafeHtmlUtils.fromString("<table width=100% cellpadding=0 cellspacing=0>"
	    		+ "<tr>"
	    		+ "<td class=email width=50%></td>"
	    		+ "<td width=20%>or</td>"
	    		+ "<td class=google width=50%></td>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=password></td>"
	    		+ "<td></td>"
	    		+ "<td></td>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=create></td>"
	    		+ "<td></td>"
	    		+ "<td></td>"
	    		+ "</tr>"
	    		+ "</table>"));
	    otoExistingEmailFieldLabel.setLabelWidth(200);
	    otoExistingPasswordFieldLabel.setLabelWidth(200);
	    otoAccountEmailFieldLabel.setLabelWidth(200);
	    choiceContatiner.add(otoExistingEmailFieldLabel, new HtmlData(".email"));
	    choiceContatiner.add(otoExistingPasswordFieldLabel, new HtmlData(".password"));
	    choiceContatiner.add(otoExistingLinkButton, new HtmlData(".create"));
	    choiceContatiner.add(otoExistingAccountGoogleButton, new HtmlData(".google"));
	    existingOTOAccountFieldSet.add(choiceContatiner);
	    otoAccountEmail.setEnabled(false);
	    
	    newOTOAccountFieldSet.setHeading(SafeHtmlUtils.fromString("Create OTO Account"));
	    choiceContatiner = new HtmlLayoutContainer(SafeHtmlUtils.fromString("<table width=100% cellpadding=0 cellspacing=0>"
	    		+ "<tr>"
	    		+ "<td class=email width=50%></td>"
	    		+ "<td width=20%>or</td>"
	    		+ "<td class=google width=50%></td>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=password></td>"
	    		+ "<td></td>"
	    		+ "<td></td>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=passwordConfirm></td>"
	    		+ "<td></td>"
	    		+ "<td></td>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=create></td>"
	    		+ "<td></td>"
	    		+ "<td></td>"
	    		+ "</tr>"
	    		+ "</table>"));
	    otoNewEmailFieldLabel.setLabelWidth(200);
	    otoNewPasswordFieldLabel.setLabelWidth(200);
	    otoNewPasswordConfirmFieldLabel.setLabelWidth(200);
	    choiceContatiner.add(otoNewEmailFieldLabel, new HtmlData(".email"));
	    choiceContatiner.add(otoNewPasswordFieldLabel, new HtmlData(".password"));
	    choiceContatiner.add(otoNewPasswordConfirmFieldLabel, new HtmlData(".passwordConfirm"));
	    choiceContatiner.add(otoNewCreateButton, new HtmlData(".create"));
	    choiceContatiner.add(otoNewAccountGoogleButton, new HtmlData(".google"));
	    newOTOAccountFieldSet.add(choiceContatiner); 
	        
	    bindEvents();
	    
	    vertical.forceLayout();
	}

	private void bindEvents() {
		otoShareCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean share = event.getValue();
				if(otoAccountEmailFieldLabel.isAttached()) {
					
				} else {
					if(share) {		
						otoVerticalInner.add(hasOtoAccountFieldLabel, new VerticalLayoutData(1, -1));
					} else {
						resetShareOTO();
					}
				}
			}
		});
		
		
		hasOTOAccount.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean hasAccount = event.getValue();
				if(hasAccount) {
					otoVerticalInner.add(existingOTOAccountFieldSet);
					otoExistingEmail.setValue("");
					otoExistingPassword.setValue("");
					otoVerticalInner.remove(newOTOAccountFieldSet);
					
				} else {
					otoVerticalInner.remove(existingOTOAccountFieldSet);
					otoNewEmail.setValue("");
					otoNewPassword.setValue("");
					otoNewPasswordConfirm.setValue("");
					otoVerticalInner.add(newOTOAccountFieldSet);
				}
			}
		});	
		hasNoOTOAccount.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean hasNoAccount = event.getValue();
				if(hasNoAccount) {
					otoVerticalInner.remove(existingOTOAccountFieldSet);
					otoVerticalInner.add(newOTOAccountFieldSet);
				} else {
					otoVerticalInner.add(existingOTOAccountFieldSet);
					otoVerticalInner.remove(newOTOAccountFieldSet);
				}
			}
		});		

		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onSave();
			}
		});
		/*saveEmailPreferencesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onSaveEmailPreferences();
			}
		});*/
		
		changePasswordButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onPasswordSave();
			}
		});
		
		emailNotificationButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onEmailNotification();
			}
		});
		
		saveBioportalButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onSaveBioportal();
			}
		});
		
		this.otoNewAccountGoogleButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onNewOTOGoogleAccount();
			}
		});
		this.otoNewCreateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onNewOTOAccount(otoNewEmail.getText(), otoNewPassword.getText(), otoNewPasswordConfirm.getText());
			}
		});
		this.otoExistingLinkButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onLinkAccount(otoShareCheckBox.getValue(), otoExistingEmail.getText(), otoExistingPassword.getText());
			}
		});
		this.otoExistingAccountGoogleButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onExistingOTOGoogleAccount();
			}
		});
		this.otoSaveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {			
				presenter.onSaveOTOAccount(otoShareCheckBox.getValue(), otoExistingEmail.getText(), otoExistingPassword.getText());
			}
		});
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setData(ShortUser user){
		firstName.setValue(user.getFirstName());
		lastName.setValue(user.getLastName());
		email.setValue(user.getEmail());
		affiliation.setValue(user.getAffiliation());
		bioportalUserId.setValue(user.getBioportalUserId());
		bioportalApiKey.setValue(user.getBioportalApiKey());
		currentPassword.setValue("");
		newPassword.setValue("");
		confirmPassword.setValue("");
		
		if ((user.getProfile()) == null || (user.getProfile()).isEmpty()) {
			matrixGenerationEmail.setValue(true);
			treeGenerationEmail.setValue(true);
			semanticMarkupEmail.setValue(true);
			taxonomyComparisonEmail.setValue(true);
		} else {
			matrixGenerationEmail.setValue(user.getProfileValue(EmailPreference.MATRIX_GENERATION.getKey()));
			treeGenerationEmail.setValue(user.getProfileValue(EmailPreference.TREE_GENERATION.getKey()));
			semanticMarkupEmail.setValue(user.getProfileValue(EmailPreference.TEXT_CAPTURE.getKey()));
			taxonomyComparisonEmail.setValue(user.getProfileValue(EmailPreference.TAXONOMY_COMPARISON.getKey()));
		}

		if (user.getOtoAccountEmail() != null
				&& !user.getOtoAccountEmail().isEmpty()) {
			setLinkedOTOAccount(user.getOtoAccountEmail());
		} else {
			resetShareOTO();
		}
		
		firstName.setEnabled(true);
		lastName.setEnabled(true);
		affiliation.setEnabled(true);
		bioportalUserId.setEnabled(true);
		bioportalApiKey.setEnabled(true);
		currentPassword.setEnabled(true);
		newPassword.setEnabled(true);
		confirmPassword.setEnabled(true);
		
		this.user = user;
		
		if (!user.getOpenIdProvider().equals("none")){
			firstName.setEnabled(false);
			lastName.setEnabled(false);
			currentPassword.setEnabled(false);
			newPassword.setEnabled(false);
			confirmPassword.setEnabled(false);
		}
	}
	
	private void resetShareOTO() {
		otoVerticalInner.remove(otoAccountEmailFieldLabel);
		otoVerticalInner.remove(hasOtoAccountFieldLabel);
		otoVerticalInner.remove(existingOTOAccountFieldSet);
		otoVerticalInner.remove(newOTOAccountFieldSet);
		hasOTOAccount.clear();
		hasNoOTOAccount.clear();
		otoShareCheckBox.setValue(false);
	}

	@Override
	public ShortUser getData() {
		ShortUser user = new ShortUser();
		user.setAffiliation(affiliation.getText());
		user.setBioportalApiKey(bioportalApiKey.getText());
		user.setBioportalUserId(bioportalUserId.getText());
		user.setEmail(email.getText());
		user.setFirstName(firstName.getText());
		user.setLastName(lastName.getText());		
		user.setProfileValue(EmailPreference.MATRIX_GENERATION.getKey()
				.toString(), matrixGenerationEmail.getValue());
		user.setProfileValue(EmailPreference.TEXT_CAPTURE.getKey().toString(),
				semanticMarkupEmail.getValue());
		user.setProfileValue(EmailPreference.TAXONOMY_COMPARISON.getKey()
				.toString(), taxonomyComparisonEmail.getValue());
		user.setProfileValue(EmailPreference.TREE_GENERATION.getKey()
				.toString(), treeGenerationEmail.getValue());
		return user;
	}
	
	@Override
	public Map<String, Boolean> getEmailPreference() {
		Map<String,Boolean> emailPreference = new HashMap<String, Boolean>();
		emailPreference.put(EmailPreference.MATRIX_GENERATION.getKey()
				.toString(), matrixGenerationEmail.getValue());		
		emailPreference.put(EmailPreference.TEXT_CAPTURE.getKey().toString(),
				semanticMarkupEmail.getValue());
		emailPreference.put(EmailPreference.TAXONOMY_COMPARISON.getKey()
				.toString(), taxonomyComparisonEmail.getValue());
		emailPreference.put(EmailPreference.TREE_GENERATION.getKey()
				.toString(), treeGenerationEmail.getValue());
		return emailPreference;
	}
	
	@Override
	public void setOTOAccount(String email, String password) {
		otoExistingEmail.setText(email);
		otoExistingPassword.setText(password);
	}
	
	@Override
	public void setLinkedOTOAccount(String otoAccountEmail) {
		otoShareCheckBox.setValue(true);
		this.otoAccountEmail.setValue(otoAccountEmail);
		otoVerticalInner.add(otoAccountEmailFieldLabel, new VerticalLayoutData(1, -1));
		otoVerticalInner.remove(hasOtoAccountFieldLabel);
		otoVerticalInner.remove(existingOTOAccountFieldSet);
		otoVerticalInner.remove(newOTOAccountFieldSet);
		otoVerticalInner.forceLayout();
	}
	
	@Override
	public boolean isLinkedOTOAccount() {
		return otoShareCheckBox.getValue() && (otoVerticalInner.getWidget(otoVerticalInner.getWidgetCount() -1)).equals(otoAccountEmailFieldLabel);
	}
	
	@Override
	public String getFirstName() {
		return firstName.getText();
	}
	
	@Override
	public String getLastName() {
		return lastName.getText();
	}
	
	@Override
	public String getAffiliation() {
		return affiliation.getText();
	}
	
	@Override
	public String getBioportalApiKey() {
		return bioportalApiKey.getText();
	}
	
	@Override
	public String getBioportalUserId() {
		return bioportalUserId.getText();
	}

	@Override
	public String getCurrentPassword() {
		return currentPassword.getText();
	}
	
	@Override
	public String getNewPassword() {
		return newPassword.getText();
	}
	

	public String getConfirmPassword() {
		return confirmPassword.getText();
	}

	@Override
	public boolean isMatrixGenerationEmailChecked() {
		return matrixGenerationEmail.getValue();
	}

	@Override
	public boolean isTreeGenerationEmailChecked() {
		return treeGenerationEmail.getValue();
	}

	@Override
	public boolean isTextCaptureEmailChecked() {
		return semanticMarkupEmail.getValue();
	}

	@Override
	public boolean isTaxonomyComparisonEmailChecked() {
		return taxonomyComparisonEmail.getValue();
	}
}
