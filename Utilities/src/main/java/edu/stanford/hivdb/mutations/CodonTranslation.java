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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.base.Splitter;

/**
 * This class has several public methods and a private constructor to prevent accessing.
 *
 * The first time it is called, the tripletsTable Map is created.
 */
public class CodonTranslation {
	private CodonTranslation() {};

	private static final String [] nas = {
		"A", "C", "G", "T", "R", "Y", "M", "W",
		"S", "K", "B", "D", "H", "V", "N"
	};
	private static final Map <String, String []> ambiguityMapping;
	private static final Map <String, Character> ambiguityInverseMapping;
	public static final Map <String, String> aaThreeToOneLetter;
	public static final Map <Character, String> aaOneToThreeLetter;
	private static Map<String, String> tripletsTable;
	private static final Map<String, String> codonToAminoAcidMap;
	private static final Map<String, List<String>> aminoAcidToCodonsMap;
	static {
		ambiguityMapping = new HashMap<String, String[]>();
		ambiguityMapping.put("A", new String[]{"A"});
		ambiguityMapping.put("C", new String[]{"C"});
		ambiguityMapping.put("G", new String[]{"G"});
		ambiguityMapping.put("T", new String[]{"T"});
		ambiguityMapping.put("R", new String[]{"A","G"});
		ambiguityMapping.put("Y", new String[]{"C","T"});
		ambiguityMapping.put("M", new String[]{"A","C"});
		ambiguityMapping.put("W", new String[]{"A","T"});
		ambiguityMapping.put("S", new String[]{"C","G"});
		ambiguityMapping.put("K", new String[]{"G","T"});
		ambiguityMapping.put("B", new String[]{"C","G","T"});
		ambiguityMapping.put("D", new String[]{"A","G","T"});
		ambiguityMapping.put("H", new String[]{"A","C","T"});
		ambiguityMapping.put("V", new String[]{"A","C","G"});
		ambiguityMapping.put("N", new String[]{"A","C","G","T"});

		ambiguityInverseMapping = new HashMap<String, Character>();
		ambiguityInverseMapping.put("A", 'A');
		ambiguityInverseMapping.put("C", 'C');
		ambiguityInverseMapping.put("G", 'G');
		ambiguityInverseMapping.put("T", 'T');
		ambiguityInverseMapping.put("AG", 'R');
		ambiguityInverseMapping.put("CT", 'Y');
		ambiguityInverseMapping.put("AC", 'M');
		ambiguityInverseMapping.put("AT", 'W');
		ambiguityInverseMapping.put("CG", 'S');
		ambiguityInverseMapping.put("GT", 'K');
		ambiguityInverseMapping.put("CGT", 'B');
		ambiguityInverseMapping.put("AGT", 'D');
		ambiguityInverseMapping.put("ACT", 'H');
		ambiguityInverseMapping.put("ACG", 'V');
		ambiguityInverseMapping.put("ACGT", 'N');

		aaThreeToOneLetter = new HashMap<String, String>();
		aaThreeToOneLetter.put("Ala","A");
		aaThreeToOneLetter.put("Cys","C");
		aaThreeToOneLetter.put("Asp","D");
		aaThreeToOneLetter.put("Glu","E");
		aaThreeToOneLetter.put("Phe","F");
		aaThreeToOneLetter.put("Gly","G");
		aaThreeToOneLetter.put("His","H");
		aaThreeToOneLetter.put("Ile","I");
		aaThreeToOneLetter.put("Lys","K");
		aaThreeToOneLetter.put("Leu","L");
		aaThreeToOneLetter.put("Met","M");
		aaThreeToOneLetter.put("Asn","N");
		aaThreeToOneLetter.put("Pro","P");
		aaThreeToOneLetter.put("Gln","Q");
		aaThreeToOneLetter.put("Arg","R");
		aaThreeToOneLetter.put("Ser","S");
		aaThreeToOneLetter.put("Thr","T");
		aaThreeToOneLetter.put("Val","V");
		aaThreeToOneLetter.put("Trp","W");
		aaThreeToOneLetter.put("Tyr","Y");
		
		aaOneToThreeLetter = aaThreeToOneLetter
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
				e -> e.getValue().charAt(0),
				e -> e.getKey()));
		
		codonToAminoAcidMap = new HashMap<String, String>();
		codonToAminoAcidMap.put("TTT","F");
		codonToAminoAcidMap.put("TTC","F");
		codonToAminoAcidMap.put("TTA","L");
		codonToAminoAcidMap.put("TTG","L");

		codonToAminoAcidMap.put("CTT","L");
		codonToAminoAcidMap.put("CTC","L");
		codonToAminoAcidMap.put("CTA","L");
		codonToAminoAcidMap.put("CTG","L");

		codonToAminoAcidMap.put("ATT","I");
		codonToAminoAcidMap.put("ATC","I");
		codonToAminoAcidMap.put("ATA","I");
		codonToAminoAcidMap.put("ATG","M");

		codonToAminoAcidMap.put("GTT","V");
		codonToAminoAcidMap.put("GTC","V");
		codonToAminoAcidMap.put("GTA","V");
		codonToAminoAcidMap.put("GTG","V");

		codonToAminoAcidMap.put("TCT","S");
		codonToAminoAcidMap.put("TCC","S");
		codonToAminoAcidMap.put("TCA","S");
		codonToAminoAcidMap.put("TCG","S");

		codonToAminoAcidMap.put("CCT","P");
		codonToAminoAcidMap.put("CCC","P");
		codonToAminoAcidMap.put("CCA","P");
		codonToAminoAcidMap.put("CCG","P");

		codonToAminoAcidMap.put("ACT","T");
		codonToAminoAcidMap.put("ACC","T");
		codonToAminoAcidMap.put("ACA","T");
		codonToAminoAcidMap.put("ACG","T");

		codonToAminoAcidMap.put("GCT","A");
		codonToAminoAcidMap.put("GCC","A");
		codonToAminoAcidMap.put("GCA","A");
		codonToAminoAcidMap.put("GCG","A");

		codonToAminoAcidMap.put("TAT","Y");
		codonToAminoAcidMap.put("TAC","Y");
		codonToAminoAcidMap.put("TAA","*");//stop
		codonToAminoAcidMap.put("TAG","*");//stop

		codonToAminoAcidMap.put("CAT","H");
		codonToAminoAcidMap.put("CAC","H");
		codonToAminoAcidMap.put("CAA","Q");
		codonToAminoAcidMap.put("CAG","Q");

		codonToAminoAcidMap.put("AAT","N");
		codonToAminoAcidMap.put("AAC","N");
		codonToAminoAcidMap.put("AAA","K");
		codonToAminoAcidMap.put("AAG","K");

		codonToAminoAcidMap.put("GAT","D");
		codonToAminoAcidMap.put("GAC","D");
		codonToAminoAcidMap.put("GAA","E");
		codonToAminoAcidMap.put("GAG","E");

		codonToAminoAcidMap.put("TGT","C");
		codonToAminoAcidMap.put("TGC","C");
		codonToAminoAcidMap.put("TGA","*");//stop
		codonToAminoAcidMap.put("TGG","W");

		codonToAminoAcidMap.put("CGT","R");
		codonToAminoAcidMap.put("CGC","R");
		codonToAminoAcidMap.put("CGA","R");
		codonToAminoAcidMap.put("CGG","R");

		codonToAminoAcidMap.put("AGT","S");
		codonToAminoAcidMap.put("AGC","S");
		codonToAminoAcidMap.put("AGA","R");
		codonToAminoAcidMap.put("AGG","R");

		codonToAminoAcidMap.put("GGT","G");
		codonToAminoAcidMap.put("GGC","G");
		codonToAminoAcidMap.put("GGA","G");
		codonToAminoAcidMap.put("GGG","G");
		
		aminoAcidToCodonsMap = new HashMap<String, List<String>>();

		for (String aa: aaThreeToOneLetter.values()) {
			aminoAcidToCodonsMap.put(aa, new ArrayList<String>());
		}

		// flip the key/value of codonToAminoAcidMap
		for (Map.Entry<String, String> entry: codonToAminoAcidMap.entrySet()) {
			String aa = entry.getValue();
			if (aa.equals("*")) {
				continue;
			}
			aminoAcidToCodonsMap.get(aa).add(entry.getKey());
		}

		generateTable();
	}

	/*
	 * Translates a triplet into its corresponding amino acid or amino acids (if the triplet encodes > 1 amino acid)
	 * The amino acid "X" is returned if the codon encodes > 4 (DEFAULT_AAS_LIMIT) amino acids or
	 * if there is no entry for the codon in the tripletsTable (occurs when triplet contains one or two '-'
	 * consistent with an edited frame-shift deletion).
	 * @param nas nucleotide triplet
	 * @return AA amino acid translation
	 */
	public static String translateNATriplet(String nas) {
		if (nas.length() != 3) {
			// System.err.println("WARNING: Must be a string of 3 nas!");
			return "X";
		}
		String aa = getAAsForTriplet(nas);
		if (aa == null) {
			aa = "X";
		}
		return aa;
	}

	public static String translateToTripletAA(String aas) {
		if (aas == null || aas.isEmpty()) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (Character aa : aas.toCharArray()) {
			result.append(aaOneToThreeLetter.getOrDefault(aa, "Xxx"));
		}
		return result.toString();
	}

	/**
	 * Take triplet NA and triplet AA then generate the mutation control string.
	 *
	 * For example: given nas="TTT" and aas="Asn"; the generated result is
	 * "  ." since the shortest path from TTT to Asn is via AAT.
	 *
	 * This method assumes that nas and aas are always valid (length%3=0).
	 *
	 * @param allNAs
	 * @param allAAs
	 * @return Control string
	 */
	public static String generateControlString(String allNAs, String allAAs) {
		Iterable<String> nasList = Splitter.fixedLength(3).split(allNAs);
		Iterable<String> aasList = Splitter.fixedLength(3).split(allAAs);
		return StreamUtils.zip(
			StreamSupport.stream(nasList.spliterator(), false),
			StreamSupport.stream(aasList.spliterator(), false),
			(nas, aas) -> {
				if (nas.isEmpty() || aas.isEmpty()) {
					return "";
				}
				return generateTripletControl(nas, aas);
			})
		.collect(Collectors.joining());
	}

	protected static String generateTripletControl(String nas, String aas) {	
		aas = aaThreeToOneLetter.get(aas);
		int maxMatched = -1;
		String maxMatchedControlString = "   ";
		for (String cmp: aminoAcidToCodonsMap.get(aas)) {
			int matched = 0;
			String controlStr = "";
			for(int i = 0; i < 3; i++) {
				Character na1 = null, na2 = null;
				na1 = nas.charAt(i);
				na2 = cmp.charAt(i);
				if (na1 == na2) {
					controlStr += ".";
					matched ++;
				}
				else {
					controlStr += " ";
				}
			}
			if (matched == 3) {
				maxMatchedControlString = ":::";
				break;
			}
			if (matched > maxMatched) {
				maxMatched = matched;
				maxMatchedControlString = controlStr;
			}
		}
		return maxMatchedControlString;
	}
	
//	public static int getMinimalNAChanges(String codon, String aa) {
//		int maxMatched = -1;
//		for (String cmp: aminoAcidToCodonsMap.get(aa)) {
//			System.out.println("cmp: " + cmp);
//			int matched = 0;
//			for(int i = 0; i < 3; i++) {
//				Character na1 = null, na2 = null;
//				if (codon.length() > i) {
//					na1 = codon.charAt(i);
//				}
//				if (cmp.length() > i) {
//					na2 = cmp.charAt(i);
//				}
//				if (na1 == na2) {
//					matched ++;
//				}
//			}
//			if (matched == 3) {
//				break;
//			}
//			if (matched > maxMatched) {
//				maxMatched = matched;
//			}
//		}
//		
//		return 3 - maxMatched;
//	
//	}

	/**
	 * Translates a string of nucleotides into a string of amino acids. Nucleotide triplets that
	 * encode more than one amino acid are translated to an "X"
	 * TODO: Should this throw an exception?
	 * @param nas
	 * @return aas
	 */
	public static String simpleTranslate(String nas) {
		return simpleTranslate(nas, null, null);
	}
	
	/**
	 * Translate a string of nucleotides into a string of amino acids. Nucleotide triplets that
	 * encode more than one amino acid are translated to an "X". If `consAAs` is specified, the
	 * wild type amino acid will be removed if the position has two or more amino acids.
	 * @param nas
	 * @param firstAA
	 * @param consAAs
	 * @return string
	 */
	public static String simpleTranslate(String nas, Integer firstAA, String consAAs) {
		StringBuilder allAAs = new StringBuilder();
		String aas;
		int extraNAs = nas.length() % 3;
		if (extraNAs != 0) {
			System.err.println("In CodonTranslation: nas is not a multiple of 3:");
			System.err.println("In CodonTranslation: nas.length():" + nas.length());
			System.err.println("In CodonTranslation: nas:" + nas);
			nas = nas.substring(0, nas.length()- extraNAs);
			System.err.println("In CodonTranslation: shortened nas:" + nas + "\n");
		}
		int lenAAs = nas.length() / 3;
		for (int pos = 0; pos < lenAAs; pos ++) {
			aas = translateNATriplet(nas.substring(pos * 3, pos * 3 + 3));
			if (aas.length() > 1 && consAAs != null) {
				String ref = consAAs.substring(firstAA + pos - 1, firstAA + pos);
				aas = aas.replaceAll(ref, "");
			}
			if (aas.length() > 1) {
				allAAs.append("X");
			}
			else {
				allAAs.append(aas);
			}
		}
		return allAAs.toString();
	}

	private static String getAAsForTriplet (String triplet) {
		return tripletsTable.get(triplet);
	}

	private static void generateTable () {
		tripletsTable = new HashMap<String, String>();

		for (int i=0; i<nas.length; i++) {
			for (int j=0; j<nas.length; j++) {
				for (int k= 0; k<nas.length; k++) {
					String triplet = nas[i] + nas[j] + nas[k];
					List<String> codons = enumerateCodonPossibilities(triplet);
					HashSet<String> uniqueAAs = new HashSet<String>();
					for (String codon : codons) {
						uniqueAAs.add(codonToAminoAcidMap.get(codon));
					}
					String aas = "";
					for (String uniqueAA : uniqueAAs) {
						aas += uniqueAA;
					}
					tripletsTable.put(triplet, aas);
				}
			}
		}
	}

	private static List<String> enumerateCodonPossibilities (String triplet) {
		List<String> codonPossibilities = new ArrayList<String>();
		String pos1 = triplet.substring(0,1);
		String pos2 = triplet.substring(1,2);
		String pos3 = triplet.substring(2);
		for (String p1 : ambiguityMapping.get(pos1)) {
			for (String p2 : ambiguityMapping.get(pos2)) {
				for (String p3 : ambiguityMapping.get(pos3)) {
					codonPossibilities.add(p1+p2+p3);
				}
			}
		}
		return codonPossibilities;
	}
	
	/** Returns merged codon may or may not contained IUPAC ambiguity codes.
	 * 
	 * @param codons a collection of codons; should be sorted by percent in descending order 
	 * @return String merged codon
	 */
	public static String getMergedCodon(Collection<String> codons) {
		List<Set<Character>> allBPs = new ArrayList<>();
		allBPs.add(new TreeSet<>());
		allBPs.add(new TreeSet<>());
		allBPs.add(new TreeSet<>());
		for (String codon : codons) {
			for (int i=0; i<3; i++) {
				Set<Character> bps = allBPs.get(i);
				char bp = codon.charAt(i);
				// only allows '-' if bps is empty
				if (bp == '-' && !bps.isEmpty()) {
					continue;
				}
				bps.add(bp);
			}
		}
		StringBuilder resultCodon = new StringBuilder();
		for (Set<Character> bps : allBPs) {
			if (bps.contains('-')) {
				// '-' override any NA codes
				resultCodon.append('-');
			} else {
				resultCodon.append(ambiguityInverseMapping.get(StringUtils.join(bps, "")));
			}
		}
		return resultCodon.toString();
	}
}
