package es.ull.taro.tourism_core.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Service("flickrService")
public class FlickrServiceImpl implements FlickrService {

	private static final String API_KEY = "FILL_WITH_API_KEY";

	private static final String PHOTO_URL = "https://farm{0}.staticflickr.com/{1}/{2}_{3}.jpg";

	@Override
	public List<String> findPhotos(String name) {
		return new ArrayList<String>(); // TODO
	}

	@Override
	public List<String> findPhotosNear(float latitude, float longitude, int radius) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource webResource = client.resource(UriBuilder.fromUri("https://api.flickr.com/services/rest/").build());
		return getPhotoURLs(webResource, latitude, longitude, radius);
	}

	private List<String> getPhotoURLs(WebResource webResource, float latitude, float longitude, int radius) {
		List<String> photoURLs = new ArrayList<String>();

		ClientResponse photoSearchResponse = webResource.queryParam("method", "flickr.photos.search").queryParam("api_key", API_KEY)
				.queryParam("lat", String.valueOf(latitude)).queryParam("lon", String.valueOf(longitude))
				.queryParam("radius", String.valueOf(radius)).queryParam("format", "json").queryParam("nojsoncallback", "1")
				.get(ClientResponse.class);
		if (200 == photoSearchResponse.getStatus() && photoSearchResponse != null) {
			String result = photoSearchResponse.getEntity(String.class);
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				JSONObject photos = jsonObject.get("photos") != null ? (JSONObject) jsonObject.get("photos") : null;
				if (photos != null) {
					JSONArray jsonArray = photos.get("photo") != null ? (JSONArray) photos.get("photo") : null;
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							if (((JSONObject) jsonArray.get(i)).has("id")) {
								photoURLs.add(buildPhotoUrl((JSONObject) jsonArray.get(i)));
							}
						}
					}
				}
			}
		}
		return photoURLs;
	}

	private String buildPhotoUrl(JSONObject photoJsonObject) {
		int farm = photoJsonObject.getInt("farm");
		String server = photoJsonObject.getString("server");
		String id = photoJsonObject.getString("id");
		String secret = photoJsonObject.getString("secret");
		return MessageFormat.format(PHOTO_URL, farm, server, id, secret);
	}
}
