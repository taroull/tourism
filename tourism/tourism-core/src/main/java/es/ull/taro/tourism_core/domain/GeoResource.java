package es.ull.taro.tourism_core.domain;

public class GeoResource extends Resource {

	private static final long serialVersionUID = 6987557186908227631L;

	private float latitude;
	private float longitude;

	public GeoResource() {
	}

	public GeoResource(String uri) {
		super(uri);
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
}
