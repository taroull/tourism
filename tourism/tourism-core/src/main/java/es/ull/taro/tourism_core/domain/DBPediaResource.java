package es.ull.taro.tourism_core.domain;

public class DBPediaResource extends GeoResource {

	private static final long serialVersionUID = 1L;
	
private String postalCode;
	
	public DBPediaResource() {
	}
	
	public DBPediaResource(String uri) {
		super(uri);
	}
	
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}
