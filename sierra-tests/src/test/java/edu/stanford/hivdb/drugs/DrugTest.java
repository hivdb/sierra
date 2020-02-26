package edu.stanford.hivdb.drugs;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

public class DrugTest {
    final static HIV hiv = HIV.getInstance();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testLoadJSON() {
    	// WARNING: This test would fail each time new drug is added.
    	assertEquals(hiv.getDrugs().size(), 24);
    }

    @Test
    public void testDrug() {
    	// WARNING: Drug private Constructor is for GSON
    }

    @Test
    public void testSetVirusInstance() {
    	Drug<HIV> drug = hiv.getDrug("ABC");

    	drug.setVirusInstance(hiv);
    }

    @Test
    public void testGetDrugClassWithCheckVirusInstance() {
    	Drug<HIV> drug = hiv.getDrug("ABC");
    	assertTrue(drug.getDrugClass() instanceof DrugClass);

    	drug.setVirusInstance(null);

    	thrown.expect(ExceptionInInitializerError.class);
    	thrown.expectMessage("Object not properly initialzed: virusInstance is empty");
    	drug.getDrugClass();
    }

    @Test
    public void testName() {
    	assertEquals(hiv.getDrug("ABC").name(), "ABC");
    }

    @Test
    public void testGetName() {
    	assertEquals(hiv.getDrug("ABC").getName(), "ABC");
    }

    @Test
    public void testGetFullName() {
    	assertEquals(hiv.getDrug("ABC").getFullName(), "abacavir");
    }

    @Test
    public void testGetSynonyms() {
    	assertEquals(hiv.getDrug("ABC").getSynonyms().size(), 0);
    	assertEquals(hiv.getDrug("DRV").getSynonyms().size(), 1);
    }

    @Test
    public void testGetDisplayAbbr() {
    	assertEquals(hiv.getDrug("ABC").getDisplayAbbr(), "ABC");
    }

    @Test
    public void testToString() {
    	assertEquals(hiv.getDrug("ABC").toString(), hiv.getDrug("ABC").name());
    }

    @Test
    public void testEquals() {
    	assertTrue(hiv.getDrug("ABC").equals(hiv.getDrug("ABC")));
    	assertFalse(hiv.getDrug("ABC").equals(hiv.getDrug("DRV")));
    	
    	assertFalse(hiv.getDrug("ABC").equals(null));
    }

    @Test
    public void testEqualsWithException() {
    	thrown.expect(NullPointerException.class);
    	assertFalse(hiv.getDrug("ABC").equals(hiv.getDrug(null)));
    }

    @Test
    public void testHashCode() {
    	assertEquals(hiv.getDrug("ABC").hashCode(), hiv.getDrug("ABC").hashCode());
    	assertEquals(hiv.getDrug("ABC").hashCode(), hiv.getDrug("ABC").hashCode());

    	assertFalse(hiv.getDrug("ABC").hashCode() == hiv.getDrug("DRV").hashCode());
    	assertFalse(hiv.getDrug("ABC").hashCode() == hiv.getDrug("DRV").hashCode());
    }

    @Test
    public void testCompareTo() {
    	assertEquals(hiv.getDrug("ABC").compareTo(hiv.getDrug("DRV")), -3);

    	assertEquals(hiv.getDrug("ABC").compareTo(hiv.getDrug("ABC")), 0);
    }

    // INFO: Start of Full Drug Test
    @Test
    public void testFullDrugs() {
		// WARNING: This test would fail every time a drug is added.
    	// INFO: You should update all the fullDrugs test below adding new drug and also update this test.
  		assertEquals(hiv.getDrugs().size(), 24);

    }

    @Test
    public void testGetDrugClassFullDrugs() {

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

    	assertEquals(hiv.getDrug("DOR").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("EFV").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("ETR").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("NVP").getDrugClass(), NNRTI);
    	assertEquals(hiv.getDrug("RPV").getDrugClass(), NNRTI);

    	assertEquals(hiv.getDrug("BIC").getDrugClass(), INSTI);
    	assertEquals(hiv.getDrug("DTG").getDrugClass(), INSTI);
    	assertEquals(hiv.getDrug("EVG").getDrugClass(), INSTI);
    	assertEquals(hiv.getDrug("RAL").getDrugClass(), INSTI);

    }


    @Test
    public void testGetFullNameFullDrugs() {

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

		assertEquals(hiv.getDrug("DOR").getFullName(), "doravirine");
		assertEquals(hiv.getDrug("EFV").getFullName(), "efavirenz");
		assertEquals(hiv.getDrug("ETR").getFullName(), "etravirine");
		assertEquals(hiv.getDrug("NVP").getFullName(), "nevirapine");
		assertEquals(hiv.getDrug("RPV").getFullName(), "rilpivirine");

		assertEquals(hiv.getDrug("BIC").getFullName(), "bictegravir");
		assertEquals(hiv.getDrug("DTG").getFullName(), "dolutegravir");
		assertEquals(hiv.getDrug("EVG").getFullName(), "elvitegravir");
		assertEquals(hiv.getDrug("RAL").getFullName(), "raltegravir");
    }

    @Test
    public void testGetSynonymsFullDrugs() {

		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("ABC").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("AZT").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("D4T").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("DDI").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("FTC").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("LMV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("TDF").getSynonyms().toArray()), "");

		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("ATV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("DRV").getSynonyms().toArray()), "DRV/r_QD");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("FPV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("IDV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("LPV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("NFV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("SQV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("TPV").getSynonyms().toArray()), "");

		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("DOR").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("EFV").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("ETR").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("NVP").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("RPV").getSynonyms().toArray()), "");

		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("BIC").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("DTG").getSynonyms().toArray()), "DTG_QD");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("EVG").getSynonyms().toArray()), "");
		assertEquals(StringUtils.joinWith(", ", hiv.getDrug("RAL").getSynonyms().toArray()), "");
    }

	@Test
	public void testGetDisplayAbbrFullDrugs() {

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

		assertEquals(hiv.getDrug("DOR").getDisplayAbbr(), "DOR");
		assertEquals(hiv.getDrug("EFV").getDisplayAbbr(), "EFV");
		assertEquals(hiv.getDrug("ETR").getDisplayAbbr(), "ETR");
		assertEquals(hiv.getDrug("NVP").getDisplayAbbr(), "NVP");
		assertEquals(hiv.getDrug("RPV").getDisplayAbbr(), "RPV");

		assertEquals(hiv.getDrug("BIC").getDisplayAbbr(), "BIC");
		assertEquals(hiv.getDrug("DTG").getDisplayAbbr(), "DTG");
		assertEquals(hiv.getDrug("EVG").getDisplayAbbr(), "EVG");
		assertEquals(hiv.getDrug("RAL").getDisplayAbbr(), "RAL");
	}
}