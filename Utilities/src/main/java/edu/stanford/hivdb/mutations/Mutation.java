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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.stanford.hivdb.aapcnt.HIVAminoAcidPercents;
import edu.stanford.hivdb.utilities.MyStringUtils;

public class Mutation implements Comparable<Mutation> {
	
	private static Pattern mutationPattern = Pattern.compile(
		"^\\s*" +
		"((?i:PR|RT|IN))?[:_-]?" +
		"([AC-IK-NP-TV-Y])?" + 
		"(\\d{1,3})" +
		"([AC-IK-NP-TV-Z.*]+(?:[#_]?[AC-IK-NP-TV-Z.*]+)?|[id_#~-]|[iI]ns(?:ertion)?|[dD]el(?:etion)?)" +
		"(?::([ACGTRYMWSKBDHVN-]{3})?)?" +
		"\\s*$");
	private static final HIVAminoAcidPercents allAAPcnts = HIVAminoAcidPercents.getInstance("all", "All");

	private Gene gene;
	private String cons;
	private int pos;
	private String aas;
	private String triplet;
	private String insertedNAs;
	private boolean isInsertion = false;
	private boolean isDeletion = false;
	private transient List<MutType> types;
	private transient Boolean isAtDrugResistancePosition;
	
	public static class InvalidMutationStringException extends RuntimeException {
		private static final long serialVersionUID = 4271016133470715497L;

		public InvalidMutationStringException(String message, Exception e) {
			super(message, e);
		}
		
		public InvalidMutationStringException(String message) {
			super(message);
		}
	}

	/**
	 *
	 * @param gene
	 * @param pos
	 * @param aas
	 * @param triplet
	 * @param insertedNAs
	 */
	public Mutation(Gene gene, int pos, String aas, String triplet, String insertedNAs) {		
		if (pos > gene.getLength()) {
			throw new IllegalArgumentException("Length is out of bounds for this gene.");
		}
		
		this.gene = gene;
		this.cons = this.gene.getConsensus(pos);
		this.pos = pos;
		this.aas = normalizeAAs(aas);
		this.triplet = triplet.toUpperCase();
		this.insertedNAs = insertedNAs;
		this.isInsertion = this.aas.contains("_");
		this.isDeletion = this.aas.equals("-");
	}

	public Mutation(Gene gene, int pos, String aas, String triplet) {
		this(gene, pos, aas, triplet, "");
	}

	public Mutation(Gene gene, int pos, String aas) {
		this(gene, pos, aas, "", "");
	}

	public Mutation(Gene gene, int pos, Character aa) {
		this(gene, pos, "" + aa, "", "");
	}
	
	// Potential Issue:
	// Doesn't support sole insertions such as "_N"
	// See commented-out conditional for illustration
	public static Mutation fromNucAminoMutation(Gene gene, int aaStart, Map<?, ?> mut) {
		int pos = ((Double) mut.get("Position")).intValue() - aaStart + 1;
		
		String codon = "";
		String insertedCodon = "";
		boolean isInsertion = (Boolean) mut.get("IsInsertion");
		boolean isDeletion = (Boolean) mut.get("IsDeletion");
		
		StringBuilder aas = new StringBuilder();
		if (isDeletion) {
			aas.append('-');
		}
		else {
			codon = (String) mut.get("CodonText");
			codon = codon.replace(' ', '-');
			/*if (codon.length() > 0)*/ aas.append(CodonTranslation.translateNATriplet(codon));
			if (isInsertion) {       
				aas.append('_');
				insertedCodon = (String) mut.get("InsertedCodonsText");
				aas.append(CodonTranslation.simpleTranslate(insertedCodon));
			}
		}
		return new Mutation(gene, pos, aas.toString(), codon, insertedCodon);
	}

	/**
	 * Normalize the input AAs.
	 *
	 * The code explains the normalization rules.
	 */
	public static String normalizeAAs(String aas) {
		if (aas == null) return null;	
		
		aas = aas.replaceAll("^[dD]elet(e|ion)|d(el)?|~$", "-")
			     .replaceAll("^[iI]nsert(ion)?|i(ns)?$|#", "_")
			     .replaceAll("[.Z]", "*");
		
		if (aas.length() > 1 && !aas.contains("_")) {
			return MyStringUtils.sortAlphabetically(aas).toUpperCase();
		}
		
		return aas.toUpperCase();
	}
		
	public Set<Mutation> split() {
		Set<Mutation> r = new HashSet<>();
		if (isInsertion()) {
			// prevent side-effect caused by insertion AAs
			r.add(new Mutation(gene, pos, "_"));
		}
		else {
			for (char aa : aas.toCharArray()) {
				r.add(new Mutation(gene, pos, aa));
			}
		}
		return r;
	}

	/**
	 * Merges with another Mutation object and returns the merged mutation.
	 *
	 * @param another
	 * @return A new merged Mutation object
	 * @throws IllegalArgumentException, UnsupportedOperationException
	 */
	public Mutation mergesWith(Mutation another) {
		if (gene != another.getGene() || pos != another.getPosition()) {
			throw new IllegalArgumentException(String.format(
				"The other mutation must be at this position: %d (%s)",
				pos, gene.toString()));
		}
		if (isIndel() || another.isIndel()) {
			throw new UnsupportedOperationException(String.format(
				"Can not merge indel mutations (%s with %s)",
				toString(), another.toString()));
		}
		StringBuilder newAAs = new StringBuilder();
		String anotherAAs = another.getAAs();
		newAAs.append(aas);
		for (char newAA : anotherAAs.toCharArray()) {
			if (aas.indexOf(newAA) == -1) {
				newAAs.append(newAA);
			}
		}

		return new Mutation(gene, pos, newAAs.toString());
	}

	/**
	 * Substracts by another Mutation object and returns the result mutation.
	 *
	 * @param another
	 * @return A new Mutation object
	 */
	public Mutation subtractsBy(Mutation another) {
		if (another == null ||
				gene != another.getGene() ||
				pos != another.getPosition()) {
			return new Mutation(gene, pos, aas);
		}
		StringBuilder newAAs = new StringBuilder();
		String anotherAAs = another.aas;
		for (char newAA : aas.toCharArray()) {
			if (anotherAAs.indexOf(newAA) == -1) {
				newAAs.append(newAA);
			}
		}

		if (newAAs.length() == 0) {
			return null;
		}
		
		return new Mutation(gene, pos, newAAs.toString());
	}

	/**
	 * Intersects with another Mutation object and returns the result mutation.
	 *
	 * @param another
	 * @return A new merged Mutation object
	 * @throws IllegalArgumentException
	 */
	public Mutation intersectsWith(Mutation another) {
		if (gene != another.getGene() || pos != another.getPosition()) {
			throw new IllegalArgumentException(String.format(
				"The other mutation must be at this position: %d (%s)",
				pos, gene.toString()));
		}
		return unsafeIntersectsWith(another);
	}

	/**
	 * Similar to intersectsWith but spare all assertions.
	 * Maybe useful when those assertions are not necessary.
	 */
	protected Mutation unsafeIntersectsWith(Mutation... others) {
		StringBuilder newAAs = new StringBuilder();
		for (Mutation another : others) {
			String anotherAAs = another.aas;
			for (char newAA : anotherAAs.toCharArray()) {
				if (aas.indexOf(newAA) > -1) {
					newAAs.append(newAA);
				}
			}
		}

		if (newAAs.length() == 0) {
			return null;
		}

		return new Mutation(gene, pos, newAAs.toString());
	}

	public boolean isAtDrugResistancePosition() {
		if (isAtDrugResistancePosition == null) {
			isAtDrugResistancePosition =
				MutationTypePairs.lookupByPosition(gene, pos)
				.parallelStream().anyMatch(mt -> mt.isDRMType());
		}
		return isAtDrugResistancePosition;
	}

	public boolean isUnsequenced() {
		// "NNN", "NN-", "NNG" should be considered as unsequenced region
		return !isInsertion &&
			StringUtils.countMatches(triplet.replace('-', 'N'),	"N") > 1;
	}
	
	/**
	 * Extracts gene from mutText string
	 * @param mutText
	 * @return a Gene enum object
	 */
	public static Gene extractGene(String mutText) {
		Gene gene = null;
		Matcher m = mutationPattern.matcher(mutText);
		if (m.matches()) {
			try {
				gene = Gene.valueOf(m.group(1).toUpperCase());
			} catch (NullPointerException e) {
				throw new InvalidMutationStringException(
					"Gene is not specified and also not found in the " +
					"given text: " + mutText + ". The correct format " +
					"for an input mutation string is, for example, " +
					"RT:215Y.", e);
			}
		}
		return gene;
	}
	
	/**
	 * Converts gene and mutText string into a Mutation object
	 * mutText may or may not have a preceding consensus
	 * @param gene, mutText
	 * @return a Mutation object
	 */
	public static Mutation parseString(Gene gene, String mutText) {
		Matcher m = mutationPattern.matcher(mutText);
		Mutation mut = null;
		if (m.matches()) {
			if (gene == null) {
				try {
					gene = Gene.valueOf(m.group(1).toUpperCase());
				} catch (NullPointerException e) {
					throw new InvalidMutationStringException(
						"Gene is not specified and also not found in the " +
						"given text: " + mutText + ". The correct format " +
						"for an input mutation string is, for example, " +
						"RT:215Y.", e);
				}
			}	
			int pos = Integer.parseInt(m.group(3));
			String aas = normalizeAAs(m.group(4)); 
			String triplet = m.group(5);
			if (triplet == null) triplet = "";
			mut = new Mutation(gene, pos, aas, triplet);
		} else {
			throw new InvalidMutationStringException(
				"Tried to parse mutation string using invalid parameters: " + mutText);
		}
		return mut;
	}
	
	public static Mutation parseString(String mutText) {
		return parseString(null, mutText);
	}

	public Gene getGene() { return gene; }
	public String getConsensus() { return cons; }
	public int getPosition() {return pos; }
	public GenePosition getGenePosition() {return new GenePosition(gene, pos);}
	public String getAAs() { return aas; }
	public String getTriplet() { return triplet; }
	public String getInsertedNAs() { return insertedNAs; }
	public boolean isInsertion() { return isInsertion; }
	public boolean isDeletion() { return isDeletion; }
	public boolean isIndel() { return isInsertion || isDeletion; }
	public boolean isMixture() { return (aas.equals("X") || aas.length()>1); }
	public boolean hasConsensus () { return aas.split("_", 2)[0].contains(gene.getConsensus(pos));}
	public boolean hasStop() { return getAAs().contains("*"); }

	public boolean isUnusual() {
		String mixture = aas;
		if (isInsertion) {
			mixture = "_";
		}
		else if (isDeletion) {
			mixture = "-";
		}
		if (mixture.contains("X")) {
			return true;
		}
		return allAAPcnts.containsUnusualAA(gene, pos, mixture);
	}

	public boolean isSDRM() { return Sdrms.isSDRM(this); }
	public boolean hasBDHVN() {
		// TODO: what if BDHVN doesn't affect the amimo acid?
		return triplet.contains("B") || triplet.contains("D") || triplet.contains("H") ||
			triplet.contains("V") || triplet.contains("N");
	}
	public boolean isAmbiguous() {
		return hasBDHVN() || aas.equals("X");
	}
	public boolean isApobecMutation() {
		return Apobec.isApobecMutation(this);
	}
	public boolean isApobecDRM() {
		return Apobec.isApobecDRM(this);
	}
	public double getHighestMutPrevalence() {
		String mixture = aas;
		if (isInsertion) {
			mixture = "_";
		}
		else if (isDeletion) {
			mixture = "-";
		}
		mixture = mixture.replace(cons, "").replaceAll("X", "");
		
		return allAAPcnts.getHighestAAPercentValue(gene, pos, mixture) * 100;
	}

	public String getAAsWithConsFirst() {
		String aas = getAAs();
		String cons = getConsensus();
		if (aas.contains(cons)) {
			return cons + aas.replaceAll(cons,"");
		} else {
			return aas;
		}
	}

	/**
	 * Retrieve the primary mutation type of current mutation.
	 */
	public MutType getPrimaryType() {
		return getTypes().get(0);
	}
	
	/**
	 * Retrieve all mutation types of current mutation.
	 *
	 * The reason we return a List here is because some mixture,
	 * for example, RT215SWY has multiple types.
	 */
	public List<MutType> getTypes() {
		if (types == null) {
			List<MutType> r = MutationTypePairs.lookupByMutation(this);
			if (r.isEmpty()) {
				r.add(MutType.Other);
			}
			types = Collections.unmodifiableList(r);
		}
		return types;
	}

	/*public List<MutationComment> getComments() {
		return MutationComments.lookupByMutation(this);
	}*/

	public String getAAsWithoutConsensus () {
		String aas = getAAs();
		String cons = getConsensus();
		if (aas.contains(cons)) {
			aas = StringUtils.replace(aas, cons, "");
		}
		return aas;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) { return true; }
		if (o == null) { return false; }
		if (!(o instanceof Mutation)) { return false;}
		Mutation m = (Mutation) o;

		// isDeletion and isInsertion is related to aas
		return new EqualsBuilder()
			.append(gene, m.gene)
			.append(pos, m.pos)
			.append(aas, m.aas)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(4541, 83345463)
			.append(gene)
			.append(pos)
			.append(aas)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return getHumanFormat();
	}

	/**
	 * This method is just an alias name of getShortHumanFormat
	 * since in GraphQL the same thing is named as "shortText".
	 *
	 * @return String
	 */
	public String getShortText() {
		return getShortHumanFormat();
	}

	/**
	 * Sort by gene, position and aas.
	 *
	 * The order implemented by this method is similar
	 * to the order implemented by this SQL query:
	 *   SELECT * FROM mutations ORDER BY gene, pos, aas;
	 *
	 */
	@Override
	public int compareTo (Mutation mut) {
		int cmp = gene.compareTo(mut.getGene());
		if (cmp == 0) {
			cmp = new Integer(pos).compareTo(mut.getPosition());
		}
		if (cmp == 0) {
			cmp = aas.compareTo(mut.getAAs());
		}
		return cmp;
	}

	/**
	 * Compares two mutations to determine if they share a nonconsensus amino acid
	 * TODO: Need to enforce the manner in which insertions and deletions are submitted. For now
	 * all insertions should be '_' and all deletions should be '-'. However, each has several synonyms
	 * TODO: can probably be replaced by comparing the sorted aas
	 * @param queryMut
	 * @return true if the Mutation and queryMut share at least one nonconsensus amino acid
	 */
	public boolean containsSharedAA (Mutation queryMut) {
		if (this.gene.equals(queryMut.gene) && this.pos == queryMut.pos) {
			return containsSharedAA(queryMut.getAAsWithoutConsensus());
		}
		return false;
	}

	public boolean containsSharedAA(String queryAAs) {
		// Replace consensus and stop codons so that they are not responsible for a match
		String myMutAAs = getAAsWithoutConsensus();
		myMutAAs = StringUtils.replace(myMutAAs, "*", "");
		String queryMutAAs = queryAAs;
		for (int i=0; i<myMutAAs.length(); i++) {
			for (int j=0; j<queryMutAAs.length(); j++) {
			if (myMutAAs.substring(i, i+1).equals(queryMutAAs.substring(j, j+1))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * The ASI format consists of an optional consensus aa and a position followed by one or more
	 * upper case amino acids. Insertions are represented by 'i', deletions by 'd', and stops by 'Z;
	 */
	public String getASIFormat() {
		String fmtAAs;
		if (isInsertion) {
			fmtAAs = "i";
		} else if (isDeletion) {
			fmtAAs = "d";
		} else {
			fmtAAs = aas.replaceAll("\\*","Z");
			fmtAAs = fmtAAs.replaceAll("X","Z");
		}
		return gene.getConsensus(pos) + pos + fmtAAs;
	}

	/**
	 * In HIVDB_Rules, insertions are denoted by '#' and deletions by '~'
	 * This differs from Insertion, _, and i and from Deletion, '-', and d
	 * @returns HIVDBformat
	 */
	public String getHIVDBFormat() {
		String fmtAAs = aas;
		if (isInsertion) {
			fmtAAs = "#";
		} else if (isDeletion) {
			fmtAAs = "~";
		}
		return pos + fmtAAs;
	}

	/**
	 * If the insertion is known, report it out in the following format T69S_SS
	 * If the insertion is not known report it out as T69Insertion
	 * Report deletions as T69Deletion
	 * If there is a mixture that contains the consensus aa, move the cons to
	 * the beginning of the mixture.
	 * Report the consensus before the position (i.e. M184V)
	 */
	public String getHumanFormat() {
		String fmtAAs;
		String cons = getConsensus();
		if (isInsertion) {
			if (aas.equals("_")) {
				fmtAAs = "Insertion";
			} else {
				fmtAAs = aas;
			}
		} else if (isDeletion) {
			fmtAAs = "Deletion";
		} else {
			if (aas.contains(cons)) {
				fmtAAs = cons + aas.replaceAll(cons,"");
			} else {
				fmtAAs = aas;
			}
		}
		return cons + pos + fmtAAs;
	}

	public String getShortHumanFormat() {
		String fmtAAs;
		String cons = getConsensus();
		if (isInsertion) {
			if (aas.equals("_")) {
				fmtAAs = "i";
			} else {
				fmtAAs = aas;
			}
		} else if (isDeletion) {
			fmtAAs = "d";
		} else {
			fmtAAs = getAAsWithConsFirst();
		}
		return cons + pos + fmtAAs;
	}

	/**
	 * Similar to getHumanFormat() except that the preceding consensus is removed.
	 */
	public String getHumanFormatWithoutCons() {
		return (getHumanFormat().substring(1));
	}

	public String getHumanFormatWithGene() {
		return gene.toString() + "_" + getHumanFormat();
	}
}