package com.api.standalone.model;

import java.util.Date;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("persons") @IdName("id")
public class Person extends Model {

	public String getName() {
		return getString("name");
	}
	public void setName(String name) {
		set("name", name);
	}
	public String getEmail() {
		return getString("email");
	}
	public void setEmail(String email) {
		set("email", email);
	}
	public Date getDateOfBirth() {
		return getDate("date_of_birth");
	}
	public void setDateOfBirth(Date dateOfBirth) {
		set("date_of_birth", dateOfBirth);
	}
	public String getAddress() {
		return getString("address");
	}
	public void setAddress(String address) {
		set("address", address);
	}
	public Person() {
		super();
	}
	public Person(String name, String address, String email, Date dateOfBirth) {
		super();
		setName(name);
		setEmail(email);
		setAddress(address);
		setDateOfBirth(dateOfBirth);
	}
}
