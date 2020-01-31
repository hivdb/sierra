package edu.stanford.hivdb.drugs;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

public class DrugTest {
    final static HIV hiv = HIV.getInstance();

    @Test
    public void testGetDrugInstance() {
    	assertNotNull(hiv.getDrug("ABC"));
    }

    @Test
    public void testGetDrugClass() {

    	final DrugClass<HIV> PI = hiv.getDrugClass("PI");
    	final DrugClass<HIV> NRTI = hiv.getDrugClass("NRTI");
    	final DrugClass<HIV> NNRTI = hiv.getDrugClass("NNRTI");
    	final DrugClass<HIV> INSTI = hiv.getDrugClass("INSTI");

    	assertEquals(hiv.getDrug("ABC").getDrugClass(), NRTI);
    	assertEquals(hiv.getDrug("AZT").getDrugClass(), NRTI);
    	assertEquals(hiv.getDrug("D4T").getDrugClass(), NRTI);
    	assertEquals(hiv.getDrug("DDI").getDrugClass(), NRTI);
    	assertEquals(hiv.getDrug("FTC").getDrugClass(), NRTI);
    	assertEquals(hiv.getDrug("LMV").getDrugClass(), NRTI);
    	assertEquals(hiv.getDrug("TDF").getDrugClass(), NRTI);

    	assertEquals(hiv.getDrug("ATV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("DRV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("FPV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("IDV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("LPV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("NFV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("SQV").getDrugClass(), PI);
    	assertEquals(hiv.getDrug("TPV").getDrugClass(), PI);

    	assertEquals(hiv.getDrug("EFV").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("ETR").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("NVP").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("RPV").getDrugClass(), NNRTI);

    	assertEquals(hiv.getDrug("DTG").getDrugClass(), INSTI);
    	assertEquals(hiv.getDrug("EVG").getDrugClass(), INSTI);
    	assertEquals(hiv.getDrug("RAL").getDrugClass(), INSTI);

    }

    @Test
    public void testDrugSize() {
		// WARNING: This test will fail every time a drug is added.
		// WARNING: Don't just fix this test case. Add the new drug
		// to all following cases too.
    	assertEquals(hiv.getDrugs().size(), 24);

    }

    @Test
    public void testGetFullName() {

		assertEquals(hiv.getDrug("ABC").getFullName(), "abacavir");
		assertEquals(hiv.getDrug("AZT").getFullName(), "zidovudine");
		assertEquals(hiv.getDrug("D4T").getFullName(), "stavudine");
		assertEquals(hiv.getDrug("DDI").getFullName(), "didanosine");
		assertEquals(hiv.getDrug("FTC").getFullName(), "emtricitabine");
		assertEquals(hiv.getDrug("LMV").getFullName(), "lamivudine");
		assertEquals(hiv.getDrug("TDF").getFullName(), "tenofovir");

		assertEquals(hiv.getDrug("ATV").getFullName(), "atazanavir/r");
		assertEquals(hiv.getDrug("DRV").getFullName(), "darunavir/r");
		assertEquals(hiv.getDrug("FPV").getFullName(), "fosamprenavir/r");
		assertEquals(hiv.getDrug("IDV").getFullName(), "indinavir/r");
		assertEquals(hiv.getDrug("LPV").getFullName(), "lopinavir/r");
		assertEquals(hiv.getDrug("NFV").getFullName(), "nelfinavir");
		assertEquals(hiv.getDrug("SQV").getFullName(), "saquinavir/r");
		assertEquals(hiv.getDrug("TPV").getFullName(), "tipranavir/r");

		assertEquals(hiv.getDrug("EFV").getFullName(), "efavirenz");
		assertEquals(hiv.getDrug("ETR").getFullName(), "etravirine");
		assertEquals(hiv.getDrug("NVP").getFullName(), "nevirapine");
		assertEquals(hiv.getDrug("RPV").getFullName(), "rilpivirine");

		assertEquals(hiv.getDrug("DTG").getFullName(), "dolutegravir");
		assertEquals(hiv.getDrug("EVG").getFullName(), "elvitegravir");
		assertEquals(hiv.getDrug("RAL").getFullName(), "raltegravir");
    }

	@Test
	public void testGetDisplayAbbr() {

		assertEquals(hiv.getDrug("ABC").getDisplayAbbr(), "ABC");
		assertEquals(hiv.getDrug("AZT").getDisplayAbbr(), "AZT");
		assertEquals(hiv.getDrug("D4T").getDisplayAbbr(), "D4T");
		assertEquals(hiv.getDrug("DDI").getDisplayAbbr(), "DDI");
		assertEquals(hiv.getDrug("FTC").getDisplayAbbr(), "FTC");
		assertEquals(hiv.getDrug("LMV").getDisplayAbbr(), "3TC");
		assertEquals(hiv.getDrug("TDF").getDisplayAbbr(), "TDF");

		assertEquals(hiv.getDrug("ATV").getDisplayAbbr(), "ATV/r");
		assertEquals(hiv.getDrug("DRV").getDisplayAbbr(), "DRV/r");
		assertEquals(hiv.getDrug("FPV").getDisplayAbbr(), "FPV/r");
		assertEquals(hiv.getDrug("IDV").getDisplayAbbr(), "IDV/r");
		assertEquals(hiv.getDrug("LPV").getDisplayAbbr(), "LPV/r");
		assertEquals(hiv.getDrug("NFV").getDisplayAbbr(), "NFV");
		assertEquals(hiv.getDrug("SQV").getDisplayAbbr(), "SQV/r");
		assertEquals(hiv.getDrug("TPV").getDisplayAbbr(), "TPV/r");

		assertEquals(hiv.getDrug("EFV").getDisplayAbbr(), "EFV");
		assertEquals(hiv.getDrug("ETR").getDisplayAbbr(), "ETR");
		assertEquals(hiv.getDrug("NVP").getDisplayAbbr(), "NVP");
		assertEquals(hiv.getDrug("RPV").getDisplayAbbr(), "RPV");

		assertEquals(hiv.getDrug("DTG").getDisplayAbbr(), "DTG");
		assertEquals(hiv.getDrug("EVG").getDisplayAbbr(), "EVG");
		assertEquals(hiv.getDrug("RAL").getDisplayAbbr(), "RAL");
	}
}