package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		dbpediaQuery.append("  ?city      rdf:type dbpedia-owl:PopulatedPlace . ");
		dbpediaQuery.append("  ?city      rdfs:label ?label .");
		dbpediaQuery.append("  ?city      dbpedia-owl:country ?country . ");
		dbpediaQuery.append("  ?country   dbpprop:commonName  \"Spain\"@en. ");
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
				// FIXME QuerySolution sol = (QuerySolution) results.next();
				// FIXME Resource resource = sol.getResource("?resource");
				// FIXME result.add(createGeoResource(resource));
			}
		} finally {
			qe.close();
		}
		return result;
	}

	@Override
	public HashMap<String, String> describeUri(String uri) {

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
					results.put(sol.get("?Name").toString(), "Nombre");
				if (sol.get("?Demonym") != null)
					results.put(sol.get("?Demonym").toString(), "Gentilicio");
				if (sol.get("?FoundingYear") != null)
					results.put(sol.get("?FoundingYear").toString(), "Año de fundación");
				if (sol.get("?LeaderName") != null)
					results.put(sol.get("?LeaderName").toString(), "Alcalde");
				if (sol.get("?Address") != null)
					results.put(sol.get("?Address").toString(), "Dirección del ayuntamiento");
				if (sol.get("?Sede") != null)
					results.put(sol.get("?Sede").toString(), "Sede");
				if (sol.get("?Elevation") != null)
					results.put(sol.get("?Elevation").toString(), "Altitud");
				if (sol.get("?FlagPhoto") != null)
					results.put(sol.get("?FlagPhoto").toString(), "Bandera");
				if (sol.get("?AreaCode") != null)
					results.put(sol.get("?AreaCode").toString(), "Código de Área");
				if (sol.get("?MunicipalityCode") != null)
					results.put(sol.get("?MunicipalityCode").toString(), "Código del municipio");
				if (sol.get("?ProvinceCode") != null)
					results.put(sol.get("?ProvinceCode").toString(), "Código de provincia");
				if (sol.get("?Description") != null)
					results.put(sol.get("?Description").toString(), "Description");
				if (sol.get("?URL") != null)
					results.put(sol.get("?URL").toString(), "URL");
				if (sol.get("?WikiLink") != null)
					results.put(sol.get("?WikiLink").toString(), "Enlace a Wikipedia");
				if (sol.get("?OtherLink") != null)
					results.put(sol.get("?OtherLink").toString(), "Otros enlaces");
				if (sol.get("?Population") != null)
					results.put(sol.get("?Population").toString(), "Número de habitantes");
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
