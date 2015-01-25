package es.ull.taro.tourism_core.domain;

import java.io.Serializable;

public class GeoResource implements Serializable {

	private static final long serialVersionUID = -8739986500622917719L;

	private String uri;
	private float latitude;
	private float longitude;

	public GeoResource() {
	}

	public GeoResource(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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
