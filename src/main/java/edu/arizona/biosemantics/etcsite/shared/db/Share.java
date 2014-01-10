package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Share implements Serializable {

	private static final long serialVersionUID = 4818830111979976800L;
	private int id;
	private Task task;
	private Set<ShortUser> invitees;
	private Date created;
	
	public Share() { }
	
	public Share(int id, Task task, Set<ShortUser> invitees, Date created) {
		super();
		this.id = id;
		this.task = task;
		this.invitees = invitees;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public Set<ShortUser> getInvitees() {
		return invitees;
	}

	public void setInvitees(Set<ShortUser> invitees) {
		this.invitees = invitees;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		Share share = (Share)object;
		if(share.getId()==this.id)
			return true;
		return false;
	}
	
}
