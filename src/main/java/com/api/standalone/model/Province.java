package com.api.standalone.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("provinces") @IdName("id")
public class Province extends Model {

	public String getName() {
		return getString("name");
	}
	public void setName(String name) {
		set("name", name);
	}
	public Province() {
		super();
	}
	public Province(String name) {
		super();
		setName(name);
	}
}
