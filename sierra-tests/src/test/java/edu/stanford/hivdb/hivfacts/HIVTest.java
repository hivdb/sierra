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


import org.junit.Test;


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
	public void testGetDrugSynonymMap() {

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
	public void testGetDrugSynonymMapWithException() {

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