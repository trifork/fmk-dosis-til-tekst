package dk.medicinkortet.dosagetranslation.wrapper;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosagetranslation.DosageToTextTranslator;
import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosagetranslation.wrapper.DosageWrapperWrapper;

public class DosageWrapperWrapperTest {

	@Test
	public void test1() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test", 
				"dråbe", "dråber", 
				"{N daglig}{N daglig}", "{0}{1}", "{2;2;2;2}{2;2}", null);
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringen indeholder flere perioder:\n"+
				"\n"+
				"Doseringen foretages kun mandag den 3. juni 2013:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag den 3. juni 2013: 2 dråber 4 gange\n"+
				"\n"+
				"Doseringsforløbet starter tirsdag den 4. juni 2013, gentages hver dag, og ophører mandag den 1. juni 2015:\n"+
				"   Doseringsforløb:\n"+
				"   2 dråber 2 gange daglig",
				definition.getLongText());
		Assert.assertEquals(
				"første dag 2 dråber 4 gange, herefter 2 dråber 2 gange daglig", 
				definition.getShortText());
		
	}
	
	@Test
	public void test2() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"tablet", "tabletter", 
				"{M+M+A+N}{PN}", "{1}{0}", "{0+0+0+20}{20}", null);
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringsforløbet starter mandag den 3. juni 2013, gentages hver dag, og ophører mandag den 1. juni 2015:\n"+
				"   Doseringsforløb:\n"+
				"   20 tabletter før sengetid + 20 tabletter efter behov",
				definition.getLongText());
//		Assert.assertEquals(
//				"20 tabletter før sengetid, samt 20 tabletter efter behov, højst 1 gang daglig", 
//				definition.getShortText());
	}
	
	@Test
	public void test3() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"tablet", "tabletter", 
				"N daglig", "28", "dag 1: 1 dag 2: 1 dag 3: 1 dag 4: 1 dag 5: 1 dag 6: 1 dag 7: 1 dag 8: 1 dag 9: 1 dag 10: 1 dag 11: 1 dag 12: 1 dag 13: 1 dag 14: 1 dag 15: 1 dag 16: 1 dag 17: 1 dag 18: 1 dag 19: 1 dag 20: 1 dag 21: 1", null);
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringsforløbet starter mandag den 3. juni 2013, forløbet gentages efter 28 dage, og ophører mandag den 1. juni 2015:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag den 3. juni 2013: 1 tablet\n"+
				"   Tirsdag den 4. juni 2013: 1 tablet\n"+
				"   Onsdag den 5. juni 2013: 1 tablet\n"+
				"   Torsdag den 6. juni 2013: 1 tablet\n"+
				"   Fredag den 7. juni 2013: 1 tablet\n"+
				"   Lørdag den 8. juni 2013: 1 tablet\n"+
				"   Søndag den 9. juni 2013: 1 tablet\n"+
				"   Mandag den 10. juni 2013: 1 tablet\n"+
				"   Tirsdag den 11. juni 2013: 1 tablet\n"+
				"   Onsdag den 12. juni 2013: 1 tablet\n"+
				"   Torsdag den 13. juni 2013: 1 tablet\n"+
				"   Fredag den 14. juni 2013: 1 tablet\n"+
				"   Lørdag den 15. juni 2013: 1 tablet\n"+
				"   Søndag den 16. juni 2013: 1 tablet\n"+
				"   Mandag den 17. juni 2013: 1 tablet\n"+
				"   Tirsdag den 18. juni 2013: 1 tablet\n"+
				"   Onsdag den 19. juni 2013: 1 tablet\n"+
				"   Torsdag den 20. juni 2013: 1 tablet\n"+
				"   Fredag den 21. juni 2013: 1 tablet\n"+
				"   Lørdag den 22. juni 2013: 1 tablet\n"+
				"   Søndag den 23. juni 2013: 1 tablet",
				definition.getLongText());
		Assert.assertEquals(
				"1 tablet daglig i 3 uger, herefter en uges pause", 
				definition.getShortText());
	}
	
	@Test
	public void test4() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"tablet", "tabletter", 
				"N daglig", "28", "dag 1: 1 dag 2: 1 dag 3: 1 dag 4: 1 dag 5: 1 dag 6: 1 dag 7: 1 dag 8: 1 dag 9: 1 dag 10: 1", "optimalt fra 15.-24. cyklusdag");
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringsforløbet starter mandag den 3. juni 2013, forløbet gentages efter 28 dage, og ophører mandag den 1. juni 2015:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag den 3. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Tirsdag den 4. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Onsdag den 5. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Torsdag den 6. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Fredag den 7. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Lørdag den 8. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Søndag den 9. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Mandag den 10. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Tirsdag den 11. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag\n"+
				"   Onsdag den 12. juni 2013: 1 tablet optimalt fra 15.-24. cyklusdag",
				definition.getLongText());
		Assert.assertEquals(
				"1 tablet daglig i 10 dage, herefter 18 dages pause", 
				definition.getShortText());
	}
	
	@Test
	public void test5() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"kapsel", "kapsler", 
				"M+M+A+N", "0", "dag 2: 1+0+0+0 dag 3: 1+0+0+0", null);
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringsforløbet starter mandag den 3. juni 2013, og ophører mandag den 1. juni 2015.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Tirsdag den 4. juni 2013: 1 kapsel morgen\n"+
				"   Onsdag den 5. juni 2013: 1 kapsel morgen",
				definition.getLongText());
		Assert.assertEquals(
				"1 kapsel morgen dag 2 og 3", 
				definition.getShortText());
	}

	@Test
	public void test6() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"tablet", "tabletter", 
				"N daglig", "0", "dag 1: 1 dag 4: 4", null);
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringsforløbet starter mandag den 3. juni 2013, og ophører mandag den 1. juni 2015.\n"+
				"Bemærk at doseringen varierer og har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag den 3. juni 2013: 1 tablet\n"+
				"   Torsdag den 6. juni 2013: 4 tabletter",
				definition.getLongText());
//		Assert.assertEquals(
//				"1 kapsel 2. og 3. dag", 
//				definition.getShortText());
	}
	
	@Test
	public void test7() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"tablet", "tabletter", 
				"N daglig", "0", "dag 1: 1 dag 22: 1 dag 36:1", null);
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
		Assert.assertEquals(
				"Doseringsforløbet starter mandag den 3. juni 2013, og ophører mandag den 1. juni 2015.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag den 3. juni 2013: 1 tablet\n"+
				"   Mandag den 24. juni 2013: 1 tablet\n"+
				"   Mandag den 8. juli 2013: 1 tablet",
				definition.getLongText());
		Assert.assertEquals(
				"1 tablet 1 gang daglig dag 1, 22 og 36", 
				definition.getShortText());
	}
	
	@Test
	public void test8() throws Exception {
		RawDefinition definition = new RawDefinition(
				1, 28100000013L, "Test",
				"dråbe", "dråber", 
				"{N daglig}{N daglig}", "{0}{1}", "{dag 1;1;1;1;1;1;1;1;1;1;1;1 dag 2: 1;1;1;1;1;1;1;1;1;1;1;1}{1;1;1;1;1;1;}", "i venstre øje");
		DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
		DosageToTextTranslator.translate(dosage, definition);
//		Assert.assertEquals(
//				"Doseringsforløbet starter mandag den 3. juni 2013 og ophører efter det angivne forløb.\n"+
//				"Bemærk at doseringen har et komplekst forløb:\n"+
//				"   Doseringsforløb:\n"+
//				"   Mandag den 3. juni 2013: 1 tablet\n"+
//				"   Mandag den 24. juni 2013: 1 tablet\n"+
//				"   Mandag den 8. juli 2013: 1 tablet",
//				definition.getLongText());
//		Assert.assertEquals(
//				"1 dråbe 12 gange daglig i 2 dage i venstre øje, herefter 1 dråbe 6 gange daglig i venstre øje", 
//				definition.getShortText());
	}
	
    @Test
    public void test9() throws Exception {
        RawDefinition definition = new RawDefinition(
                1, 28100000013L, "Test", 
                "dråbe", "dråber", 
                "{N daglig}{N daglig}", "{0}{1}", "{2;2;2;2}{2;2}", null);
        DosageWrapper dosage = DosageWrapperWrapper.wrap(definition);
        dosage.getStructures().getStructures().iterator().next().setDosagePeriodPostfix("[postfix]");
        DosageToTextTranslator.translate(dosage, definition);
        Assert.assertEquals(
                "Doseringen indeholder flere perioder:\n"+
                "\n"+
                "Doseringen foretages kun mandag den 3. juni 2013:\n"+
                "   Doseringsforløb:\n"+
                "   Mandag den 3. juni 2013: 2 dråber 4 gange [postfix]\n"+
                "\n"+
                "Doseringsforløbet starter tirsdag den 4. juni 2013, gentages hver dag, og ophører mandag den 1. juni 2015:\n"+
                "   Doseringsforløb:\n"+
                "   2 dråber 2 gange daglig",
                definition.getLongText());
        Assert.assertEquals(
                "første dag 2 dråber 4 gange, herefter 2 dråber 2 gange daglig", 
                definition.getShortText());
        
    }
    
}
