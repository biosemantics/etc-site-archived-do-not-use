package edu.arizona.biosemantics.etcsite.client.common;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;
import edu.arizona.biosemantics.oto2.oto.client.common.Alerter.InfoMessageBox;

public class Alerter {
	
	private static AutoProgressMessageBox box;

	public static MessageBox failedToCreateTaxonDescription(Throwable caught) {
		return showAlert("Create Taxon Description", "Failed to create taxon description.", caught);
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
		return showAlert("Password Reset", "Failed to request password reset code", caught);
	}

	public static MessageBox failedToResetPassword(Throwable caught) {
		return showAlert("Password Reset", "Failed to reset password", caught);
	}

	public static MessageBox failedToGetFileContent(Throwable caught) {
		return showAlert("Get File Content", "Failed to get file content", caught);
	}

	public static MessageBox failedToValidateXmlContentForSchema(Throwable caught) {
		return showAlert("Validate XML", "Failed to validate xml", caught);
	}

	public static MessageBox failedToSetFileContent(Throwable caught) {
		return showAlert("Set File Content", "Failed to set file content", caught);
	}

	public static MessageBox failedToSetSchema(Throwable caught) {
		return showAlert("Set Schema", "Failed to set schema", caught);
	}

	public static MessageBox failedToGetSchema(Throwable caught) {
		return showAlert("Get Schema", "Failed to get schema", caught);
	}

	public static MessageBox failedToIsDirectory(Throwable caught) {
		return showAlert("Directory Check", "Failed to check directory", caught);
	}

	public static MessageBox failedToMoveFile(Throwable caught) {
		return showAlert("Move File", "Failed to move file", caught);
	}

	public static MessageBox failedToGetUsersFiles(Throwable caught) {
		return showAlert("Password Reset", "Failed to request password reset code", caught);
	}

	public static MessageBox failedToCreateDirectory(Throwable caught) {
		return showAlert("Create Directory Reset", "Failed to create directory", caught);
	}

	public static MessageBox failedToRenameFile(Throwable caught) {
		return showAlert("Rename File", "Failed to rename file", caught);
	}

	public static MessageBox failedToDeleteFile(Throwable caught) {
		return showAlert("Delete File", "Failed to delete file", caught);
	}

	public static MessageBox failedToGetDownloadPath(Throwable caught) {
		return showAlert("Get Download", "Failed to get download", caught);
	}

	public static MessageBox failedToSearch(Throwable caught) {
		return showAlert("Search", "Failed to search", caught);
	}

	public static MessageBox invalidFormat() {
		return showAlert("Invalid Format", "Invalid format detected");
	}

	public static MessageBox savedSuccessfully() {
		return showInfo("Save", "Saved successfully");
	}

	public static MessageBox validFormat() {
		return showInfo("Valid Format", "Valid format detected");
	}

	public static MessageBox failedToGetFileContentHighlighted(Throwable caught) {
		return showAlert("Get File Content Highlighted", "Failed to get file content highlighted", caught);
	}

	public static MessageBox failedToGetResumableTasks(Throwable caught) {
		return showAlert("Get Resumable Tasks", "Failed to get resumable tasks", caught);
	}

	public static MessageBox failedToValidateSession(Throwable caught) {
		return showAlert("Validate Session", "Failed to validate session", caught);
	}

	public static MessageBox failedToLoginWithgGoogle(Throwable caught) {
		return showAlert("Login Using Google", "Failed to login using Google", caught);
	}

	public static MessageBox failedToGetLatestResumable(Throwable caught) {
		return showAlert("Get Latest Resumable Task", "Failed to get latest resumable task", caught);
	}

	public static MessageBox failedToGetUsers(Throwable caught) {
		return showAlert("Get Users", "Failed to get users", caught);
	}

	public static MessageBox failedToAddOrUpdateShare(Throwable caught) {
		return showAlert("Share", "Failed to modify share", caught);
	}

	public static MessageBox failedToGetInvitees(Throwable caught) {
		return showAlert("Get Invitees", "Failed to get invitees", caught);
	}

	public static MessageBox failedToRemoveMeFromShare(Throwable caught) {
		return showAlert("Remove Me from Share", "Failed to remove me from share", caught);
	}

	public static MessageBox failedToCancelTask(Throwable caught) {
		return showAlert("Cancel Task", "Failed to cancel task", caught);
	}

	public static MessageBox failedToGoToTaskStage(Throwable caught) {
		return showAlert("Go to Task Stage", "Failed to go to task stage", caught);
	}

	public static MessageBox failedToGetAllTasks(Throwable caught) {
		return showAlert("Get All Tasks", "Failed to get all tasks", caught);
	}

	public static MessageBox couldNotFindUser(UserNotFoundException e) {
		return showAlert("Password Reset", "Failed to request password reset code");
	}

	public static MessageBox failedToUpdateUser(Throwable caught) {
		return showAlert("Update User", "Failed to update user", caught);
	}

	public static MessageBox savedSettingsSuccesfully() {
		return showInfo("Save Settings", "Saved settings successfully");
	}

	public static MessageBox failedToOutputMatrix(Throwable caught) {
		return showAlert("Output Matrix", "Failed to output matrix", caught);
	}

	public static MessageBox failedToReview(Throwable caught) {
		return showAlert("Review Matrix", "Failed to review matrix", caught);
	}

	public static MessageBox failedToGetTask(Throwable caught) {
		return showAlert("Get Task", "Failed to get task", caught);
	}

	public static MessageBox failedToStartSemanticMarkup(Throwable caught) {
		return showAlert("Start Text Capture", "Failed to start text capture", caught);
	}

	public static MessageBox failedToIsValidInput(Throwable caught) {
		return showAlert("Validate Input", "Failed to validate input", caught);
	}

	public static MessageBox failedToLearn(Throwable caught) {
		return showAlert("Learn", "Failed to learn", caught);
	}

	public static MessageBox failedToOutput(Throwable caught) {
		return showAlert("Output", "Failed to output result", caught);
	}

	public static MessageBox failedToParse(Throwable caught) {
		return showAlert("Password Reset", "Failed to request password reset code", caught);
	}

	public static MessageBox failedToGetDescription(Throwable caught) {
		return showAlert("Get Description", "Failed to get description", caught);
	}

	public static MessageBox failedToSetDescription(Throwable caught) {
		return showAlert("Set Description", "Failed to set description", caught);
	}

	public static MessageBox failedToSaveOto(Throwable caught) {
		return showAlert("Save Term Organization", "Failed to save term organization", caught);
	}

	public static MessageBox failedToCompleteReview(Throwable caught) {
		return showAlert("Complete Matrix Review", "Failed to complete matrix review.", caught);
	}

	public static MessageBox failedToSaveMatrixGeneration(Throwable caught) {
		return showAlert("Save Matrix", "Failed to save matrix", caught);
	}

	public static MessageBox failedToProcess(Throwable caught) {
		return showAlert("Matrix Generation", "Failed to generate matrix", caught);
	}

	public static MessageBox failedToStartMatrixGeneration(Throwable caught) {
		return showAlert("Start matrix Generation", "Failed to start matrix generation", caught);
	}
	
	public static MessageBox failedToGetDepth(Throwable caught) {
		return showAlert("Get Depth", "Failed to get depth", caught);
	}
	
	public static MessageBox failedToIsvalidMarkedupTaxonDescriptionContent(
			Throwable caught) {
		return showAlert("Is valid Markedup Taxon Description", "Failed to validate markedup taxon description", caught);
	}
	
	public static MessageBox resetCodeSent(String email) {
		return showInfo("Code sent", "An authentication code will be sent to "
				+ "your email address (" + email + "). ");
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

	public static MessageBox inputError(String message) {
		return showAlert("Input error", message);
	}

	public static MessageBox fileCreationSuccessful(int count, String message) {
		return showInfo("File creation", count+" file(s) successfully created</br>" +  message);
	}

	public static MessageBox tooManyFiles() {
		return showAlert("Too many files", "Currently only uploads <= 50 files are allowed");
	}
	
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
		return showAlert("No destination selected", "Please select a valid parent directory");
	}

	public static MessageBox invalidParentDirectory() {
		return showAlert("File Manager", "Please select a valid parent directory.");
	}

	public static MessageBox invalidFileName() {
		return showAlert("File Manager", "Please select a valid file or directory to rename");
	}

	public static MessageBox invalidFileToDelete() {
		return showAlert("File Manager", "Please select a valid file or directory to delete");
	}

	public static MessageBox notDownloadable() {
		return showAlert("File Manager", "Not downloadable");
	}

	public static MessageBox selectFileToDownload() {
		return showAlert("File Manager", "Please select a file to download");
	}

	public static MessageBox fileManagerMessage(String message) {
		return showInfo("File Manager", message);
	}

	public static MessageBox sureToDelete(String file) {
		return showConfirm("File Manager", 
				"Are you sure you want to delete '" + file + "'?");				
	}

	private static MessageBox showConfirm(String title, String message) {
		 ConfirmMessageBox confirm = new ConfirmMessageBox(title, message);
		 confirm.show();
         return confirm;
	}

	public static MessageBox resumableTask() {
		MessageBox confirm = showConfirm("Resumable Task", "You have a resumable task of this type");
		confirm.getButton(PredefinedButton.YES).setText("Resume");
		confirm.getButton(PredefinedButton.NO).setText("Start New");
		return confirm;
	}

	public static MessageBox sharedInputForTask() {
		return showInfo("Shared input", "The selected input is not owned. "
				+ "To start the task the files will be copied to your own space.");
	}

	public static MessageBox selectValidInputDirectory() {
		return showAlert("Input Directory", "Please select a valid input directory.");
	}

	public static MessageBox selectTaskName() {
		return showAlert("Task Name", "Please enter a name for this task.");
	}

	public static MessageBox invalidInputDirectory() {
		return showAlert("Input Directory", "This is not a valid input directory.");
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

}
