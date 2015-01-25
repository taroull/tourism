package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

public interface DBpediaSpotlightService {

	public static final String BEAN_ID = "dBpediaSpotlightService";

	public List<String> getDBpediaURIs(String text) throws JsonParseException, ClientHandlerException, UniformInterfaceException, IOException;
}
