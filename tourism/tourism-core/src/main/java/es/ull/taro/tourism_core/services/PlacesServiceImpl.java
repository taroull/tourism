package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.RDFDataMgr;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import es.ull.taro.tourism_core.domain.BeachResource;

@Service("placesService")
public class PlacesServiceImpl implements PlacesService {

	@Override
	public List<es.ull.taro.tourism_core.domain.TDTResource> find(String name) {

		Model model = loadRDFFile();

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX places: <http://purl.org/ontology/places#> ");
		sparqlQuery.append("PREFIX tdt: <http://turismo-de-tenerife.org/def/turismo#> ");

		sparqlQuery.append("SELECT ?beach ?title ?lat ?long ?PostCode");
		sparqlQuery.append("{ ");
		sparqlQuery.append("  ?beach a places:Beach. ");
		sparqlQuery.append(" OPTIONAL {  ?beach tdt:ows_LinkTitle ?title. ");
		sparqlQuery.append("  ?beach tdt:ows_Georeferencia ?geoPoint. ");
		sparqlQuery.append("  ?geoPoint a geo:Point. ");
		sparqlQuery.append("  ?geoPoint geo:lat ?lat. ");
		sparqlQuery.append("  ?geoPoint geo:long ?long. }");
		sparqlQuery.append("OPTIONAL {?beach tdt:ows_CodigoPostal ?PostCode . }");
		
		sparqlQuery.append("  FILTER regex(?title, \"").append(name).append("\", \"i\"). ");
		sparqlQuery.append("}");

		List<es.ull.taro.tourism_core.domain.TDTResource> resources = new ArrayList<es.ull.taro.tourism_core.domain.TDTResource>();

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				es.ull.taro.tourism_core.domain.TDTResource resource = new es.ull.taro.tourism_core.domain.TDTResource(sol.getResource("?beach").getURI().toString());
				resource.setName(sol.getLiteral("?title").toString());
				if(sol.getLiteral("?PostCode") != null)
					resource.setPostalCode(sol.getLiteral("?PostCode").toString());
				else
					resource.setPostalCode("-----");
				if (sol.getLiteral("?lat") != null && !sol.getLiteral("?lat").toString().isEmpty())
					resource.setLatitude(sol.getLiteral("?lat").getFloat());
				else
					resource.setLatitude(Float.parseFloat("0"));
				if (sol.getLiteral("?long") != null  && !sol.getLiteral("?long").toString().isEmpty())
					resource.setLongitude(sol.getLiteral("?long").getFloat());
				else
					resource.setLongitude(Float.parseFloat("0"));
				resources.add(resource);
			}
		} finally {
			qe.close();
		}

		return resources;
	}

	@Override
	public List<String> findBeachesAround(float latitude, float longitude, int radiusInMeters) {

		Model model = loadRDFFile();

		// radius is specified in meters, but to make the query, we have to
		// divide the radius by 100.000
		double convertedRadius = Double.valueOf(radiusInMeters) / 100000;

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX places: <http://purl.org/ontology/places#> ");
		sparqlQuery.append("PREFIX tdt: <http://turismo-de-tenerife.org/def/turismo#> ");

		sparqlQuery.append("SELECT ?beach ");
		sparqlQuery.append("WHERE { ");
		sparqlQuery.append("  ?beach a places:Beach. ");
		sparqlQuery.append("  ?beach tdt:ows_Georeferencia ?geoPoint. ");
		sparqlQuery.append("  ?geoPoint a geo:Point. ");
		sparqlQuery.append("  ?geoPoint geo:lat ?lat. ");
		sparqlQuery.append("  ?geoPoint geo:long ?long. ");
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
				Resource resource = sol.getResource("?beach");
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}

	@Override
	public Map<String, String> describeUri(String uri) {

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
		sparqlQuery2.append("PREFIX tdt: <http://turismo-de-tenerife.org/def/turismo#>");
		sparqlQuery2
				.append("SELECT ?Name ?Zone ?PostalCode ?Address ?Telephone ?Fax ?URL ?Remark ?Email ?Municipality ?Photo ?SandColor ?Swell ?HowToGet ?Category ?Recommendation ?BeachLenght ?ActivityType ?Situation ?BlueFlag ?Nudist ?FirstAid ?Flammable ?Endangerment");
		sparqlQuery2.append("{?resource tdt:ows_LinkTitle ?Name . ");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Zona ?Zone . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_CodigoPostal ?PostalCode . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Direccion ?Address . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Telefono ?Telephone. }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Fax ?Fax . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Web ?URL. }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_ObservacionesSituacion ?Remark . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_DescripcionEspanol ?Description . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Email ?Email . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Municipio ?Municipality .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Image ?Photo . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_ColorArena ?SandColor .} ");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_TipoOleaje ?Swell .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_ComoLlegarEspanol ?HowToGet . } ");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Categorias ?Category . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Recomendado ?Recommendation . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_LongitudPlaya ?BeachLenght .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_AnchoPlaya ?BeachWidth . }");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_TipoActividad ?ActivityType .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Situacion ?Situation .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_BanderaAzul ?BlueFlag .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Nudista ?Nudist .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Socorrismo ?FirstAid .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_MaterialInf ?Flammable .}");
		sparqlQuery2.append("OPTIONAL {?resource tdt:ows_Peligrosidad ?Endangerment .}}");

		QueryExecution qe2 = QueryExecutionFactory.create(sparqlQuery2.toString(), resultModel);
		HashMap<String, String> results = new HashMap<String, String>();
		try {
			com.hp.hpl.jena.query.ResultSet ns = qe2.execSelect();
			while (ns.hasNext()) {
				QuerySolution soln = ns.nextSolution();
				results.put("Nombre", soln.getLiteral("?Name").toString());
				if (soln.getLiteral("?ActivityType") != null)
					results.put("Tipo de actividad", soln.getLiteral("?ActivityType").toString());
				if (soln.getLiteral("?Zone") != null)
					results.put("Zona", soln.getLiteral("?Zone").toString());
				if (soln.getLiteral("?PostalCode") != null)
					results.put("Código Postal", soln.getLiteral("?PostalCode").toString());
				if (soln.getLiteral("?Address") != null)
					results.put("Dirección", soln.getLiteral("?Address").toString());
				if (soln.getLiteral("?Telephone") != null)
					results.put("Teléfono", soln.getLiteral("?Telephone").toString());
				if (soln.getLiteral("?Fax") != null)
					results.put("Fax", soln.getLiteral("?Fax").toString());
				if (soln.getLiteral("?URL") != null)
					results.put("URL", soln.getLiteral("?URL").toString());
				if (soln.getLiteral("?Remark") != null)
					results.put("Observaciones", soln.getLiteral("?Remark").toString());
				if (soln.getLiteral("?Description") != null)
					results.put("Description", soln.getLiteral("?Description").toString());
				if (soln.getLiteral("?Email") != null)
					results.put("Correo", soln.getLiteral("?Email").toString());
				if (soln.getLiteral("?Municipality") != null)
					results.put("Municipio", soln.getLiteral("?Municipality").toString());
				if (soln.getLiteral("?SandColor") != null)
					results.put("Color de la arena", soln.getLiteral("?SandColor").toString());
				if (soln.getLiteral("?Swell") != null)
					results.put("Tipo de oleaje", soln.getLiteral("?Swell").toString());
				if (soln.getLiteral("?HowToGet") != null)
					results.put("Cómo llegar", soln.getLiteral("?HowToGet").toString());
				if (soln.getLiteral("?Category") != null)
					results.put("Categorías", soln.getLiteral("?Category").toString());
				if (soln.getLiteral("?Recommendation") != null)
					results.put("Recomendación", soln.getLiteral("?Recommendation").toString());
				if (soln.getLiteral("?BeachLenght") != null)
					results.put("Longitud de la playa", soln.getLiteral("?BeachLenght").toString());
				if (soln.getLiteral("?BeachWidth") != null)
					results.put("Ancho de la playa", soln.getLiteral("?BeachWidth").toString());
				if (soln.getLiteral("?Situation") != null)
					results.put("Situación", soln.getLiteral("?Situation").toString());
				if (soln.getLiteral("?BlueFlag") != null)
					results.put("Bandera azul", soln.getLiteral("?BlueFlag").toString());
				if (soln.getLiteral("?Nudist") != null)
					results.put("Nudista", soln.getLiteral("?Nudist").toString());
				if (soln.getLiteral("?FirstAid") != null)
					results.put("Socorrismo", soln.getLiteral("?FirstAid").toString());
				if (soln.getLiteral("?Flammable") != null)
					results.put("Material Inflamable", soln.getLiteral("?Flammable").toString());
				if (soln.getLiteral("?Endangerment") != null)
					results.put("Peligrosidad", soln.getLiteral("?Endangerment").toString());
			}
		} finally {
			qe2.close();
		}
		return results;
	}

	@Override
	public BeachResource createBeachResource(String uri) {
		Resource resource = retrieve(uri);
		BeachResource beachResource = new BeachResource();
		Model m = loadRDFFile();

		beachResource.setUri(resource.getURI());
		Statement ows_Georeferencia = resource.getProperty(m.createProperty("http://turismo-de-tenerife.org/def/turismo#ows_Georeferencia"));
		float latitude = ows_Georeferencia.getProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat")).getLiteral().getFloat();
		float longitude = ows_Georeferencia.getProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long")).getLiteral().getFloat();
		String postalCode = resource.getProperty(m.createProperty("http://turismo-de-tenerife.org/def/turismo#ows_CodigoPostal")).getLiteral().getString();

		beachResource.setPostalCode(postalCode);
		beachResource.setLatitude(latitude);
		beachResource.setLongitude(longitude);

		return beachResource;
	}

	protected Model loadRDFFile() {
		return RDFDataMgr.loadModel("playas.rdf");
	}

	@Override
	public Resource retrieve(String uri) {
		Model model = loadRDFFile();
		return model.getResource(uri);
	}
}
