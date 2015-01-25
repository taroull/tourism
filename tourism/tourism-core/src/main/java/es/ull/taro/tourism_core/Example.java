package es.ull.taro.tourism_core;


import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.jena.JenaRDFParser;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class Example {

	public static void main(String[] args) {

		String service = "http://webenemasuno.linkeddata.es/sparql";

		StringBuilder query = new StringBuilder();
		query.append("PREFIX OPMO: <http://webenemasuno.linkeddata.es/ontology/OPMO/> ");
		query.append("SELECT DISTINCT ?a WHERE {?a a OPMO:Trip} LIMIT 10 ");

		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query.toString());

		try {

			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {

				QuerySolution sol = (QuerySolution) results.next();

				Resource resource = sol.getResource("?a");

				FileManager fManager = FileManager.get();
				fManager.addLocatorURL();
				Model model = fManager.loadModel(resource.getURI());

				final JenaRDFParser parser = new JenaRDFParser();
				final Object json = JsonLdProcessor.fromRDF(model, parser);

				System.out.println(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qe.close();
		}
	}
}
