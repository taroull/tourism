package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

@Service("geoLinkedDataService")
public class GeoLinkedDataServiceImpl implements GeoLinkedDataService {

	@Autowired
	private CoreService coreService;

	@Override
	public List<String> retrievePlacesAround(float latitude, float longitude, int radius) {

		// radius is especified in meters, but to make the query, we have to
		// divide the radius by 100.000
		double convertedRadius = Double.valueOf(radius) / 100000;

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		sparqlQuery.append("SELECT  DISTINCT ?subject ?label ?lat ?long");
		sparqlQuery.append("WHERE { ");
		sparqlQuery.append("  ?subject geo:geometry ?g.  ");
		sparqlQuery.append("  ?g geo:lat ?lat. ");
		sparqlQuery.append("  ?g geo:long ?long. ");
		sparqlQuery.append("  ?subject rdfs:label ?label.");
		sparqlQuery.append("FILTER(xsd:double(?lat) - xsd:double('").append(latitude).append("') <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double('").append(latitude).append("') - xsd:double(?lat) <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double(?long) - xsd:double('").append(longitude).append("') <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double('").append(longitude).append("') - xsd:double(?long) <= ").append(convertedRadius)
				.append(" &&	lang(?label) = \"es\" ). ");
		sparqlQuery.append("}");

		List<String> uris = new ArrayList<String>();

		QueryExecution qe = QueryExecutionFactory.sparqlService("http://geo.linkeddata.es/sparql", sparqlQuery.toString());
		try {
			ResultSet rs = qe.execSelect();
			for (; rs.hasNext();) {
				QuerySolution sol = (QuerySolution) rs.next();
				if (sol.get("?label") != null)
					uris.add(sol.get("?label").toString());
				Resource resource = sol.getResource("?subject");
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}
}
