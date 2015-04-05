package es.ull.taro.tourism_core.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Resource implements Serializable {

	private static final long serialVersionUID = -8739986500622917719L;

	private String uri;
	private String name;

	public Resource() {
	}

	public Resource(String uri) {
		this.uri = uri;
	}

	public Resource(String uri, String name) {
		this.uri = uri;
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(name).append(uri).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Resource)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		Resource rhs = (Resource) obj;
		return new EqualsBuilder().append(name, rhs.name).append(uri, rhs.uri).isEquals();
	}
}
