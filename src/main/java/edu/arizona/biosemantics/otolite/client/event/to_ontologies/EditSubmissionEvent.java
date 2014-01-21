package edu.arizona.biosemantics.otolite.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.otolite.shared.beans.toontologies.OntologySubmission;

public class EditSubmissionEvent extends GwtEvent<EditSubmissionEventHandler> {
	public static Type<EditSubmissionEventHandler> TYPE = new Type<EditSubmissionEventHandler>();
	private OntologySubmission submission;

	public EditSubmissionEvent(OntologySubmission submission) {
		this.setSubmission(submission);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditSubmissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditSubmissionEventHandler handler) {
		handler.onClick(this);
	}

	public OntologySubmission getSubmission() {
		return submission;
	}

	public void setSubmission(OntologySubmission submission) {
		this.submission = submission;
	}

}
