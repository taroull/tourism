package es.ull.taro.tourism_core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.jsonldjava.core.JsonLdError;
import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.HTOResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class HTOServiceTest {

	@Autowired
	private AccommodationService accommodationService;

	@Autowired
	private GastroService gastroService;

	@Autowired
	private CoreService coreService;

	@Test
	public void testFindAccommodations() {
		HashMap<String, String> accommodations = accommodationService.find("Aguere");
		assertEquals(accommodations.size(), 1);
		assertEquals(accommodations.get(0), "http://turismo-de-tenerife.org/resource/alojamiento/Aguere");
	}

	@Test
	public void testFindGastros() {
		HashMap<String, String> accommodations = gastroService.find("avencio");
		assertEquals(accommodations.size(), 1);
		assertEquals(accommodations.get(0), "http://turismo-de-tenerife.org/resource/restauracion/AVENCIO");
	}

	@Test
	public void testRetrieveGastro() {
		String uri = "http://turismo-de-tenerife.org/resource/restauracion/TROPICANA";
		Resource resource = gastroService.retrieve(uri);
		assertNotNull(resource);
	}

	@Test
	public void testCreateHTOResource() {
		String uri = "http://turismo-de-tenerife.org/resource/restauracion/EL%20CONDUTE";
		HTOResource htoResource = gastroService.createHtoResource(uri);
		assertNotNull(htoResource);
	}

	@Test
	public void testRetrieveMunicipalityInfo() throws JsonLdError {
		String uri = "http://turismo-de-tenerife.org/resource/restauracion/LOS_ASADORES";
		List<String> uris = coreService.retrieveMunicipalityInfo(uri);
		assertNotNull(uris);
	}
}
