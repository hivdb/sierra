/*

    Copyright (C) 2020 Stanford HIVDB team

    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;


public class HIVTest {

	final static HIV hiv = HIV.getInstance();

	@Test
	public void testGetInstance() {
		HIV hiv1 = HIV.getInstance();
		HIV hiv2 = HIV.getInstance();
		assertSame(hiv1, hiv2);
    }

    @Test
    public void testgetDrugClasses() {
        assertNotNull(hiv.getDrugClasses());
    }

    @Test
    public void testGetStrain() {
    	assertNotNull(hiv.getStrain("HIV1"));
    }

    @Test
    public void testGetDrugClass() {
    	assertEquals(hiv.getDrugClass("PI").getAbstractGene(), "PR");
    	assertEquals(hiv.getDrugClass("NRTI").getAbstractGene(), "RT");
    	assertEquals(hiv.getDrugClass("NNRTI").getAbstractGene(), "RT");
    	assertEquals(hiv.getDrugClass("INSTI").getAbstractGene(), "IN");
    }

    @Test
    public void testDrugClassesFullName() {
    	assertEquals(
    			hiv.getDrugClass("PI").getFullName(),
    			"Protease Inhibitor"

    			);
    	assertEquals(
    			hiv.getDrugClass("NRTI").getFullName(),
    			"Nucleoside Reverse Transcriptase Inhibitor"
    			);
    	assertEquals(
    			hiv.getDrugClass("NNRTI").getFullName(),
    			"Non-nucleoside Reverse Transcriptase Inhibitor"
    			);
    	assertEquals(
    			hiv.getDrugClass("INSTI").getFullName(),
    			"Integrase Strand Transfer Inhibitor"
    			);

    }

    @Test
    public void testGetDrugClassSynonymMap() {
    	assertEquals(hiv.getDrugClassSynonymMap().get("PI").getName(), "PI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("NRTI").getName(), "NRTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("NNRTI").getName(), "NNRTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("INSTI").getName(), "INSTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("INI").getName(), "INSTI");

    }

    @Test
    public void testGetDrugs() {

    	List<Drug<HIV>> piExpecteds = new ArrayList<>();
    	piExpecteds.add(hiv.getDrug("ATV"));
    	piExpecteds.add(hiv.getDrug("DRV"));
    	piExpecteds.add(hiv.getDrug("FPV"));
    	piExpecteds.add(hiv.getDrug("IDV"));
    	piExpecteds.add(hiv.getDrug("LPV"));
    	piExpecteds.add(hiv.getDrug("NFV"));
    	piExpecteds.add(hiv.getDrug("SQV"));
    	piExpecteds.add(hiv.getDrug("TPV"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("PI").getDrugs()), piExpecteds);

    	List<Drug<HIV>> nrtiExpecteds = new ArrayList<>();
    	nrtiExpecteds.add(hiv.getDrug("ABC"));
    	nrtiExpecteds.add(hiv.getDrug("AZT"));
    	nrtiExpecteds.add(hiv.getDrug("D4T"));
    	nrtiExpecteds.add(hiv.getDrug("DDI"));
    	nrtiExpecteds.add(hiv.getDrug("FTC"));
    	nrtiExpecteds.add(hiv.getDrug("LMV"));
    	nrtiExpecteds.add(hiv.getDrug("TDF"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("NRTI").getDrugs()), nrtiExpecteds);

    	List<Drug<HIV>> nnrtiExpecteds = new ArrayList<>();
    	nnrtiExpecteds.add(hiv.getDrug("DOR"));
    	nnrtiExpecteds.add(hiv.getDrug("EFV"));
    	nnrtiExpecteds.add(hiv.getDrug("ETR"));
    	nnrtiExpecteds.add(hiv.getDrug("NVP"));
    	nnrtiExpecteds.add(hiv.getDrug("RPV"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("NNRTI").getDrugs()), nnrtiExpecteds);

    	List<Drug<HIV>> instiExpecteds = new ArrayList<>();

    	instiExpecteds.add(hiv.getDrug("BIC"));
    	instiExpecteds.add(hiv.getDrug("DTG"));
    	instiExpecteds.add(hiv.getDrug("EVG"));
    	instiExpecteds.add(hiv.getDrug("RAL"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("INSTI").getDrugs()), instiExpecteds);
    }

    @Test
    public void testClassOfDrugs() {

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
    public void testNumberOfDrugs() {
    	assertEquals(hiv.getDrugs().size(), 24);

    }

    @Test
    public void testDrugFullName() {

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
	public void testDrugDisplayAbbr() {

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

	@Test
	public void testDrugGetSynonym() {

		assertEquals(hiv.getDrugSynonymMap().get("ABC"),   hiv.getDrug("ABC"));
		assertEquals(hiv.getDrugSynonymMap().get("AZT"),   hiv.getDrug("AZT"));
		assertEquals(hiv.getDrugSynonymMap().get("D4T"),   hiv.getDrug("D4T"));
		assertEquals(hiv.getDrugSynonymMap().get("DDI"),   hiv.getDrug("DDI"));
		assertEquals(hiv.getDrugSynonymMap().get("FTC"),   hiv.getDrug("FTC"));
		assertEquals(hiv.getDrugSynonymMap().get("3TC"),   hiv.getDrug("LMV"));
		assertEquals(hiv.getDrugSynonymMap().get("LMV"),   hiv.getDrug("LMV"));
		assertEquals(hiv.getDrugSynonymMap().get("TDF"),   hiv.getDrug("TDF"));
		assertEquals(hiv.getDrugSynonymMap().get("ATV/r"), hiv.getDrug("ATV"));
		assertEquals(hiv.getDrugSynonymMap().get("DRV/r"), hiv.getDrug("DRV"));
		assertEquals(hiv.getDrugSynonymMap().get("FPV/r"), hiv.getDrug("FPV"));
		assertEquals(hiv.getDrugSynonymMap().get("IDV/r"), hiv.getDrug("IDV"));
		assertEquals(hiv.getDrugSynonymMap().get("LPV/r"), hiv.getDrug("LPV"));
		assertEquals(hiv.getDrugSynonymMap().get("ATV"),   hiv.getDrug("ATV"));
		assertEquals(hiv.getDrugSynonymMap().get("DRV"),   hiv.getDrug("DRV"));
		assertEquals(hiv.getDrugSynonymMap().get("FPV"),   hiv.getDrug("FPV"));
		assertEquals(hiv.getDrugSynonymMap().get("IDV"),   hiv.getDrug("IDV"));
		assertEquals(hiv.getDrugSynonymMap().get("LPV"),   hiv.getDrug("LPV"));
		assertEquals(hiv.getDrugSynonymMap().get("NFV"),   hiv.getDrug("NFV"));
		assertEquals(hiv.getDrugSynonymMap().get("SQV/r"), hiv.getDrug("SQV"));
		assertEquals(hiv.getDrugSynonymMap().get("TPV/r"), hiv.getDrug("TPV"));
		assertEquals(hiv.getDrugSynonymMap().get("SQV"),   hiv.getDrug("SQV"));
		assertEquals(hiv.getDrugSynonymMap().get("TPV"),   hiv.getDrug("TPV"));
		assertEquals(hiv.getDrugSynonymMap().get("EFV"),   hiv.getDrug("EFV"));
		assertEquals(hiv.getDrugSynonymMap().get("ETR"),   hiv.getDrug("ETR"));
		assertEquals(hiv.getDrugSynonymMap().get("NVP"),   hiv.getDrug("NVP"));
		assertEquals(hiv.getDrugSynonymMap().get("RPV"),   hiv.getDrug("RPV"));
		assertEquals(hiv.getDrugSynonymMap().get("DTG"),   hiv.getDrug("DTG"));
		assertEquals(hiv.getDrugSynonymMap().get("EVG"),   hiv.getDrug("EVG"));
		assertEquals(hiv.getDrugSynonymMap().get("RAL"),   hiv.getDrug("RAL"));
	}

	@Test
	public void testDrugGetSynonymWithException() {

		assertNull(hiv.getDrugSynonymMap().get(""));
		assertNull(hiv.getDrugSynonymMap().get("EVH"));
	}

	@Test
	public void testGetApobecMutations() {
		assertNotNull(hiv.getApobecMutations());
	}


	@Test
	public void testGetApobecDRMs() {
		assertNotNull(hiv.getApobecDRMs());
	}

}