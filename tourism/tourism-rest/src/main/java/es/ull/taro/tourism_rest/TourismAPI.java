package es.ull.taro.tourism_rest;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.ull.taro.tourism_core.domain.GeoResource;
import es.ull.taro.tourism_core.services.AccommodationService;
import es.ull.taro.tourism_core.services.AemetService;
import es.ull.taro.tourism_core.services.CoreService;
import es.ull.taro.tourism_core.services.DBpediaService;
import es.ull.taro.tourism_core.services.GastroService;
import es.ull.taro.tourism_core.services.PlacesService;
import es.ull.taro.tourism_core.services.TourismOfficesService;

@Component
@Path("/")
public class TourismAPI {

	@Autowired
	protected DBpediaService dBpediaService;

	@Autowired
	protected CoreService coreService;

	@Autowired
	protected AccommodationService accommodationService;

	@Autowired
	protected GastroService gastroService;

	@Autowired
	protected PlacesService placesService;
	
	@Autowired
	protected TourismOfficesService tourismOfficesService;
	
	@Autowired
	protected AemetService aemetService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("resource")
	public Object getResourceByUri(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieve(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("resource2")
	public Object getResourceByUri2(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieve2(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbpediaResource")
	public List<GeoResource> findResourceByName(@QueryParam(value = "name") String name) throws Exception {
		return dBpediaService.find(name);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accommodation")
	public HashMap<String, String> findAccommodation(@QueryParam(value = "name") String name) throws Exception {
		return accommodationService.find(name);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("gastro")
	public HashMap<String, String> findGastro(@QueryParam(value = "name") String name) throws Exception {
		return gastroService.find(name);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("municipalityInfo")
	public List<String> retrieveMunicipalityInfo(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieveMunicipalityInfo(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("municipalityPhotos")
	public List<String> retrieveMunicipalityPhotos(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieveMunicipalityPhotos(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("photosAround")
	public List<String> retrievePhotosAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrievePhotosAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("placesAround")
	public List<String> retrievePlacesAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrievePlacesAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tourismOfficesAround")
	public List<String> retrieveTourismOfficesAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveTourismOfficesAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("placesNear")
	public HashMap<String, String> findPlacesNear(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.findPlacesNear(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("beachResource")
	public HashMap<String, String> findBeachByName(@QueryParam(value = "name") String name) throws Exception {
		return placesService.find(name);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tourismOfficeResource")
	public HashMap<String, String> findTourismOfficeByName(@QueryParam(value = "name") String name) throws Exception {
		return tourismOfficesService.find(name);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wheatherProps")
	public HashMap<String, String> stationProps(@QueryParam(value = "uri") String uri) throws Exception {
		return aemetService.showWeatherStationProps(uri);
	}
}
