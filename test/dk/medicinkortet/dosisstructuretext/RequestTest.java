package dk.medicinkortet.dosisstructuretext;

import junit.framework.TestCase;

public class RequestTest extends TestCase{
	
	public void testRequest() {
		try {
			Runner.run("test/CreateDrugMedicationRequest-1.xml");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

}
