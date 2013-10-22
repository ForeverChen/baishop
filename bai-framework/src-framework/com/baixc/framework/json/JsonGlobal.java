package com.baixc.framework.json;

import net.sf.json.JsonConfig;


public class JsonGlobal {

	public static final JsonConfig jsonConfig = new JsonConfig();
	
	static{
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
		jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor());
	}
}
