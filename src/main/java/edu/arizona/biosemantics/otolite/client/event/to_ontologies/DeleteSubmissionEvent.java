package edu.arizona.biosemantics.otolite.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.otolite.shared.beans.toontologies.OntologySubmission;

public class DeleteSubmissionEvent extends
		GwtEvent<DeleteSubmissionEventHandler> {
	public static Type<DeleteSubmissionEventHandler> TYPE = new Type<DeleteSubmissionEventHandler>();
	private OntologySubmission submission;

	public DeleteSubmissionEvent(OntologySubmission submission) {
		this.setSubmission(submission);
	}

	@Override
	public Type<DeleteSubmissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteSubmissionEventHandler handler) {
		handler.onClick(this);
	}

	public OntologySubmission getSubmission() {
		return submission;
	}

	public void setSubmission(OntologySubmission submission) {
		this.submission = submission;
	}

}
