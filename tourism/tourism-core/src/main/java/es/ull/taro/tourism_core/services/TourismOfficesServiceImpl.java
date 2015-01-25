package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.riot.RDFDataMgr;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import es.ull.taro.tourism_core.domain.OfficeResource;

@Service("tourismOfficesService")
public class TourismOfficesServiceImpl implements TourismOfficesService {

	@Override
	public HashMap<String, String> find(String name) {

		Model model = loadRDFFile();

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX org: <http://www.w3.org/TR/vocab-org/> ");
		sparqlQuery.append("PREFIX vCard: <http://www.w3.org/TR/vcard-rdf/> ");

		sparqlQuery.append("SELECT ?office ?locality");
		sparqlQuery.append("{ ");
		sparqlQuery.append("  ?office a org:OrganizationalUnit. ");
		sparqlQuery.append("  ?office org:hasRegisteredSite ?registeredSite. ");
		sparqlQuery.append("  ?registeredSite a org:Site. ");
		sparqlQuery.append("  ?registeredSite org:siteAddress ?location. ");
		sparqlQuery.append("  ?location a vCard:Location. ");
		sparqlQuery.append("  ?location vCard:hasAddress ?address. ");
		sparqlQuery.append("  ?address a vCard:Address. ");
		sparqlQuery.append("  ?address vCard:locality ?locality. ");
		sparqlQuery.append("  FILTER regex(?locality, \"").append(name).append("\", \"i\"). ");
		sparqlQuery.append("}");

		HashMap<String, String> uris = new HashMap<String, String>();

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				Resource resource = sol.getResource("?office");
				Literal locality = sol.getLiteral("?locality");
				
				
				System.out.println("uri " + resource + " nombre " + locality);
				uris.put(resource.getURI().toString(), locality.toString());
			}
		} finally {
			qe.close();
		}

		return uris;
	}
	
	
	@Override
	public List<String> findTourismOfficesAround(float latitude, float longitude, int radiusInMeters) {

		Model model = loadRDFFile();

		// radius is specified in meters, but to make the query, we have to
		// divide the radius by 100.000
		double convertedRadius = Double.valueOf(radiusInMeters) / 100000;

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX org: <http://www.w3.org/TR/vocab-org/> ");
		sparqlQuery.append("PREFIX vCard: <http://www.w3.org/TR/vcard-rdf/> ");

		sparqlQuery.append("SELECT ?office ?lat ?long ");
		sparqlQuery.append("WHERE { ");
		sparqlQuery.append("  ?office a org:OrganizationalUnit. ");
		sparqlQuery.append("  ?office org:hasRegisteredSite ?site. ");
		sparqlQuery.append("  ?site a org:Site. ");
		sparqlQuery.append("  ?site org:siteAddress ?siteAddress. ");
		sparqlQuery.append("  ?siteAddress a vCard:Location. ");
		sparqlQuery.append("  ?siteAddress geo:location ?geoLocation. ");
		sparqlQuery.append("  ?geoLocation a geo:Point. ");
		sparqlQuery.append("  ?geoLocation geo:lat ?lat. ");
		sparqlQuery.append("  ?geoLocation geo:long ?long. ");
		sparqlQuery.append("FILTER(xsd:double(?lat) - xsd:double('").append(latitude).append("') <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double('").append(latitude).append("') - xsd:double(?lat) <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double(?long) - xsd:double('").append(longitude).append("') <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double('").append(longitude).append("') - xsd:double(?long) <= ").append(convertedRadius).append(" ). ");
		sparqlQuery.append("}");

		List<String> uris = new ArrayList<String>();

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				Resource resource = sol.getResource("?office");
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}

	protected Model loadRDFFile() {
		return RDFDataMgr.loadModel("tdtoficinasdeturismov1.2.0.rdf");
	}
	
	@Override
	public HashMap<String, String> describeUri(String uri) {

		Model model = loadRDFFile();

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("DESCRIBE ").append("<").append(uri).append(">");

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);
		Model resultModel;
		try {
			resultModel = qe.execDescribe();
		} finally {
			qe.close();
		}
		StringBuilder sparqlQuery2 = new StringBuilder();
		sparqlQuery2.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery2.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery2.append("PREFIX org: <http://www.w3.org/TR/vocab-org/> ");
		sparqlQuery2.append("PREFIX vCard: <http://www.w3.org/TR/vcard-rdf/> ");
		sparqlQuery2.append("PREFIX foaf: <http://xmlns.com/foaf/spec/>");
		sparqlQuery2.append("SELECT ?Email ?Telephone ?StreetName ?City ?PostCode ?Photo ?Url ?Purpose " +
				                    "{ OPTIONAL {?resource org:hasRegisteredSite ?B1_Site . ?B1_Site org:siteAddress ?B1_Location . ?B1_Location vCard:hasEmail ?Email . }" +
				                    "?B1_Location vCard:hasTelephone ?B2_Telephone . ?B2_Telephone vCard:hasValue ?Telephone . " +
				                    "?B1_Location vCard:hasAddress ?B3_Address . ?B3_Address vCard:street-address ?StreetName ." +
				                    "?B3_Address vCard:locality ?City . " +
				                    "?B3_Address vCard:postal-code ?PostCode ." +
				                    "OPTIONAL {?B1_Location vCard:hasPhoto ?Photo . }" +
				                    "OPTIONAL {?B1_Location foaf:homepage ?Url . }" +
				                    "OPTIONAL {?resource org:purpose ?Purpose .}}");

		QueryExecution qe2 = QueryExecutionFactory.create(sparqlQuery2.toString(), resultModel);
		HashMap<String, String> results = new HashMap<String, String>();
		try {
			com.hp.hpl.jena.query.ResultSet ns = qe2.execSelect();
	        while(ns.hasNext()){
	            QuerySolution soln = ns.nextSolution();
	            if (soln.getResource("?Email") != null) results.put(soln.getResource("?Email").toString(), "Correo"); 
	            results.put(soln.getResource("?Telephone").toString(), "Teléfono"); 
	            results.put(soln.getLiteral("?StreetName").toString(), "Dirección");
	            results.put(soln.getLiteral("?City").toString(), "Nombre");
	            results.put(soln.getLiteral("?PostCode").toString(), "Código Postal");
	            //if (soln.getResource("?Photo") != null) results.put(soln.getResource("?Photo").toString(), "Foto");
	            if (soln.getResource("?Url") != null) results.put(soln.getResource("?Url").toString(), "URL");
	            if (soln.getLiteral("?Purpose") != null) results.put(soln.getLiteral("?Purpose").toString(), "Tipo de actividad");
	    }
		} finally {
			qe2.close();
		}
	return results;
	}
	
	@Override
	public OfficeResource createOfficeResource(String uri) {
		Resource resource = retrieve(uri);
		OfficeResource offResource = new OfficeResource();
		Model m = loadRDFFile();

		offResource.setUri(resource.getURI());
		Statement hasRegisteredSite = resource.getProperty(m.createProperty("http://www.w3.org/TR/vocab-org/hasRegisteredSite"));
		Statement siteAddress = hasRegisteredSite.getProperty(m.createProperty("http://www.w3.org/TR/vocab-org/siteAddress"));
		Statement location = siteAddress.getProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#location"));
		float latitude = location.getProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat")).getLiteral().getFloat();
		float longitude = location.getProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long")).getLiteral().getFloat();
		Statement hasAddress = siteAddress.getProperty(m.createProperty("http://www.w3.org/TR/vcard-rdf/hasAddress"));
		String postalCode = hasAddress.getProperty(m.createProperty("http://www.w3.org/TR/vcard-rdf/postal-code")).getLiteral().getString();
		
		offResource.setPostalCode(postalCode);
		offResource.setLatitude(latitude);
		offResource.setLongitude(longitude);
        
		return offResource;
	}
   
	@Override
	public Resource retrieve(String uri) {
		Model model = loadRDFFile();
		return model.getResource(uri);
	}

}