package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.jsonldjava.core.JsonLdError;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.GeoResource;
import es.ull.taro.tourism_core.vocabularies.DBpedia;
import es.ull.taro.tourism_core.vocabularies.WGS84;

@Service("dBpediaService")
public class DBpediaServiceImpl extends BaseServiceImpl implements DBpediaService {

	@Autowired
	private CoreService coreService;

	@Override
	public List<String> retrieveMunicipalityInfo(String postalCode) throws JsonLdError {

		StringBuilder dbpediaQuery = new StringBuilder();
		dbpediaQuery.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		dbpediaQuery.append("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		dbpediaQuery.append("PREFIX dbpprop: <http://dbpedia.org/property/> ");
		dbpediaQuery.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ");
		dbpediaQuery.append("SELECT DISTINCT ?city ");
		dbpediaQuery.append("WHERE {");
		dbpediaQuery.append("  ?city rdf:type dbpedia-owl:PopulatedPlace . ");
		dbpediaQuery.append("  ?city rdfs:label ?label .");
		dbpediaQuery.append("  ?city dbpedia-owl:country ?country . ");
		dbpediaQuery.append("  ?country dbpprop:commonName  \"Spain\"@en. ");
		dbpediaQuery.append("  ?city  dbpedia-owl:postalCode \"").append(postalCode).append("\"@en . ");
		dbpediaQuery.append("}");

		List<String> uris = new ArrayList<String>();

		QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", dbpediaQuery.toString());
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				Resource resource = sol.getResource("?city");
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}

	@Override
	public List<String> retrieveMunicipalityInfoES(String postalCode) throws JsonLdError {

		StringBuilder dbpediaQuery = new StringBuilder();
		dbpediaQuery.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ");
		dbpediaQuery.append("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		dbpediaQuery.append("SELECT DISTINCT ?city ");
		dbpediaQuery.append("WHERE {");
		dbpediaQuery.append("  ?city rdf:type dbpedia-owl:PopulatedPlace . ");
		dbpediaQuery.append("  ?city dbpedia-owl:country <http://es.dbpedia.org/resource/Spain> . ");
		dbpediaQuery.append("  ?city dbpedia-owl:postalCode \"").append(postalCode).append("\"@es . ");
		dbpediaQuery.append("}");

		List<String> uris = new ArrayList<String>();

		QueryExecution qe = QueryExecutionFactory.sparqlService("http://es.dbpedia.org/sparql", dbpediaQuery.toString());
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				Resource resource = sol.getResource("?city");
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}

	@Override
	public List<GeoResource> find(String query) throws JsonLdError {

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX dbpprop: <http://dbpedia.org/property/> ");
		sparqlQuery.append("SELECT DISTINCT ?resource ");
		sparqlQuery.append("WHERE {?resource dbpprop:name ?label ");
		sparqlQuery.append("FILTER regex(?label, \"").append(query).append("\", \"i\")}");

		QueryExecution qe = QueryExecutionFactory.sparqlService(DBpedia.SPARQL_ENDPOINT, sparqlQuery.toString());

		List<GeoResource> result = new ArrayList<GeoResource>();

		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				Resource resource = sol.getResource("?resource");
				result.add(new GeoResource(resource.getURI()));
			}
		} finally {
			qe.close();
		}
		return result;
	}

	@Override
	public Map<String, String> describeUri(String uri) {

		StringBuilder dbpediaQuery = new StringBuilder();
		dbpediaQuery.append("DESCRIBE ").append("<").append(uri).append(">");

		QueryExecution qe = QueryExecutionFactory.sparqlService("http://es.dbpedia.org/sparql", dbpediaQuery.toString());
		Model resultModel;
		try {
			resultModel = qe.execDescribe();
		} finally {
			qe.close();
		}
		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		sparqlQuery.append("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		sparqlQuery.append("PREFIX dbpprop: <http://dbpedia.org/property/> ");
		sparqlQuery.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ");
		sparqlQuery.append("PREFIX prop-es: <http://es.dbpedia.org/property/> ");
		sparqlQuery.append("PREFIX foaf:  <http://xmlns.com/foaf/0.1/>");
		sparqlQuery
				.append("SELECT ?Name ?Demonym ?FoundingYear ?LeaderName ?Address ?Sede ?Elevation ?AreaCode ?MunicipalityCode ?ProvinceCode ?Description ?URL ?FlagPhoto ?WikiLink ?OtherLink ?Population");
		sparqlQuery.append("{");
		sparqlQuery.append("?resource rdfs:label ?Name . FILTER (lang(?Name) = \"es\") ");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:demonym ?Demonym . }");
		sparqlQuery.append("OPTIONAL {?resource prop-es:fundación ?FoundingYear . }");
		sparqlQuery.append("OPTIONAL {?resource prop-es:alcalde ?LeaderName . }");
		sparqlQuery.append("OPTIONAL {?resource prop-es:direcciónDelAyuntamiento ?Address . }");
		sparqlQuery.append("OPTIONAL {?resource prop-es:altitud ?Elevation .}");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:thumbnail ?FlagPhoto .}");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:areaCode ?AreaCode .}");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:municipalityCode ?MunicipalityCode . }");
		sparqlQuery.append("OPTIONAL {?resource prop-es:cod ?ProvinceCode . }");
		sparqlQuery.append("OPTIONAL {?resource prop-es:sede ?Sede .}");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:abstract ?Description .}");
		sparqlQuery.append("OPTIONAL {?resource foaf:homepage ?URL . }");
		sparqlQuery.append("OPTIONAL {?resource foaf:isPrimaryTopicOf ?WikiLink .}");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:wikiPageExternalLink ?OtherLink .}");
		sparqlQuery.append("OPTIONAL {?resource dbpedia-owl:populationTotal ?Population .}");
		sparqlQuery.append("}");

		HashMap<String, String> results = new HashMap<String, String>();
		QueryExecution qe2 = QueryExecutionFactory.create(sparqlQuery.toString(), resultModel);
		try {
			ResultSet rs = qe2.execSelect();
			for (; rs.hasNext();) {
				QuerySolution sol = (QuerySolution) rs.next();
				if (sol.get("?Name") != null)
					results.put("Nombre", sol.get("?Name").toString());
				if (sol.get("?Demonym") != null)
					results.put("Gentilicio", sol.get("?Demonym").toString());
				if (sol.get("?FoundingYear") != null)
					results.put("Año de fundación", sol.get("?FoundingYear").toString());
				if (sol.get("?LeaderName") != null)
					results.put("Alcalde", sol.get("?LeaderName").toString());
				if (sol.get("?Address") != null)
					results.put("Dirección del ayuntamiento", sol.get("?Address").toString());
				if (sol.get("?Sede") != null)
					results.put("Sede", sol.get("?Sede").toString());
				if (sol.get("?Elevation") != null)
					results.put("Altitud", sol.get("?Elevation").toString());
				if (sol.get("?FlagPhoto") != null)
					results.put("Bandera", sol.get("?FlagPhoto").toString());
				if (sol.get("?AreaCode") != null)
					results.put("Código de Área", sol.get("?AreaCode").toString());
				if (sol.get("?MunicipalityCode") != null)
					results.put("Código del municipio", sol.get("?MunicipalityCode").toString());
				if (sol.get("?ProvinceCode") != null)
					results.put("Código de provincia", sol.get("?ProvinceCode").toString());
				if (sol.get("?Description") != null)
					results.put("Description", sol.get("?Description").toString());
				if (sol.get("?URL") != null)
					results.put("URL", sol.get("?URL").toString());
				if (sol.get("?WikiLink") != null)
					results.put("Enlace a Wikipedia", sol.get("?WikiLink").toString());
				if (sol.get("?OtherLink") != null)
					results.put("Otros enlaces", sol.get("?OtherLink").toString());
				if (sol.get("?Population") != null)
					results.put("Número de habitantes", sol.get("?Population").toString());
			}
		} finally {
			qe.close();
		}

		return results;
	}

	@Override
	public String getVocabulary() {
		return WGS84.VOCABULARY;
	}

	@Override
	public String getLatitudeProperty() {
		return WGS84.LATITUDE;
	}

	@Override
	public String getLongitudeProperty() {
		return WGS84.LONGITUDE;
	}
}
