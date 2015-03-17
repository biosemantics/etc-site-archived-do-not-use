package edu.arizona.biosemantics.etcsite.client.common;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter.InfoMessageBox;

public class Alerter {
	
	private static AutoProgressMessageBox box;
	
	public static MessageBox startLoading() {
		box = new AutoProgressMessageBox("Loading", "Loading your data, please wait...");
        box.setProgressText("Loading...");
        box.auto();
        box.show();
        return box;
	}
	
	public static void stopLoading() {
		box.hide();
		box = null;
	}

	public static MessageBox failedToCreateTaxonDescription(Throwable caught) {
		return showAlert("Create Taxon Description", "Failed to create taxon description.", caught);
	}
	
	public static MessageBox invalidOTOAccount(Throwable caught) {
		return showAlert("Invalid OTO Account", "Invalid OTO account credentials.", caught);
	}
	
	public static MessageBox failedToCreateOTOAccount(Throwable caught) {
		return showAlert("Failed to Create", "Failed to create OTO Account.", caught);
	}
	
	public static MessageBox failedToSaveOTOAccount(Throwable caught) {
		return showAlert("Failed to Save", "Failed to save OTO Account.", caught);
	}
	
	public static void successfullyCreatedOTOAccount() {
		showInfo("Created Account successfully", "Successfully created OTO Account.");
	}
	
	public static MessageBox failedToImportOto(Throwable caught) {
		return showAlert("Import Term Categorizations", "Failed to import term categorizations.", caught);
	}

	public static MessageBox failedToCreateFile(Throwable caught) {
		return showAlert("Create File", "Failed to create file.", caught);
	}

	public static MessageBox failedToCheckValidityOfTaxonDescription(Throwable caught) {
		return showAlert("Taxon Description Validation", "Validation of taxon description failed.", caught);
	}

	public static MessageBox failedToSetContent(Throwable caught) {
		return showAlert("Set File Content", "Failed to set file content.", caught);
	}

	public static MessageBox failedToAuthenticate(Throwable caught) {
		return showAlert("Authentication", "Failed to authenticate.", caught);
	}

	public static MessageBox failedToRetrieveSetup(Throwable caught) {
		return showAlert("Setup Retrieval", "Failed to retrieve Setup.", caught);
	}

	public static MessageBox failedToCreateCaptcha(Throwable caught) {
		return showAlert("Captcha Creation", "Failed to create captcha.", caught);
	}

	public static MessageBox failedToRegister(Throwable caught) {
		return showAlert("Registration", "Failed to register.", caught);
	}

	public static MessageBox failedToRequestPasswordResetCode(Throwable caught) {
		return showAlert("Password Reset", "Failed to request password reset code.", caught);
	}

	public static MessageBox failedToResetPassword(Throwable caught) {
		return showAlert("Password Reset", "Failed to reset password.", caught);
	}

	public static MessageBox failedToGetFileContent(Throwable caught) {
		return showAlert("Get File Content", "Failed to get file content.", caught);
	}

	public static MessageBox failedToValidateXmlContentForSchema(Throwable caught) {
		return showAlert("Validate XML", "Failed to validate xml.", caught);
	}

	public static MessageBox failedToSetFileContent(Throwable caught) {
		return showAlert("Set File Content", "Failed to set file content.", caught);
	}

	public static MessageBox failedToSetSchema(Throwable caught) {
		return showAlert("Set Schema", "Failed to set schema.", caught);
	}

	public static MessageBox failedToGetSchema(Throwable caught) {
		return showAlert("Get Schema", "Failed to get schema.", caught);
	}

	public static MessageBox failedToIsDirectory(Throwable caught) {
		return showAlert("Directory Check", "Failed to check directory.", caught);
	}

	public static MessageBox failedToMoveFile(Throwable caught) {
		return showAlert("Move File", "Failed to move file,\n" + caught.getMessage(), caught);
	}

	public static MessageBox failedToGetUsersFiles(Throwable caught) {
		return showAlert("Get users files", "Failed to get users files.", caught);
	}

	public static MessageBox failedToCreateDirectory(Throwable caught) {
		return showAlert("Create Directory Reset", "Failed to create directory.\n" + caught.getMessage(), caught);
	}

	public static MessageBox failedToRenameFile(Throwable caught) {
		return showAlert("Rename File", "Failed to rename file.\n" + caught.getMessage(), caught);
	}

	public static MessageBox failedToDeleteFile(Throwable caught) {
		return showAlert("Delete File", "Failed to delete file.\n" + caught.getMessage(), caught);
	}

	public static MessageBox failedToGetDownloadPath(Throwable caught) {
		return showAlert("Get Download", "Failed to get download.", caught);
	}

	public static MessageBox failedToSearch(Throwable caught) {
		return showAlert("Search", "Failed to search.", caught);
	}

	public static MessageBox invalidFormat() {
		return showAlert("Invalid Format", "Invalid format detected.");
	}

	public static MessageBox savedSuccessfully() {
		return showInfo("Save", "Saved successfully.");
	}

	public static MessageBox validFormat() {
		return showInfo("Valid Format", "Valid format detected.");
	}

	public static MessageBox failedToGetFileContentHighlighted(Throwable caught) {
		return showAlert("Get File Content Highlighted", "Failed to get file content highlighted.", caught);
	}

	public static MessageBox failedToGetResumableTasks(Throwable caught) {
		return showAlert("Get Resumable Tasks", "Failed to get resumable tasks.", caught);
	}

	public static MessageBox failedToValidateSession(Throwable caught) {
		return showAlert("Validate Session", "Failed to validate session.", caught);
	}

	public static MessageBox failedToLoginWithgGoogle(Throwable caught) {
		return showAlert("Login Using Google", "Failed to login using Google.", caught);
	}

	public static MessageBox failedToGetLatestResumable(Throwable caught) {
		return showAlert("Get Latest Resumable Task", "Failed to get latest resumable task.", caught);
	}

	public static MessageBox failedToGetUsers(Throwable caught) {
		return showAlert("Get Users", "Failed to get users.", caught);
	}
	
	public static MessageBox failedToGetUser(Throwable caught) {
		return showAlert("Get User", "Failed to get user.", caught);
	}

	public static MessageBox failedToAddOrUpdateShare(Throwable caught) {
		return showAlert("Share", "Failed to modify share.", caught);
	}

	public static MessageBox failedToGetInvitees(Throwable caught) {
		return showAlert("Get Invitees", "Failed to get invitees.", caught);
	}

	public static MessageBox failedToRemoveMeFromShare(Throwable caught) {
		return showAlert("Remove Me from Share", "Failed to remove me from share.", caught);
	}

	public static MessageBox failedToCancelTask(Throwable caught) {
		return showAlert("Cancel Task", "Failed to cancel task.", caught);
	}

	public static MessageBox failedToGoToTaskStage(Throwable caught) {
		return showAlert("Go to Task Stage", "Failed to go to task stage.", caught);
	}

	public static MessageBox failedToGetAllTasks(Throwable caught) {
		return showAlert("Get All Tasks", "Failed to get all tasks.", caught);
	}

	public static MessageBox failedToUpdateUser(Throwable caught) {
		return showAlert("Update User", "Failed to update user.", caught);
	}

	public static MessageBox savedSettingsSuccesfully() {
		return showInfo("Save Settings", "Saved settings successfully.");
	}

	public static MessageBox failedToOutputMatrix(Throwable caught) {
		return showAlert("Download Matrix", "Failed to download matrix.", caught);
	}
	
	public static MessageBox failedToSaveMatrix(Throwable caught) {
		return showAlert("Save Matrix", "Failed to save matrix.", caught);
	}
	
	public static MessageBox failedToSaveTaxonomyComparisonModel(Throwable caught) {
		return showAlert("Save Taxonomy Comparison", "Failed to save taxonomy comparison.", caught);
	}

	public static MessageBox failedToReview(Throwable caught) {
		return showAlert("Review Matrix", "Failed to review matrix.", caught);
	}
	
	public static MessageBox failedToViewKey(Throwable caught) {
		return showAlert("View Key", "Failed to view key.", caught);
	}
	
	public static MessageBox failedToLoadTaxonomies(Throwable caught) {
		return showAlert("View Key", "Failed to load taxonomies.", caught);
	}

	public static MessageBox failedToGetTask(Throwable caught) {
		return showAlert("Get Task", "Failed to get task.", caught);
	}

	public static MessageBox failedToStartSemanticMarkup(Throwable caught) {
		return showAlert("Start Text Capture", "Failed to start text capture.", caught);
	}
	
	public static MessageBox failedToStartTreeGeneration(Throwable caught) {
		return showAlert("Start Tree Generation", "Failed to start tree generation.", caught);
	}
	
	public static MessageBox failedToIsValidInput(Throwable caught) {
		return showAlert("Validate Input", "Failed to validate input.", caught);
	}

	public static MessageBox failedToLearn(Throwable caught) {
		return showAlert("Learn", "Failed to learn.", caught);
	}

	public static MessageBox failedToOutput(Throwable caught) {
		return showAlert("Output", "Failed to output result.", caught);
	}

	public static MessageBox failedToParse(Throwable caught) {
		return showAlert("Parse", "Failed to parse.", caught);
	}

	public static MessageBox failedToGetDescription(Throwable caught) {
		return showAlert("Get Description", "Failed to get description.", caught);
	}

	public static MessageBox failedToSetDescription(Throwable caught) {
		return showAlert("Set Description", "Failed to set description.", caught);
	}

	public static MessageBox failedToSaveOto(Throwable caught) {
		return showAlert("Save Term Organization", "Failed to save term organization.", caught);
	}

	public static MessageBox failedToCompleteReview(Throwable caught) {
		return showAlert("Complete Matrix Review", "Failed to complete matrix review.", caught);
	}

	public static MessageBox failedToGenerateMatrix(Throwable caught) {
		return showAlert("Matrix Generation", "Failed to generate matrix.", caught);
	}
	
	public static MessageBox failedToRunTaxonomyComparison(Throwable caught) {
		return showAlert("Taxonomy Comparison", "Failed to run Taxonomy Comparison.", caught);
	}
	
	public static MessageBox failedToRunMirGeneration(Throwable caught) {
		return showAlert("Taxonomy Comparison", "Failed to run Mir Generatino.", caught);
	}

	public static MessageBox failedToRunInputVisualization(Throwable caught) {
		return showAlert("Taxonomy Comparison", "Failed to run Input Visualization.", caught);
	}

	public static MessageBox failedToStartMatrixGeneration(Throwable caught) {
		return showAlert("Start matrix Generation", "Failed to start matrix generation.", caught);
	}
	
	public static MessageBox failedToStartTaxonomyComparison(Throwable caught) {
		return showAlert("Start Taxonomy Comparison", "Failed to start taxonomy comparison.", caught);
	}
	
	public static MessageBox failedToGetDepth(Throwable caught) {
		return showAlert("Get Depth", "Failed to get depth.", caught);
	}
	
	public static MessageBox failedToIsvalidMarkedupTaxonDescriptionContent(
			Throwable caught) {
		return showAlert("Validate Markedup Taxon Description", "Failed to validate markedup taxon description.", caught);
	}
	
	public static MessageBox resetCodeSent(String email) {
		return showInfo("Code sent", "An authentication code will be sent to "
				+ "your email address (" + email + "). ");
	}

	public static MessageBox inputError(String message) {
		return showAlert("Input error", message);
	}

	public static MessageBox fileCreationSuccessful(int count, String message) {
		return showInfo("File creation", count+" file(s) successfully created</br>" +  message);
	}

	public static MessageBox tooManyFiles() {
		return showAlert("Too many files", "Currently only uploads <= 50 files are allowed.");
	}
	
	public static MessageBox notSavedInvalidXmlContent() {
		return showAlert("Not saved", "Content is no longer valid against the <a href='https://raw.githubusercontent.com/biosemantics/schemas/master/consolidation_01272014/semanticMarkupInput.xsd' target='_blank'>input schema</a>. "
							+ "correct the problems and try to save again.");
	}

	public static MessageBox fileNotEditable() {
		return showAlert("File not editable", "This file type can't be edited at this time.");
	}

	public static MessageBox fileSaved() {
		return showInfo("File saved", "File saved successfully.");
	}

	public static MessageBox maxDepthReached() {
		return showAlert("File Manager", "Only a directory depth of " + Configuration.fileManagerMaxDepth + " is allowed.");
	}

	public static MessageBox invalidMoveToDecendant() {
		return showAlert("File Manager", "Directory cannot be moved into its descendants.");
	}

	public static MessageBox noDestinationSelected() {
		return showAlert("No destination selected", "Please select a valid parent directory.");
	}

	public static MessageBox invalidParentDirectory() {
		return showAlert("File Manager", "Please select a valid parent directory.");
	}

	public static MessageBox invalidFileName() {
		return showAlert("File Manager", "Please select a valid file or directory to rename.");
	}

	public static MessageBox invalidFileToDelete() {
		return showAlert("File Manager", "Please select a valid file or directory to delete.");
	}
	
	public static MessageBox systemFolderNotAllowedInputForTask() {
		return showAlert("Input Directory", "Can not use system folders (Root, 'Owned', or  'Shared') as the input folder. Select another folder.");
	}

	public static MessageBox notDownloadable() {
		return showAlert("File Manager", "Not downloadable.");
	}

	public static MessageBox selectFileToDownload() {
		return showAlert("File Manager", "Please select a file to download.");
	}

	public static MessageBox fileManagerMessage(String message) {
		return showInfo("File Manager", message);
	}

	public static MessageBox sureToDelete(String file) {
		return showConfirm("File Manager", 
				"Are you sure you want to delete '" + file + "'?");				
	}

	public static MessageBox resumableTask() {
		MessageBox confirm = showConfirm("Resumable Task", "You have a resumable task of this type."); //message box can only have YES and NO buttons.
		confirm.getButton(PredefinedButton.YES).setText("Resume");
		confirm.getButton(PredefinedButton.NO).setText("Start New");
		return confirm;
	}

	public static MessageBox sharedInputForTask() {
		return showInfo("Shared input", "The selected input is not owned by you. "
				+ "The files will be copied to your own space.");
	}

	public static MessageBox selectValidInputDirectory() {
		return showAlert("Input Directory", "Please select a valid input directory.");
	}


	public static MessageBox invalidInputDirectory() {
		return showAlert("Input Directory", "Input directory is not valid. Please select a valid input directory.");
	}
	public static MessageBox emptyFolder() {
		return showAlert("Input Directory", "Input directory contains no files. Please select a valid input directory.");
		
	}
	
	public static MessageBox containSubFolder() {
		return showAlert("Input Directory", "Input directory can not contain sub-directories. Please select a valid input directory.");
		
	}
	public static MessageBox selectTaskName() {
		return showAlert("Task Name", "Please enter a name for this task.");
	}


	public static MessageBox signOutSuccessful() {
		return showInfo("Sign-out", "You are now signed out.");
	}

	public static MessageBox matrixGeneratedEmpty() {
		return showAlert("Matrix Empty", 
				"Note: No data was generated for this matrix. This could be due to insufficient or invalid input. <br/><br/>"
				+ "If you feel this is an error, please <a href=\"https://github.com/biosemantics/matrix-generation/issues\" target=\"_blank\">report the issue on github</a>. (Remember to include your ETC <br/>"
				+ "username and the name of the folder containing your input files!)");
	}

	public static MessageBox unmatchedBrackets() {
		return showAlert("Unmatched Brackets", 
				"You have not corrected all the unmatched brackets.");

	}

	public static MessageBox confirmTaskDelete(int count) {
		return showConfirm("Task Manager", "Are you sure you want to delete these " + count + " tasks?");
	}
	
	public static MessageBox failedToGetTaxonomyComparisonResult(Throwable t) {
		return showAlert("Taxonomy Comparison failed", "Failed to get Taxonomy Comparison result", t);
	}
	
	public static MessageBox confirmSharedTasksDelete() {
		return showConfirm("Task Manager", "Some of these tasks have been shared with "
				+ "other users. If you delete them they will be removed for all users. "
				+ "Do you want to continue?");
	}
	
	public static MessageBox confirmSharedTaskDelete() {
		return showConfirm("Task Manager", "If you delete this task it will be removed for all invitees of the share. "
				+ "Do you want to continue?");
	}

	public static MessageBox confirmTaskDelete(String name) {
		return showConfirm("Task Manager", "Are you sure you want to delete task '" + name + "'?");
	}
	
	public static MessageBox confirmOtoImport() {
		return showConfirm("Import Term Categorizations", "An imported term categorization will override the existing "
				+ "term categorization for that term if there is a conflict. Do you want to continue?");
	}

	public static MessageBox confirmSaveMatrix() {
		return showYesNoCancelConfirm("Save Changes?", "Do you want to save your matrix changes before continuing to the next step?");
	}
	
	private static MessageBox showAlert(String title, String message, Throwable caught) {
		if(caught != null)
			caught.printStackTrace();
		return showAlert(title, message);
	}
	
	private static MessageBox showAlert(String title, String message) {
		AlertMessageBox alert = new AlertMessageBox(title, message);
		alert.show();
		return alert;
	}

	private static MessageBox showInfo(String title, String message) {
		InfoMessageBox info = new InfoMessageBox(title, message);
		info.show();
		return info;
	}
	
	private static MessageBox showConfirm(String title, String message) {
		 ConfirmMessageBox confirm = new ConfirmMessageBox(title, message);
		 confirm.show();
         return confirm;
	}

	private static MessageBox showYesNoCancelConfirm(String title, String message) {
		MessageBox box = new MessageBox(title, message);
        box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
        box.setIcon(MessageBox.ICONS.question());
        box.show();
        return box;
	}



	


	
}
