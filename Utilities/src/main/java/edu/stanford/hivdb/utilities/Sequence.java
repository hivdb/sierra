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

package edu.stanford.hivdb.utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import net.sf.jfasta.FASTAElement;

public class Sequence {
	private static Map<Character, Character> COMPLEMENT_CODES;

	static {
		Map<Character, Character> complement_codes = new HashMap<>();
		complement_codes.put('A', 'T');
		complement_codes.put('C', 'G');
		complement_codes.put('G', 'C');
		complement_codes.put('T', 'A');
		// complement_codes.put('W', 'W');
		// complement_codes.put('S', 'S');
		complement_codes.put('M', 'K');
		complement_codes.put('K', 'M');
		complement_codes.put('R', 'Y');
		complement_codes.put('Y', 'R');
		complement_codes.put('B', 'V');
		complement_codes.put('D', 'H');
		complement_codes.put('H', 'D');
		complement_codes.put('V', 'B');
		// complement_codes.put('N', 'N');
		COMPLEMENT_CODES = Collections.unmodifiableMap(complement_codes);
	}

	private String header;
	private String sequence;
	private String removedInvalidChars;

	/**
	 * Initializes a sequence with given header and sequence string.
	 *
	 * @param header
	 * @param sequenceText
	 */
	public Sequence(String header, String sequenceText) {
		this.header = header;
		this.removedInvalidChars = "";
		this.sequence = sanitizeSequence(sequenceText);
	}

	/**
	 * Creates a Sequence object from designated Genbank Accession ID
	 * 
	 * IMPORTANT: This function is only designed for unit tests. Use it in
	 * production will significant reduce the performance and stability.
	 * 
	 * @param accession
	 * @return a Sequence object
	 */
	public static Sequence fromGenbank(String accession) {
		List<String> accessions = Arrays.asList(accession);
		List<Sequence> result = FastaUtils.fetchGenbank(accessions);
		if (result.isEmpty()) return null;
		return result.get(0);
	}

	/**
	 * Initializes a Sequence object from a jFASTA element
	 *
	 * @param el
	 */
	public Sequence(final FASTAElement el) {
		this(el.getHeader(), el.getSequence());
	}

	/**
	 * Sanitizes the sequence string to remove non-IUPAC characters
	 *
	 * @param sequenceText
	 * @return the sanitized sequence string
	 */
	private String sanitizeSequence(String sequenceText) {
		sequenceText = sequenceText.toUpperCase();
		this.removedInvalidChars += sequenceText
			.replaceAll("[ACGTRYMWSKBDHVN]", "");
		return sequenceText
			.replaceAll("[^ACGTRYMWSKBDHVN]", "");
	}

	/**
	 * Gets removed non-IUPAC characters by `sanitizeSequence`
	 * 
	 * @return a set of removed characters
	 */
	public Set<Character> removedInvalidChars() {
		Set<Character> result = new TreeSet<>();
		for (Character c : removedInvalidChars.toCharArray()) {
			result.add(c);
		}
		return result;
	}

	/**
	 * Gets the header name of the sequence.
	 * 
	 * @return String
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Gets the sequence text.
	 *
	 * @return String
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Gets the sequence length/size.
	 * 
	 * @return Integer
	 */
	public Integer getLength() {
		return sequence.length();
	}

	/**
	 * Gets the MD5 hash string of this sequence.
	 *
	 * @return String
	 */
	public String getMD5() {
		return DigestUtils.md5Hex(sequence);
	}

	/**
	 * Gets the SHA512 hash string of this sequence.
	 *
	 * @return String
	 */
	public String getSHA512() {
		return DigestUtils.sha512Hex(sequence);
	}

	/**
	 * Calculates the reverse compliment of current sequence.
	 *
	 * @return Sequence object of the reverse compliment
	 */
	public Sequence reverseCompliment() {
		StringBuilder reversed = new StringBuilder();
		int seqLen = sequence.length();

		for (int i = seqLen - 1; i >= 0; i--) {
			char code = sequence.charAt(i);
			reversed.append(COMPLEMENT_CODES.getOrDefault(code, code));
		}
		return new Sequence(header, reversed.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) { return true; }
		if (o == null) { return false; }
		if (!(o instanceof Sequence)) { return false;}
		Sequence s = (Sequence) o;

		return new EqualsBuilder()
			.append(header, s.header)
			.append(sequence, s.sequence)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(6945227, 231285)
			.append(header)
			.append(sequence)
			.toHashCode();
	}

	@Override
	public String toString() {
		return String.format(">%s\n%s", header, sequence);
	}
}
