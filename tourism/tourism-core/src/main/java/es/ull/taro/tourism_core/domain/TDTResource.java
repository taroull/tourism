package es.ull.taro.tourism_core.domain;

public class TDTResource extends GeoResource{

	private static final long serialVersionUID = -2810134268668483800L;
	private String postalCode;
	
	public TDTResource() {
	}
	
	public TDTResource(String uri) {
		super(uri);
	}
	
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}
