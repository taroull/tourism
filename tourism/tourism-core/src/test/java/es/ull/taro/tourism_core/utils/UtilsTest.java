package es.ull.taro.tourism_core.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void testObtainNameFromDbpediaURI() {
		String uri = "http://dbpedia.org/resource/Puerto_de_la_Cruz";
		String name = Utils.getNameFromDbpediaURI(uri);
		Assert.assertEquals("Puerto_de_la_Cruz", name);
	}
}
