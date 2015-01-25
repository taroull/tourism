package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

@Service("aemetService")
public class AemetServiceImpl implements AemetService {

	@Override
	public List<String> findWeatherStationsAround(float latitude, float longitude, int radiusInMeters) {

		// radius is specified in meters, but to make the query, we have to
		// divide the radius by 100.000
		double convertedRadius = Double.valueOf(radiusInMeters) / 100000;

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX ssn:	 <http://purl.oclc.org/NET/ssnx/ssn#> ");
		sparqlQuery.append("PREFIX aemet:	 <http://aemet.linkeddata.es/ontology/> ");
		sparqlQuery.append("PREFIX w3ctime:<http://www.w3.org/2006/time#> ");

		sparqlQuery.append("SELECT  ?station ");
		sparqlQuery.append("WHERE { ");
		sparqlQuery.append("  ?station aemet:stationName ?name . ");
		sparqlQuery.append("  ?station a <http://aemet.linkeddata.es/ontology/WeatherStation>. ");
		sparqlQuery.append("  ?station geo:location ?location. ");
		sparqlQuery.append("  ?location geo:lat ?lat. ");
		sparqlQuery.append("  ?location geo:long ?long. ");
		sparqlQuery.append("FILTER(xsd:double(?lat) - xsd:double('").append(latitude).append("') <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double('").append(latitude).append("') - xsd:double(?lat) <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double(?long) - xsd:double('").append(longitude).append("') <= ").append(convertedRadius);
		sparqlQuery.append("  && xsd:double('").append(longitude).append("') - xsd:double(?long) <= ").append(convertedRadius)
				.append(" ). ");
		sparqlQuery.append("}");

		List<String> uris = new ArrayList<String>();

		QueryExecution qe = QueryExecutionFactory.sparqlService("http://aemet.linkeddata.es/sparql", sparqlQuery.toString());
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				Resource resource = sol.getResource("?station");
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}

	@Override
	public HashMap<String, String> showWeatherStationProps(String uri) {

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX ssn:	 <http://purl.oclc.org/NET/ssnx/ssn#> ");
		sparqlQuery.append("PREFIX aemet:	 <http://aemet.linkeddata.es/ontology/> ");
		sparqlQuery.append("PREFIX w3ctime:<http://www.w3.org/2006/time#> ");

		sparqlQuery.append("SELECT ?DateTime { ");
		sparqlQuery.append("?Station geo:location <" + uri + "> .");
		sparqlQuery.append("?Obs ssn:observedBy ?Station .");
		sparqlQuery.append("?Obs aemet:observedInInterval ?Inter .");
		sparqlQuery.append("?Inter w3ctime:hasBeginning ?Instant .");
		sparqlQuery.append("?Instant w3ctime:inDateTime ?DateTimeDescription .");
		sparqlQuery.append("?DateTimeDescription w3ctime:inXSDDateTime ?DateTime . }");
		sparqlQuery.append(" ORDER BY DESC(?DateTime)");
		sparqlQuery.append("LIMIT 1 ");

		QueryExecution initQuery = QueryExecutionFactory.sparqlService("http://aemet.linkeddata.es/sparql", sparqlQuery.toString());
		String lastPrediction;
		try {
			ResultSet results = initQuery.execSelect();
			QuerySolution sol = (QuerySolution) results.next();
			lastPrediction = sol.getLiteral("?DateTime").toString();
		} finally {
			initQuery.close();
		}
		String date = lastPrediction.replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "Z\"^^xsd:dateTime");
		String newDate = date.replaceFirst(" ", "T");

		HashMap<String, String> props = new HashMap<String, String>();
		sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX ssn:	 <http://purl.oclc.org/NET/ssnx/ssn#> ");
		sparqlQuery.append("PREFIX aemet:	 <http://aemet.linkeddata.es/ontology/> ");
		sparqlQuery.append("PREFIX w3ctime:<http://www.w3.org/2006/time#> ");
		sparqlQuery.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>");

		sparqlQuery.append("SELECT ?PropName ?PropValue ?Comment { ");
		sparqlQuery.append("?Station geo:location <" + uri + "> .");
		sparqlQuery.append("?Obs ssn:observedBy ?Station .");
		sparqlQuery.append("?Obs ssn:observedProperty ?Prop.");
		sparqlQuery.append("?Prop rdfs:label ?PropName .");
		sparqlQuery.append("?Prop rdfs:comment ?Comment .");
		sparqlQuery.append("?Obs aemet:valueOfObservedData ?PropValue .");
		sparqlQuery.append("?Obs aemet:observedInInterval ?Inter .");
		sparqlQuery.append("?Inter w3ctime:hasBeginning ?Instant .");
		sparqlQuery.append("?Instant w3ctime:inDateTime ?DateTimeDescription .");
		sparqlQuery.append("?DateTimeDescription w3ctime:inXSDDateTime \"" + newDate + " . }");

		QueryExecution finalQuery = QueryExecutionFactory.sparqlService("http://aemet.linkeddata.es/sparql", sparqlQuery.toString());
		try {
			ResultSet results = finalQuery.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				String value = sol.getLiteral("?PropValue").toString() + " " + sol.getLiteral("?Comment").toString();
				props.put(sol.getLiteral("?PropName").toString(), value);
			}
		} finally {
			finalQuery.close();
		}

		return props;
	}
}
