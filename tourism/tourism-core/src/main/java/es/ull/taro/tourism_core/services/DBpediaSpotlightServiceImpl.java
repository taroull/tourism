package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class DBpediaSpotlightServiceImpl implements DBpediaSpotlightService {

	public static final String DBPEDIA_SPOTLIGHT_ENDPOINT_EN = "http://spotlight.sztaki.hu:2222";
	public static final String DBPEDIA_SPOTLIGHT_ENDPOINT_ES = "http://spotlight.sztaki.hu:2231";

	@Override
	public List<String> getDBpediaURIs(String text) throws JsonParseException, ClientHandlerException, UniformInterfaceException,
			IOException {

		List<String> resourceURIs = new ArrayList<String>();

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource webResource = client.resource(UriBuilder.fromUri(DBPEDIA_SPOTLIGHT_ENDPOINT_EN).build());

		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("text", text);
		queryParams.add("confidence", "0.1");
		queryParams.add("support", "20");

		ClientResponse response = webResource.path("/rest/annotate").accept(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, queryParams);

		if (response != null) {
			JSONObject resultJSON = new JSONObject(response.getEntity(String.class));
			JSONArray entities = resultJSON.getJSONArray("Resources");

			for (int i = 0; i < entities.length(); i++) {
				JSONObject entity = entities.getJSONObject(i);
				resourceURIs.add(entity.getString("@URI"));
			}
		}
		return resourceURIs;
	}
}
