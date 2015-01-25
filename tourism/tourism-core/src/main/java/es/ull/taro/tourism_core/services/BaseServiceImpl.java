package es.ull.taro.tourism_core.services;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import es.ull.taro.tourism_core.domain.GeoResource;

public abstract class BaseServiceImpl implements BaseService {

	protected FileManager fManager = FileManager.get();

	public BaseServiceImpl() {
		fManager.addLocatorURL();
	}

	protected Literal getProperty(Model model, Property property) {
		StmtIterator iter = model.listStatements(new SimpleSelector(null, property, (RDFNode) null));
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			if (stmt.getObject().isLiteral()) {
				return (Literal) stmt.getObject();
			}
		}
		return null;
	}

	protected void fillLatitude(Model model, GeoResource geoResource) {
		Property latProperty = model.createProperty(getVocabulary(), getLatitudeProperty());
		Literal literal = getProperty(model, latProperty);
		if (literal != null) {
			geoResource.setLatitude(literal.getFloat());
		}
	}

	protected void fillLongitude(Model model, GeoResource geoResource) {
		Property longProperty = model.createProperty(getVocabulary(), getLongitudeProperty());
		Literal literal = getProperty(model, longProperty);
		if (literal != null) {
			geoResource.setLongitude(literal.getFloat());
		}
	}

	public abstract String getVocabulary();

	public abstract String getLatitudeProperty();

	public abstract String getLongitudeProperty();
}
