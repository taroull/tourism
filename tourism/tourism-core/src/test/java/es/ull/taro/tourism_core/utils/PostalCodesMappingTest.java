package es.ull.taro.tourism_core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class PostalCodesMappingTest {

	@Test
	public void testGetMunicipalityCode() {
		String municipalityCode = PostalCodesMapping.getInstance().getMunicipalityCode("38350");
		assertEquals("38043", municipalityCode);
	}

	@Test
	public void testGetPostalCodes() {
		List<String> postalCodes = PostalCodesMapping.getInstance().getPostalCodes("38037");
		Set<String> postalCodesSet = new HashSet<String>(postalCodes);
		assertTrue(postalCodesSet.contains("38713"));
		assertTrue(postalCodesSet.contains("38714"));
		assertTrue(postalCodesSet.contains("38700"));
		assertTrue(postalCodesSet.contains("38712"));
		assertTrue(postalCodesSet.contains("38715"));
		assertEquals(5, postalCodesSet.size());
	}
}
