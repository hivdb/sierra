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

package edu.stanford.hivdb.mutations;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hivdb.drugs.DrugClass;

/**
 *
 * enum for Gene. Will replace the previous class-wrapper-around enum implementation now called Gene
 *
 */
public enum Gene {
	PR("PQITLWQRPLVTIKIGGQLKEALLDTGADDTVLEEMNLPGRWKPKMIGGI" +
	   "GGFIKVRQYDQILIEICGHKAIGTVLVGPTPVNIIGRNLLTQIGCTLNF"),
	RT("PISPIETVPVKLKPGMDGPKVKQWPLTEEKIKALVEICTEMEKEGKISKI" +
	   "GPENPYNTPVFAIKKKDSTKWRKLVDFRELNKRTQDFWEVQLGIPHPAGL" +
	   "KKKKSVTVLDVGDAYFSVPLDKDFRKYTAFTIPSINNETPGIRYQYNVLP" +
	   "QGWKGSPAIFQSSMTKILEPFRKQNPDIVIYQYMDDLYVGSDLEIGQHRT" +
	   "KIEELRQHLLRWGFTTPDKKHQKEPPFLWMGYELHPDKWTVQPIVLPEKD" +
	   "SWTVNDIQKLVGKLNWASQIYAGIKVKQLCKLLRGTKALTEVIPLTEEAE" +
	   "LELAENREILKEPVHGVYYDPSKDLIAEIQKQGQGQWTYQIYQEPFKNLK" +
	   "TGKYARMRGAHTNDVKQLTEAVQKIATESIVIWGKTPKFKLPIQKETWEA" +
	   "WWTEYWQATWIPEWEFVNTPPLVKLWYQLEKEPIVGAETFYVDGAANRET" +
	   "KLGKAGYVTDRGRQKVVSLTDTTNQKTELQAIHLALQDSGLEVNIVTDSQ" +
	   "YALGIIQAQPDKSESELVSQIIEQLIKKEKVYLAWVPAHKGIGGNEQVDK" +
	   "LVSAGIRKVL"),
	IN("FLDGIDKAQEEHEKYHSNWRAMASDFNLPPVVAKEIVASCDKCQLKGEAM" +
	   "HGQVDCSPGIWQLDCTHLEGKIILVAVHVASGYIEAEVIPAETGQETAYF" +
	   "LLKLAGRWPVKTIHTDNGSNFTSTTVKAACWWAGIKQEFGIPYNPQSQGV" +
	   "VESMNKELKKIIGQVRDQAEHLKTAVQMAVFIHNFKRKGGIGGYSAGERI" +
	   "VDIIATDIQTKELQKQITKIQNFRVYYRDSRDPLWKGPAKLLWKGEGAVV" +
	   "IQDNSDIKVVPRRKAKIIRDYGKQMAGDDCVASRQDED");

	private final String consensus;

	private Gene(String consensus) {
		this.consensus = consensus;
	}

	/**
	 * Get the drug classes associated with a gene
	 */
	public List<DrugClass> getDrugClasses() {
		List<DrugClass> drugClasses = new ArrayList<>();
		switch(this) {
		case RT:
			drugClasses.add(DrugClass.NRTI);
			drugClasses.add(DrugClass.NNRTI);
			break;
		case PR:
			drugClasses.add(DrugClass.PI);
			break;
		case IN:
			drugClasses.add(DrugClass.INSTI);
			break;
		}
		return drugClasses;
	}

	/**
	 * Get the mutation types associated with a gene
	 */
	public List<MutType> getMutationTypes() {
		List<MutType> mutTypes = new ArrayList<>();
		switch(this) {
		case RT:
			mutTypes.add(MutType.NRTI);
			mutTypes.add(MutType.NNRTI);
			mutTypes.add(MutType.Other);
			break;
		case PR:
			mutTypes.add(MutType.Major);
			mutTypes.add(MutType.Accessory);
			mutTypes.add(MutType.Other);
			break;
		case IN:
			mutTypes.add(MutType.Major);
			mutTypes.add(MutType.Accessory);
			mutTypes.add(MutType.Other);
			break;
		}
		return mutTypes;
	}

	/**
	 * Get the scored mutation types associated with a gene
	 */
	public List<MutType> getScoredMutTypes() {
		List<MutType> mutTypes = new ArrayList<>();
		switch(this) {
		case RT:
			mutTypes.add(MutType.NRTI);
			mutTypes.add(MutType.NNRTI);
			break;
		case PR:
			mutTypes.add(MutType.Major);
			mutTypes.add(MutType.Accessory);
			break;
		case IN:
			mutTypes.add(MutType.Major);
			mutTypes.add(MutType.Accessory);
			break;
		}
		return mutTypes;
	}


	public int getLength() {
		return this.consensus.length();
	}

	/**
	 * Get the consensus amino acid (AA) at a position in a gene
	 * Indexed starting from 1
	 * @param pos
	 * @return the AA at the submitted position
	 */
	public String getConsensus(int pos) {
		return getConsensus(pos, 1);
	}

	public String getConsensus(int pos, int length) {
		return this.consensus.substring(pos - 1, pos - 1 + length);
	}

	public String getConsensus() {
		return this.consensus;
	}

}
