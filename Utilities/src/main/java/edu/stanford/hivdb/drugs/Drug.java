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

import java.util.HashMap;
import java.util.Map;

public enum Drug {
	ABC("abacavir", "ABC", DrugClass.NRTI, true),
	AZT("zidovudine", "AZT", DrugClass.NRTI, true),
	D4T("stavudine", "D4T",DrugClass.NRTI, true),
	DDI("didanosine", "DDI", DrugClass.NRTI, true),
	FTC("emtricitabine", "FTC", DrugClass.NRTI, true),
	LMV("lamivudine", "3TC", DrugClass.NRTI, true),
	TDF("tenofovir", "TDF", DrugClass.NRTI, true),

	ATV("atazanavir/r", "ATV/r", DrugClass.PI, true),
	DRV("darunavir/r", "DRV/r", DrugClass.PI, true),
	FPV("fosamprenavir/r", "FPV/r", DrugClass.PI, true),
	IDV("indinavir/r", "IDV/r", DrugClass.PI, true),
	LPV("lopinavir/r", "LPV/r", DrugClass.PI, true),
	NFV("nelfinavir", "NFV", DrugClass.PI, true),
	SQV("saquinavir/r", "SQV/r", DrugClass.PI, true),
	TPV("tipranavir/r", "TPV/r", DrugClass.PI, true),

	EFV("efavirenz", "EFV", DrugClass.NNRTI, true),
	ETR("etravirine", "ETR", DrugClass.NNRTI, true),
	NVP("nevirapine", "NVP", DrugClass.NNRTI, true),
	RPV("rilpivirine", "RPV", DrugClass.NNRTI, true),

	DTG("dolutegravir", "DTG", DrugClass.INSTI, true),
	EVG("elvitegravir", "EVG", DrugClass.INSTI, true),
	RAL("raltegravir", "RAL",DrugClass.INSTI, true);

	private final String fullName;
	private final String displayAbbr;
	private final DrugClass drugClass;
	public final boolean forHivdbResistanceTesting;

	private Drug(
			final String fullName, final String displayAbbr,
			final DrugClass drugClass, final boolean forHivdbResistanceTesting) {
		this.fullName = fullName;
		this.displayAbbr = displayAbbr;
		this.drugClass = drugClass;
		this.forHivdbResistanceTesting = forHivdbResistanceTesting;
	}

	public DrugClass getDrugClass() {
		return drugClass;
	}

	public String getFullName() {
		return fullName;
	}

	public String getDisplayAbbr() {
		return displayAbbr;
	}

	public static Drug getSynonym(String synonym) {
		Map<String, Drug> drugSynonyms = new HashMap<>();
		drugSynonyms.put("LPV/r", Drug.LPV);
		drugSynonyms.put("IDV/r", Drug.IDV);
		drugSynonyms.put("FPV/r", Drug.FPV);
		drugSynonyms.put("ATV/r", Drug.ATV);
		drugSynonyms.put("DRV/r", Drug.DRV);
		drugSynonyms.put("TPV/r", Drug.TPV);
		drugSynonyms.put("SQV/r", Drug.SQV);
		drugSynonyms.put("3TC",  Drug.LMV);

		if (!drugSynonyms.containsKey(synonym)) {
			return Drug.valueOf(synonym);
		} else {
			return drugSynonyms.get(synonym);
		}
	}

}
