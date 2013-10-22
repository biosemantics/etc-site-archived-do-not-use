package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;
import java.util.Date;

import edu.arizona.sirls.etc.site.shared.rpc.InputTypeEnum;

public class InputType implements Serializable {
	
	private static final long serialVersionUID = -5836126918662301203L;
	private int id;
	private InputTypeEnum inputTypeEnum;
	private Date created;
	
	public InputType(int id, InputTypeEnum inputTypeEnum, Date created) {
		super();
		this.id = id;
		this.inputTypeEnum = inputTypeEnum;
		this.created = created;
	}
	public InputType() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public InputTypeEnum getInputTypeEnum() {
		return inputTypeEnum;
	}
	public void setInputTypeEnum(InputTypeEnum inputTypeEnum) {
		this.inputTypeEnum = inputTypeEnum;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	
	
}
