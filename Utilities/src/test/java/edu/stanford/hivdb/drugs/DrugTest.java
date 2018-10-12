/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
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

package edu.stanford.hivdb.drugs;

import static org.junit.Assert.*;

import org.junit.Test;

public class DrugTest {

	@Test
	public void testDrugCount() {
		// This test failed every time when a new drug is added.
		// WARNING: Don't just fix this test case. Add the new drug
		// to all following cases too.
		assertEquals(Drug.values().length, 24);
	}

	@Test
	public void testGetDrugClass() {

		final DrugClass PI = DrugClass.PI;
		final DrugClass NRTI = DrugClass.NRTI;
		final DrugClass NNRTI = DrugClass.NNRTI;
		final DrugClass INSTI = DrugClass.INSTI;

		assertEquals(Drug.ABC.getDrugClass(), NRTI);
		assertEquals(Drug.AZT.getDrugClass(), NRTI);
		assertEquals(Drug.D4T.getDrugClass(), NRTI);
		assertEquals(Drug.DDI.getDrugClass(), NRTI);
		assertEquals(Drug.FTC.getDrugClass(), NRTI);
		assertEquals(Drug.LMV.getDrugClass(), NRTI);
		assertEquals(Drug.TDF.getDrugClass(), NRTI);

		assertEquals(Drug.ATV.getDrugClass(), PI);
		assertEquals(Drug.DRV.getDrugClass(), PI);
		assertEquals(Drug.FPV.getDrugClass(), PI);
		assertEquals(Drug.IDV.getDrugClass(), PI);
		assertEquals(Drug.LPV.getDrugClass(), PI);
		assertEquals(Drug.NFV.getDrugClass(), PI);
		assertEquals(Drug.SQV.getDrugClass(), PI);
		assertEquals(Drug.TPV.getDrugClass(), PI);

		assertEquals(Drug.EFV.getDrugClass(), NNRTI);
		assertEquals(Drug.ETR.getDrugClass(), NNRTI);
		assertEquals(Drug.NVP.getDrugClass(), NNRTI);
		assertEquals(Drug.RPV.getDrugClass(), NNRTI);

		assertEquals(Drug.DTG.getDrugClass(), INSTI);
		assertEquals(Drug.EVG.getDrugClass(), INSTI);
		assertEquals(Drug.RAL.getDrugClass(), INSTI);
	}

	@Test
	public void testGetFullName() {
		assertEquals(Drug.ABC.getFullName(), "abacavir");
		assertEquals(Drug.AZT.getFullName(), "zidovudine");
		assertEquals(Drug.D4T.getFullName(), "stavudine");
		assertEquals(Drug.DDI.getFullName(), "didanosine");
		assertEquals(Drug.FTC.getFullName(), "emtricitabine");
		assertEquals(Drug.LMV.getFullName(), "lamivudine");
		assertEquals(Drug.TDF.getFullName(), "tenofovir");

		assertEquals(Drug.ATV.getFullName(), "atazanavir/r");
		assertEquals(Drug.DRV.getFullName(), "darunavir/r");
		assertEquals(Drug.FPV.getFullName(), "fosamprenavir/r");
		assertEquals(Drug.IDV.getFullName(), "indinavir/r");
		assertEquals(Drug.LPV.getFullName(), "lopinavir/r");
		assertEquals(Drug.NFV.getFullName(), "nelfinavir");
		assertEquals(Drug.SQV.getFullName(), "saquinavir/r");
		assertEquals(Drug.TPV.getFullName(), "tipranavir/r");

		assertEquals(Drug.EFV.getFullName(), "efavirenz");
		assertEquals(Drug.ETR.getFullName(), "etravirine");
		assertEquals(Drug.NVP.getFullName(), "nevirapine");
		assertEquals(Drug.RPV.getFullName(), "rilpivirine");

		assertEquals(Drug.DTG.getFullName(), "dolutegravir");
		assertEquals(Drug.EVG.getFullName(), "elvitegravir");
		assertEquals(Drug.RAL.getFullName(), "raltegravir");
	}

	@Test
	public void testGetDisplayAbbr() {
		assertEquals(Drug.ABC.getDisplayAbbr(), "ABC");
		assertEquals(Drug.AZT.getDisplayAbbr(), "AZT");
		assertEquals(Drug.D4T.getDisplayAbbr(), "D4T");
		assertEquals(Drug.DDI.getDisplayAbbr(), "DDI");
		assertEquals(Drug.FTC.getDisplayAbbr(), "FTC");
		assertEquals(Drug.LMV.getDisplayAbbr(), "3TC");
		assertEquals(Drug.TDF.getDisplayAbbr(), "TDF");

		assertEquals(Drug.ATV.getDisplayAbbr(), "ATV/r");
		assertEquals(Drug.DRV.getDisplayAbbr(), "DRV/r");
		assertEquals(Drug.FPV.getDisplayAbbr(), "FPV/r");
		assertEquals(Drug.IDV.getDisplayAbbr(), "IDV/r");
		assertEquals(Drug.LPV.getDisplayAbbr(), "LPV/r");
		assertEquals(Drug.NFV.getDisplayAbbr(), "NFV");
		assertEquals(Drug.SQV.getDisplayAbbr(), "SQV/r");
		assertEquals(Drug.TPV.getDisplayAbbr(), "TPV/r");

		assertEquals(Drug.EFV.getDisplayAbbr(), "EFV");
		assertEquals(Drug.ETR.getDisplayAbbr(), "ETR");
		assertEquals(Drug.NVP.getDisplayAbbr(), "NVP");
		assertEquals(Drug.RPV.getDisplayAbbr(), "RPV");

		assertEquals(Drug.DTG.getDisplayAbbr(), "DTG");
		assertEquals(Drug.EVG.getDisplayAbbr(), "EVG");
		assertEquals(Drug.RAL.getDisplayAbbr(), "RAL");
	}

	@Test
	public void testGetSynonym() {
		assertEquals(Drug.getSynonym("ABC"), Drug.ABC);
		assertEquals(Drug.getSynonym("AZT"), Drug.AZT);
		assertEquals(Drug.getSynonym("D4T"), Drug.D4T);
		assertEquals(Drug.getSynonym("DDI"), Drug.DDI);
		assertEquals(Drug.getSynonym("FTC"), Drug.FTC);
		assertEquals(Drug.getSynonym("3TC"), Drug.LMV);
		assertEquals(Drug.getSynonym("LMV"), Drug.LMV);
		assertEquals(Drug.getSynonym("TDF"), Drug.TDF);

		assertEquals(Drug.getSynonym("ATV/r"), Drug.ATV);
		assertEquals(Drug.getSynonym("DRV/r"), Drug.DRV);
		assertEquals(Drug.getSynonym("FPV/r"), Drug.FPV);
		assertEquals(Drug.getSynonym("IDV/r"), Drug.IDV);
		assertEquals(Drug.getSynonym("LPV/r"), Drug.LPV);
		assertEquals(Drug.getSynonym("ATV"), Drug.ATV);
		assertEquals(Drug.getSynonym("DRV"), Drug.DRV);
		assertEquals(Drug.getSynonym("FPV"), Drug.FPV);
		assertEquals(Drug.getSynonym("IDV"), Drug.IDV);
		assertEquals(Drug.getSynonym("LPV"), Drug.LPV);
		assertEquals(Drug.getSynonym("NFV"), Drug.NFV);
		assertEquals(Drug.getSynonym("SQV/r"), Drug.SQV);
		assertEquals(Drug.getSynonym("TPV/r"), Drug.TPV);
		assertEquals(Drug.getSynonym("SQV"), Drug.SQV);
		assertEquals(Drug.getSynonym("TPV"), Drug.TPV);

		assertEquals(Drug.getSynonym("EFV"), Drug.EFV);
		assertEquals(Drug.getSynonym("ETR"), Drug.ETR);
		assertEquals(Drug.getSynonym("NVP"), Drug.NVP);
		assertEquals(Drug.getSynonym("RPV"), Drug.RPV);

		assertEquals(Drug.getSynonym("DTG"), Drug.DTG);
		assertEquals(Drug.getSynonym("EVG"), Drug.EVG);
		assertEquals(Drug.getSynonym("RAL"), Drug.RAL);

	}

	@Test
	public void testValueOf() {
		assertEquals(Drug.valueOf("ABC"), Drug.ABC);
		assertEquals(Drug.valueOf("AZT"), Drug.AZT);
		assertEquals(Drug.valueOf("D4T"), Drug.D4T);
		assertEquals(Drug.valueOf("DDI"), Drug.DDI);
		assertEquals(Drug.valueOf("FTC"), Drug.FTC);
		assertEquals(Drug.valueOf("LMV"), Drug.LMV);
		assertEquals(Drug.valueOf("TDF"), Drug.TDF);

		assertEquals(Drug.valueOf("ATV"), Drug.ATV);
		assertEquals(Drug.valueOf("DRV"), Drug.DRV);
		assertEquals(Drug.valueOf("FPV"), Drug.FPV);
		assertEquals(Drug.valueOf("IDV"), Drug.IDV);
		assertEquals(Drug.valueOf("LPV"), Drug.LPV);
		assertEquals(Drug.valueOf("NFV"), Drug.NFV);
		assertEquals(Drug.valueOf("SQV"), Drug.SQV);
		assertEquals(Drug.valueOf("TPV"), Drug.TPV);

		assertEquals(Drug.valueOf("EFV"), Drug.EFV);
		assertEquals(Drug.valueOf("ETR"), Drug.ETR);
		assertEquals(Drug.valueOf("NVP"), Drug.NVP);
		assertEquals(Drug.valueOf("RPV"), Drug.RPV);

		assertEquals(Drug.valueOf("DTG"), Drug.DTG);
		assertEquals(Drug.valueOf("EVG"), Drug.EVG);
		assertEquals(Drug.valueOf("RAL"), Drug.RAL);

	}

}
