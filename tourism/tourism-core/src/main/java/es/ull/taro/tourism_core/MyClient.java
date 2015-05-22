package es.ull.taro.tourism_core;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

import es.ull.taro.tourism_core.services.DBpediaSpotlightServiceImpl;

public class MyClient {

	public static void main(String[] args) throws JsonParseException, ClientHandlerException, UniformInterfaceException, IOException {

		// try {
		// List<DBpediaResource> resources = new
		// DBpediaServiceImpl().find("puerto de la cruz");
		// for (DBpediaResource resource : resources) {
		// System.out.println(resource);
		// }

		List<String> uRIs = new DBpediaSpotlightServiceImpl().getDBpediaURIs("Tenerife");
		System.out.println(Arrays.toString(uRIs.toArray(new String[0])));

		// } catch (JsonLdError e) {
		// e.printStackTrace();
		// }
	}
	
}
