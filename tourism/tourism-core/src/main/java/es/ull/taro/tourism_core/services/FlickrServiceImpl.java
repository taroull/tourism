package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Service("flickrService")
public class FlickrServiceImpl implements FlickrService {

	@Override
	public List<String> findPhotos(String name) {
		String path = new StringBuilder().append("/flickrwrappr/photos/").append(name).toString();
		return processGet(path);
	}

	@Override
	public List<String> findPhotosNear(float latitude, float longitude, int radius) {
		String path = new StringBuilder().append("/flickrwrappr/location/").append(latitude).append("/").append(longitude).append("/")
				.append(radius).toString();
		return processGet(path);
	}

	private List<String> processGet(String path) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource webResource = client.resource(UriBuilder.fromUri("http://wifo5-03.informatik.uni-mannheim.de").build());

		ClientResponse response = webResource.path(path).accept("application/rdf+xml").get(ClientResponse.class);

		List<String> photoUrls = new ArrayList<String>();
		if (response != null) {
			String result = response.getEntity(String.class);
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = XML.toJSONObject(result);
				JSONObject rdfContent = jsonObject.get("rdf:RDF") != null ? (JSONObject) jsonObject.get("rdf:RDF") : null;
				if (rdfContent != null) {
					JSONArray jsonArray = rdfContent.get("rdf:Description") != null ? (JSONArray) rdfContent.get("rdf:Description") : null;
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							if (((JSONObject) jsonArray.get(i)).has("foaf:depiction")) {
								JSONObject foafDepiction = (JSONObject) ((JSONObject) jsonArray.get(i)).get("foaf:depiction");
								String photoUrl = foafDepiction.getString("rdf:resource");
								photoUrls.add(photoUrl);
							}
						}
					}
				}
			}
		}
		return photoUrls;
	}
}
