package com.api.standalone.util;

import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfiguration extends ResourceConfig {
	
	/**
	 * set package
	 * */
	public JerseyConfiguration() {
		packages("com.api.standalone");
	}

}
