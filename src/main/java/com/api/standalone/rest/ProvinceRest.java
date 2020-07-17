package com.api.standalone.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import com.api.standalone.model.Province;

@Path("provinces")
public class ProvinceRest {

	/** 
	 * select provinces
	 * */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProvices() {
		Base.open();
		LazyList<Province> provinces = Province.findAll().orderBy("name");
		String json = provinces.toJson(true, "name");
		Base.close();
		return Response.ok(json).build();
	}
}
