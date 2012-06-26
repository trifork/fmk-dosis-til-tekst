/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class TextHelperTest {

	@Test
	public void testTrim() {
		assertEquals("400", TextHelper.trim("400"));
		assertEquals("0", TextHelper.trim("0.000"));
		assertEquals("0", TextHelper.trim("0,000"));
		assertEquals("0", TextHelper.trim("0,"));
		assertEquals("0.1", TextHelper.trim("0.100"));
		assertEquals("0,01", TextHelper.trim("0,010"));
	}
	
	@Test
	public void testBigDecimal() {
		assertEquals("0", TextHelper.formatQuantity(new BigDecimal("0.0")));
		assertEquals("0,1", TextHelper.formatQuantity(new BigDecimal("0.1")));
		assertEquals("0,01", TextHelper.formatQuantity(new BigDecimal("0.01")));
		assertEquals("0,000000001", TextHelper.formatQuantity(new BigDecimal("0.000000001")));
		assertEquals("999999999,999999999", TextHelper.formatQuantity(new BigDecimal("999999999.999999999")));
	}

}
