
package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

@Service("twitterService")
public class TwitterImpl implements Twitter {

	private static final String URL_BASE = "https://api.twitter.com/1.1/search/tweets.json";

	private static final String PROPERTY_CONSUMER_KEY = "9SWP6w9Nu7rBdrIEGzbCTQInJ";
	private static final String PROPERTY_CONSUMER_SECRET = "Xe0ohMJpdnWWysGYRWNrqxBroWJcpuKOg2XaJL1R9BSakbnaIq";
	private static final String PROPERTY_TOKEN = "53918999-JfYXDE5GN01DPdDWY1Hg8VAuIRkmDeznVZn745JyZ";
	private static final String PROPERTY_TOKEN_SECRET = "hlwRiOCkzNnUmeGMZjMX6wS0FaN8cGzkQwNf4KHBbDDfM";

	@Override
	public List<HashMap<String, String>> findTwittersNear(float latitude, float longitude, int radius) {
		Client client = Client.create();
		OAuthParameters params = new OAuthParameters().signatureMethod("HMAC-SHA1").consumerKey(PROPERTY_CONSUMER_KEY).token(PROPERTY_TOKEN).version();
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(PROPERTY_CONSUMER_SECRET).tokenSecret(PROPERTY_TOKEN_SECRET);
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), params, secrets);
		WebResource res = client.resource(URL_BASE + "?result_type=lasted&geocode="+latitude+"%2C"+longitude+"%2C" + radius+"km");
		res.addFilter(filter);
		return getTwitterURLs(res);
	}

	private List<HashMap<String, String>> getTwitterURLs(WebResource webResource) {
		List<HashMap<String, String>> tweets = new ArrayList<HashMap<String, String>>();

		ClientResponse tweetSearchResponse = webResource.get(ClientResponse.class);
		// 200 todo OK
		if (200 == tweetSearchResponse.getStatus() && tweetSearchResponse != null) {
			String result = tweetSearchResponse.getEntity(String.class);
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				JSONArray statuses = jsonObject.get("statuses") != null ? (JSONArray) jsonObject.get("statuses") : null;
				if (statuses != null) {
					for(int i = 0; i < statuses.length(); i++) {
						String objeto = statuses.getJSONObject(i).getString("text");
						String screenName = statuses.getJSONObject(i).getJSONObject("user").getString("screen_name");
						String name = statuses.getJSONObject(i).getJSONObject("user").getString("name");
						String fecha = statuses.getJSONObject(i).getString("created_at");
						String perfil = statuses.getJSONObject(i).getJSONObject("user").getString("profile_image_url");
						String url = null;
						if(statuses.getJSONObject(i).getJSONObject("user").get("url") != null) {
							url = statuses.getJSONObject(i).getJSONObject("user").get("url").toString();
						//	url = (String)statuses.getJSONObject(i).getJSONObject("user").get("url");
						}
						HashMap<String, String> hash = new HashMap<String, String>();
						
						hash.put("name", name);
						hash.put("text", objeto);
						hash.put("screen_name", screenName);
						hash.put("date", fecha);
						hash.put("image", perfil);
						hash.put("entity", url);
						
						tweets.add(hash);
					}
					
				}
			}
		}
		return tweets;
	}
}




