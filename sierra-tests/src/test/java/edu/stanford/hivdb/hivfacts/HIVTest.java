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
    	assertNotNull(hiv.getDrugClass("PI"));
    	assertNotNull(hiv.getDrugClass("NRTI"));
    	assertNotNull(hiv.getDrugClass("NNRTI"));
    	assertNotNull(hiv.getDrugClass("INSTI"));
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