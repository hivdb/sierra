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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: all static method should be move to class Sequence


public class SeqUtils {

	private static final Set<Integer> RYMWKS_CODES;
	private static final Set<Integer> BDHVN_CODES;

	static {
		RYMWKS_CODES = Collections.unmodifiableSet(
			"RYMWKS".chars().boxed().collect(Collectors.toSet()));
		BDHVN_CODES = Collections.unmodifiableSet(
			"BDHVN".chars().boxed().collect(Collectors.toSet()));
	}

	public SeqUtils() {
		throw new RuntimeException("Class SeqUtils shouldn't be initialized.");
	}

	/**
	 * Calculate the percentage for appearances of RYMWKS.
	 * Notes we don't include BDHVN here since they are unusual
	 * and normally indicate sequencing quality problems.
	 *
	 * @param nucleotideSeq
	 * @return double
	 */
	public static double mixturePcnt(String nucleotideSeq) {
		if (nucleotideSeq.isEmpty()) {
			return 0.0;
		}
		return 100.0 * (double) numRYMWKS(nucleotideSeq) / (double) nucleotideSeq.length();
	}

	public static int numBDHVN(String nucleotideSeq) {
		return (int) nucleotideSeq
			.chars()
			.filter(code -> BDHVN_CODES.contains(code))
			.count();
	}

	public static int numRYMWKS(String nucleotideSeq) {
		return (int) nucleotideSeq
			.chars()
			.filter(code -> RYMWKS_CODES.contains(code))
			.count();
	}

	public static String replaceCodon(String string, int pos, String replacementText) {
		return new StringBuilder(string)
			.replace(pos, pos + 3, replacementText).toString();
	}

	public static String trimDownstreamNAs (String seq) {
		int bleed = seq.length() % 3;
		if (bleed > 0) {
			seq = seq.substring(0, seq.length() - bleed);
		}
		return seq;
	}

	// Trims nucleotides that are not multiples of 3
	public static String trimUpstreamNAs (String seq) {
		int bleed = seq.length() % 3;
		if (bleed > 0) {
			seq = seq.substring(bleed);
		}
		return seq;
	}

}
