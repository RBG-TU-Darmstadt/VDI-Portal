package de.tud.cs.rbg.vdi.web.test.integration;

import net.sourceforge.jwebunit.junit.WebTestCase;

/**
 * Some basic tests for WebInterface deployed to tomcat.
 */
public class ExampleWebTestCase extends WebTestCase {

	@Override
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setBaseUrl("http://localhost:8080/");
	}

	/**
	 * just testing if WebInterface is running in tomcat.
	 */
	public void testVMLogin() {
		beginAt("/WebInterface");
		assertTitleEquals("VDI Portal - TUD FB20");
		// clickLink("login");
		// clickLinkWithExactText("Weiter zum Login");
		// assertLinkPresentWithExactText("Neue VM");
	}
}
