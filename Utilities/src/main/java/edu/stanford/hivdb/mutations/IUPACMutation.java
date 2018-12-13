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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.utilities.MyStringUtils;

public class IUPACMutation extends AAMutation {

	private static Pattern mutationPattern = Pattern.compile(
		"^\\s*" +
		"((?i:PR|RT|IN))?[:_-]?" +
		"([AC-IK-NP-TV-Y])?" +
		"(\\d{1,3})" +
		"([AC-IK-NP-TV-Z.*]+(?:[#_]?[AC-IK-NP-TV-Z.*]+)?|[id_#~-]|[iI]ns(?:ertion)?|[dD]el(?:etion)?)" +
		"(?::([ACGTRYMWSKBDHVN-]{3})?)?" +
		"\\s*$");

	private final String aas;
	private final String triplet;
	private final String insertedNAs;

	private static char[] calcAACharArray(String aas) {
		aas = normalizeAAs(aas);
		if (aas.contains("_")) {
			aas = "_";
		}
		return aas.toCharArray();
	}

	public static Pattern getPattern() {
		return mutationPattern;
	}

	public static IUPACMutation fromNucAminoMutation(Gene gene, int aaStart, Map<?, ?> mut) {
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
			// The length of `CodonText` from NucAmino always equals to 3
			aas.append(CodonTranslation.translateNATriplet(codon));
			if (isInsertion) {
				aas.append('_');
				insertedCodon = (String) mut.get("InsertedCodonsText");
				aas.append(CodonTranslation.simpleTranslate(insertedCodon));
			}
		}
		return new IUPACMutation(gene, pos, aas.toString(), codon, insertedCodon);
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
				throw new InvalidMutationException(
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
	public static IUPACMutation parseString(Gene gene, String mutText) {
		Matcher m = mutationPattern.matcher(mutText);
		IUPACMutation mut = null;
		if (m.matches()) {
			if (gene == null) {
				try {
					gene = Gene.valueOf(m.group(1).toUpperCase());
				} catch (NullPointerException e) {
					throw new InvalidMutationException(
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
			mut = new IUPACMutation(gene, pos, aas, triplet, "", 0xff);
		} else {
			throw new InvalidMutationException(
				"Tried to parse mutation string using invalid parameters: " + mutText);
		}
		return mut;
	}

	public static IUPACMutation parseString(String mutText) {
		return parseString(null, mutText);
	}

	/**
	 *
	 * @param gene
	 * @param pos
	 * @param aas
	 * @param triplet
	 * @param insertedNAs
	 * @param maxDisplayAAs
	 */
	public IUPACMutation(
		Gene gene, int position, String aas,
		String triplet, String insertedNAs,
		int maxDisplayAAs
	) {
		super(gene, position, calcAACharArray(aas), maxDisplayAAs);
		this.aas = normalizeAAs(aas);
		this.triplet = triplet.toUpperCase();
		this.insertedNAs = insertedNAs;
	}


	/**
	 *
	 * @param gene
	 * @param pos
	 * @param aas
	 * @param triplet
	 * @param insertedNAs
	 */
	public IUPACMutation(
		Gene gene, int position, String aas,
		String triplet, String insertedNAs
	) {
		this(
			gene, position, aas, triplet, insertedNAs,
			AAMutation.DEFAULT_MAX_DISPLAY_AAS
		);
	}

	public IUPACMutation(Gene gene, int position, String aas, String triplet) {
		this(gene, position, aas, triplet, "");
	}

	public IUPACMutation(Gene gene, int position, String aas) {
		this(gene, position, aas, "", "");
	}

	public IUPACMutation(Gene gene, int position, Character aa) {
		this(gene, position, "" + aa, "", "");
	}

	@Override
	@Deprecated
	public Mutation mergesWith(Mutation another) {
		if (isIndel() || another.isIndel()) {
			throw new UnsupportedOperationException(String.format(
				"Can not merge indel mutations (%s with %s)",
				toString(), another.toString()));
		}
		return super.mergesWith(another);
	}

	@Override
	public boolean isUnsequenced() {
		// "NNN", "NN-", "NNG" should be considered as unsequenced region
		return !isInsertion() &&
			StringUtils.countMatches(triplet.replace('-', 'N'), "N") > 1;
	}

	@Override
	public String getDisplayAAs() {
		String[] splited = aas.split("_", 2);
		if (splited[0].length() > getMaxDisplayAAs()) {
			splited[0] = "X";
		}
		return StringUtils.join(splited, '_');
	}

	@Override
	public String getAAs() { return aas; }

	@Override
	public String getTriplet() { return triplet; }

	@Override
	public String getInsertedNAs() { return insertedNAs; }

	@Override
	public boolean hasBDHVN() {
		// TODO: what if BDHVN doesn't affect the amimo acid?
		return triplet.matches(".*[BDHVN].*");
	}

	@Override
	public String getAAsWithRefFirst() {
		String[] aas = getDisplayAAs().split("_", 2);
		String ref = getReference();

		if (aas[0].contains(ref)) {
			aas[0] = ref + aas[0].replaceAll(ref, "");
		}
		return String.join("_", aas);
	}

	@Override
	public String getAAsWithoutReference () {
		String[] aas = getDisplayAAs().split("_", 2);
		String ref = getReference();
		aas[0] = aas[0].replace(ref, "");
		return String.join("_", aas);
	}

}
