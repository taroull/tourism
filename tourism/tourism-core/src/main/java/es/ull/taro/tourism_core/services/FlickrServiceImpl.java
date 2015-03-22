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

	private static final String FLICKR_API_KEY = "6fd16e9b459d25282a7dc4da7cbf7bd7";

	@Override
	public List<String> findPhotos(String name) {
		return new ArrayList<String>(); // TODO
	}

	@Override
	public List<String> findPhotosNear(float latitude, float longitude, int radius) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource webResource = client.resource(UriBuilder.fromUri("https://api.flickr.com/services/rest/").build());
		List<Long> photoIds = getPhotoIds(webResource, latitude, longitude, radius);
		return getPhotoUrls(webResource, photoIds);
	}

	private List<String> getPhotoUrls(WebResource webResource, List<Long> photoIds) {
		List<String> photoUrls = new ArrayList<String>();
		for (Long photoId : photoIds) {
			ClientResponse photoInfoResponse = webResource.queryParam("method", "flickr.photos.getInfo").queryParam("api_key", FLICKR_API_KEY)
					.queryParam("photo_id", String.valueOf(photoId)).queryParam("format", "rest").queryParam("nojsoncallback", "1").accept("application/xml")
					.get(ClientResponse.class);
			if (200 == photoInfoResponse.getStatus() && photoInfoResponse != null) {
				String result = photoInfoResponse.getEntity(String.class);
				if (StringUtils.isNotBlank(result)) {
					JSONObject jsonObject = XML.toJSONObject(result);
					JSONObject rsp = jsonObject.get("rsp") != null ? (JSONObject) jsonObject.get("rsp") : null;
					if (rsp != null) {
						JSONObject photos = rsp.get("photo") != null ? (JSONObject) rsp.get("photo") : null;
						if (photos != null) {
							JSONObject urls = photos.get("urls") != null ? (JSONObject) photos.get("urls") : null;
							photoUrls.add(urls.getJSONObject("url").getString("content"));
						}
					}
				}
			}
		}
		return photoUrls;
	}

	private List<Long> getPhotoIds(WebResource webResource, float latitude, float longitude, int radius) {
		List<Long> photoIds = new ArrayList<Long>();

		ClientResponse photoSearchResponse = webResource.queryParam("method", "flickr.photos.search").queryParam("api_key", FLICKR_API_KEY)
				.queryParam("lat", String.valueOf(latitude)).queryParam("lon", String.valueOf(longitude)).queryParam("radius", String.valueOf(radius))
				.queryParam("format", "rest").accept("application/xml").get(ClientResponse.class);
		if (200 == photoSearchResponse.getStatus() && photoSearchResponse != null) {
			String result = photoSearchResponse.getEntity(String.class);
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = XML.toJSONObject(result);
				JSONObject rsp = jsonObject.get("rsp") != null ? (JSONObject) jsonObject.get("rsp") : null;
				if (rsp != null) {
					JSONObject photos = rsp.get("photos") != null ? (JSONObject) rsp.get("photos") : null;
					if (photos != null) {
						JSONArray jsonArray = photos.get("photo") != null ? (JSONArray) photos.get("photo") : null;
						if (jsonArray != null) {
							for (int i = 0; i < jsonArray.length(); i++) {
								if (((JSONObject) jsonArray.get(i)).has("id")) {
									Long photoId = (Long) ((JSONObject) jsonArray.get(i)).get("id");
									photoIds.add(photoId);
								}
							}
						}
					}
				}
			}
		}
		return photoIds;
	}
}
