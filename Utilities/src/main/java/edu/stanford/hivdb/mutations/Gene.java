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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.stanford.hivdb.drugs.DrugClass;

/**
 *
 * enum for Gene. Will replace the previous class-wrapper-around enum implementation now called Gene
 *
 */
public class Gene implements Comparable<Gene> {
	
	private static final Map<String, Gene> singletons;
	
	static {
		Map<String, Gene> _singletons = new HashMap<>();
		
		// LANL Consensus B
		_singletons.put(
			"HIV1PR",
			new Gene(
				Strain.HIV1, GeneEnum.PR, // 99 AAs
				"PQITLWQRPLVTIKIGGQLKEALLDTGADDTVLEEMNLPGRWKPKMIGGI" +
				"GGFIKVRQYDQILIEICGHKAIGTVLVGPTPVNIIGRNLLTQIGCTLNF",
				/* firstNA */2253));
		_singletons.put(
			"HIV1RT",
			new Gene(
				Strain.HIV1, GeneEnum.RT, // 560 AAs
				"PISPIETVPVKLKPGMDGPKVKQWPLTEEKIKALVEICTEMEKEGKISKI" +
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
				"LVSAGIRKVL",
				/* firstNA */2550));
		_singletons.put(
			"HIV1IN",
			new Gene(
				Strain.HIV1, GeneEnum.IN, // 288 AAs
				"FLDGIDKAQEEHEKYHSNWRAMASDFNLPPVVAKEIVASCDKCQLKGEAM" +
				"HGQVDCSPGIWQLDCTHLEGKIILVAVHVASGYIEAEVIPAETGQETAYF" +
				"LLKLAGRWPVKTIHTDNGSNFTSTTVKAACWWAGIKQEFGIPYNPQSQGV" +
				"VESMNKELKKIIGQVRDQAEHLKTAVQMAVFIHNFKRKGGIGGYSAGERI" +
				"VDIIATDIQTKELQKQITKIQNFRVYYRDSRDPLWKGPAKLLWKGEGAVV" +
				"IQDNSDIKVVPRRKAKIIRDYGKQMAGDDCVASRQDED",
				/* firstNA */4230));

		// ROD
		_singletons.put(
			"HIV2APR",
			new Gene(
				Strain.HIV2A, GeneEnum.PR, // 99 AAs
				"PQFSLWKRPVVTAYIEGQPVEVLLDTGADDSIVAGIELGNNYSPKIVGGI" +
				"GGFINTKEYKNVEIEVLNKKVRATIMTGDTPINIFGRNILTALGMSLNL",
				/* firstNA */2253)); // TODO: update firstNA to ROD
		_singletons.put(
			"HIV2ART",
			new Gene(
				Strain.HIV2A, GeneEnum.RT, // 559 AAs
				"PVAKVEPIKIMLKPGKDGPKLRQWPLTKEKIEALKEICEKMEKEGQLEEA" +
				"PPTNPYNTPTFAIKKKDKNKWRMLIDFRELNKVTQDFTEIQLGIPHPAGL" +
				"AKKRRITVLDVGDAYFSIPLHEDFRPYTAFTLPSVNNAEPGKRYIYKVLP" +
				"QGWKGSPAIFQHTMRQVLEPFRKANKDVIIIQYMDDILIASDRTDLEHDR" +
				"VVLQLKELLNGLGFSTPDEKFQKDPPYHWMGYELWPTKWKLQKIQLPQKE" +
				"IWTVNDIQKLVGVLNWAAQLYPGIKTKHLCRLIRGKMTLTEEVQWTELAE" +
				"AELEENRIILSQEQEGHYYQEEKELEATVQKDQENQWTYKIHQEEKILKV" +
				"GKYAKVKNTHTNGIRLLAQVVQKIGKEALVIWGRIPKFHLPVEREIWEQW" +
				"WDNYWQVTWIPDWDFVSTPPLVRLAFNLVGDPIPGAETFYTDGSCNRQSK" +
				"EGKAGYVTDRGKDKVKKLEQTTNQQAELEAFAMALTDSGPKVNIIVDSQY" +
				"VMGISASQPTESESKIVNQIIEEMIKKEAIYVAWVPAHKGIGGNQEVDHL" +
				"VSQGIRQVL",
				/* firstNA */2550)); // TODO: update firstNA to ROD
		_singletons.put(
			"HIV2AIN",
			new Gene(
				Strain.HIV2A, GeneEnum.IN, // 293 AAs
				"FLEKIEPAQEEHEKYHSNVKELSHKFGIPNLVARQIVNSCAQCQQKGEAI" +
				"HGQVNAELGTWQMDCTHLEGKIIIVAVHVASGFIEAEVIPQESGRQTALF" +
				"LLKLASRWPITHLHTDNGANFTSQEVKMVAWWIGIEQSFGVPYNPQSQGV" +
				"VEAMNHHLKNQISRIREQANTIETIVLMAIHCMNFKRRGGIGDMTPSERL" +
				"INMITTEQEIQFLQAKNSKLKDFRVYFREGRDQLWKGPGELLWKGEGAVL" +
				"VKVGTDIKIIPRRKAKIIRDYGGRQEMDSGSHLEGAREDGEMA",
				/* firstNA */4230)); // TODO: update firstNA to ROD
		
		// EHO
		_singletons.put(
			"HIV2BPR",
			new Gene(
				Strain.HIV2B, GeneEnum.PR, // 99 AAs
				"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
				"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
				/* firstNA */2253)); // TODO: update firstNA to EHO
		_singletons.put(
			"HIV2BRT",
			new Gene(
				Strain.HIV2B, GeneEnum.RT, // 559 AAs
				"PVARIEPVKVQLKPEKDGPKIRQWPLSKEKILALKEICEKMEKEGQLEEA" +
				"PPTNPYNSPTFAIKKKDKNKWRMLIDFRELNKVTQEFTEVQLGIPHPAGL" +
				"ASKKRITVLDVGDAYFSVPLDPDFRQYTAFTLPAVNNAEPGKRYLYKVLP" +
				"QGWKGSPAIFQYTMAKVLDPFRKANNDVTIIQYMDDILVASDRSDLEHDR" +
				"VVSQLKELLNNMGFSTPEEKFQKDPPFKWMGYELWPKKWKLQKIQLPEKE" +
				"VWTVNDIQKLVGVLNWAAQLFPGIKTRHICKLIRGKMTLTEEVQWTELAE" +
				"AEFQENKIILEQEQEGSYYKEGVPLEATVQKNLANQWTYKIHQGDKILKV" +
				"GKYAKVKNTHTNGVRLLAHVVQKIGKEALVIWGEIPMFHLPVERETWDQW" +
				"WTDYWQVTWIPEWDFVSTPPLIRLAYNLVKDPLEGVETYYTDGSCNKASK" +
				"EGKAGYVTDRGKDKVKPLEQTTNQQAELEAFALALQDSGPQVNIIVDSQY" +
				"VMGIVAAQPTETESPIVREIIEEMIKKEKIYVGWVPAHKGLGGNQEVDHL" +
				"VSQGIRQIL",
				/* firstNA */2550)); // TODO: update firstNA to EHO
		_singletons.put(
			"HIV2BIN",
			new Gene(
				Strain.HIV2B, GeneEnum.IN, // 296 AAs
				"FLEKIEPAQEEHEKYHNNVKELVHKFGIPQLVARQIVNSCDKCQQKGEAI" +
				"HGQVNSELGTWQMDCTHLEGKVIIVAVHVASGFIEAEVIPQETGRQTALF" +
				"LLKLASRWPITHLHTDNGANFTSQDVKMAAWWIGIEQTFGVPYNPESQGV" +
				"VEAMNHHLKNQIDRIRDQAVSIETVVLMATHCMNFKRRGGIGDMTPAERI" +
				"VNMITTEQEIQFLQTKNLKFQNFRVYYREGRDQLWKGPGDLLWKGEGAVI" +
				"IKVGTEIKVIPRRKAKIIRNYGGGKELDCSADVEDTMQAREVAQSN",
				/* firstNA */4230)); // TODO: update firstNA to EHO
		
		
		singletons = Collections.unmodifiableMap(_singletons);
	}

	private final Strain strain;
	private final GeneEnum geneEnum;
	private final String reference;
	private final int firstNA;

	private Gene(Strain strain, GeneEnum gene, String reference, int firstNA) {
		this.strain = strain;
		this.geneEnum = gene;
		this.reference = reference;
		this.firstNA = firstNA;
	}

	public static Gene valueOf(String strainText, String geneText) {
		return singletons.get(String.format("%s%s", strainText, geneText));
	}

	public static Gene valueOf(Strain strain, String geneText) {
		return singletons.get(String.format("%s%s", strain, geneText));
	}

	public static Gene valueOf(Strain strain, GeneEnum gene) {
		return singletons.get(String.format("%s%s", strain, gene));
	}
	
	public static Gene valueOf(String strainGeneText) {
		return singletons.get(strainGeneText);
	}
	
	public static Gene[] values(String strainText) {
		return new Gene[] {
			singletons.get(strainText + "PR"),
			singletons.get(strainText + "RT"),
			singletons.get(strainText + "IN")
		};
	}

	public static Gene[] values(Strain strain) {
		return new Gene[] {
			singletons.get(strain + "PR"),
			singletons.get(strain + "RT"),
			singletons.get(strain + "IN")
		};
	}

	/**
	 * Get the drug classes associated with a gene
	 */
	public List<DrugClass> getDrugClasses() {
		List<DrugClass> drugClasses = new ArrayList<>();
		switch(geneEnum) {
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
		switch(geneEnum) {
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
		switch(geneEnum) {
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
		return this.reference.length();
	}
	
	public int getFirstNA() {
		return this.firstNA;
	}
	
	public int getNASize() {
		return this.reference.length() * 3;
	}

	/**
	 * Get the reference amino acid (AA) at a position in a gene
	 * Indexed starting from 1
	 * @param pos
	 * @return the AA at the submitted position
	 */
	public String getReference(int pos) {
		return getReference(pos, 1);
	}

	public String getReference(int pos, int length) {
		return this.reference.substring(pos - 1, pos - 1 + length);
	}

	public String getReference() {
		return this.reference;
	}
	
	public Strain getStrain() {
		return this.strain;
	}
	
	public GeneEnum getGeneEnum() {
		return this.geneEnum;
	}
	
	public String getNameWithStrain() {
		return this.toString();
	}
	
	public String getName() {
		return this.geneEnum.name();
	}
	
	@Override
	public String toString() {
		return String.format("%s%s", strain, geneEnum);
	}

	@Override
	public int compareTo(Gene o) {
		if (o == null) throw new NullPointerException("Null is incomprable.");
		int cmp = strain.compareTo(o.strain);
		if (cmp == 0) {
			cmp = geneEnum.compareTo(o.geneEnum);
		}
		return cmp;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(23564345, 437136943)
			.append(strain).append(geneEnum).toHashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		// singleton only has one reference point
		return this == o;
		
	}

}
