package es.ull.taro.tourism_core;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.jena.JenaRDFParser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaRDFParserTest {

	public void test() throws JsonLdError {

		final StringBuilder turtle = new StringBuilder();
		turtle.append("@prefix const: <http://foo.com/> .\n");
		turtle.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n");
		turtle.append("<http://localhost:8080/foo1> const:code \"123\" .\n");
		turtle.append("<http://localhost:8080/foo2> const:code \"ABC\"^^xsd:string .\n");

		final List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>() {
			{
				add(new LinkedHashMap<String, Object>() {
					{
						put("@id", "http://localhost:8080/foo1");
						put("http://foo.com/code", new ArrayList<Object>() {
							{
								add(new LinkedHashMap<String, Object>() {
									{
										put("@value", "123");
									}
								});
							}
						});
					}
				});
				add(new LinkedHashMap<String, Object>() {
					{
						put("@id", "http://localhost:8080/foo2");
						put("http://foo.com/code", new ArrayList<Object>() {
							{
								add(new LinkedHashMap<String, Object>() {
									{
										put("@value", "ABC");
									}
								});
							}
						});
					}
				});
			}
		};

		final Model modelResult = ModelFactory.createDefaultModel().read(new ByteArrayInputStream(turtle.toString().getBytes()), "", "TURTLE");
		final JenaRDFParser parser = new JenaRDFParser();
		final Object json = JsonLdProcessor.fromRDF(modelResult, parser);

	}
}