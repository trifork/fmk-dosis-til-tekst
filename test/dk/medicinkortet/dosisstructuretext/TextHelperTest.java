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
