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

import java.util.Collections;
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

	DOR("doravirine", "DOR", DrugClass.NNRTI, true),
	EFV("efavirenz", "EFV", DrugClass.NNRTI, true),
	ETR("etravirine", "ETR", DrugClass.NNRTI, true),
	NVP("nevirapine", "NVP", DrugClass.NNRTI, true),
	RPV("rilpivirine", "RPV", DrugClass.NNRTI, true),

	BIC("bictegravir", "BIC", DrugClass.INSTI, true),
	DTG("dolutegravir", "DTG", DrugClass.INSTI, true),
	EVG("elvitegravir", "EVG", DrugClass.INSTI, true),
	RAL("raltegravir", "RAL",DrugClass.INSTI, true);

	private static final Map<String, Drug> drugSynonyms;

	private final String fullName;
	private final String displayAbbr;
	private final DrugClass drugClass;
	public final boolean forHivdbResistanceTesting;
	
	static {
		Map<String, Drug> tmpDrugSynonyms = new HashMap<>();
		tmpDrugSynonyms.put("LPV/r", Drug.LPV);
		tmpDrugSynonyms.put("IDV/r", Drug.IDV);
		tmpDrugSynonyms.put("FPV/r", Drug.FPV);
		tmpDrugSynonyms.put("ATV/r", Drug.ATV);
		tmpDrugSynonyms.put("DRV/r", Drug.DRV);
		tmpDrugSynonyms.put("TPV/r", Drug.TPV);
		tmpDrugSynonyms.put("SQV/r", Drug.SQV);
		tmpDrugSynonyms.put("3TC",  Drug.LMV);
		// ANRS distinguishes QD and BID for DRV/r and DTG;
		// By default we only use BID here
		tmpDrugSynonyms.put("DRV/r_QD", Drug.DRV);
		tmpDrugSynonyms.put("DTG_QD", Drug.DTG);
		drugSynonyms = Collections.unmodifiableMap(tmpDrugSynonyms);
	}

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
		if (drugSynonyms.containsKey(synonym)) {
			return drugSynonyms.get(synonym);
		} else {
			try {
				return Drug.valueOf(synonym);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}
}