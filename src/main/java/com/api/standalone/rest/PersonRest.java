package com.api.standalone.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import com.api.standalone.model.Person;

@Path("persons")
public class PersonRest {

	/** 
	 * select persons
	 * */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersons() {
		Base.open();
		LazyList<Person> persons = Person.findAll().orderBy("name");
		String json = persons.toJson(true, "name", "address", "date_of_birth", "email");
		System.out.println("sukses");
		Base.close();
		return Response.ok(json).build();
	}
}
