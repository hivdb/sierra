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

package edu.stanford.hivdb.alignment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.hivdb.mutations.CodonTranslation;

public class CodonAligner {

	private static final String maybeImproperCodonAlignmentRegex = "(?<=[. ])(-+)(?=[. ])";
	private static Pattern maybeImproperCodonAlignment;
	private String alignedNAs;
	private String controlLine;
	private String aaTripletLine;
	private boolean touched = false;

	static {
		maybeImproperCodonAlignment = Pattern.compile(maybeImproperCodonAlignmentRegex);
	}

	public CodonAligner(
			final String alignedNAs,
			final String controlLine,
			final String aaTripletLine) {
		this.alignedNAs = alignedNAs;
		this.controlLine = controlLine;
		this.aaTripletLine = aaTripletLine;
	}

	public static CodonAligner process(
			String alignedNAs, String controlLine, String aaTripletLine) {
		CodonAligner self = new CodonAligner(alignedNAs, controlLine, aaTripletLine);
		self.fixAll();
		return self;
	}

	public Boolean getTouched() { return touched; }
	public String getAlignedNAs() { return alignedNAs; }
	public String getControlLine() { return controlLine; }
	public String getAATripletLine() { return aaTripletLine; }

	/**
	 * This method fix single gap for either insertion or deletion.
	 *
	 * Generally the processes are similar between these two types.
	 * The difference is, for insertion `aaTripletLine` need to be fixed
	 * but for deletion `alignedNAs` need to be fixed.
	 *
	 * @param gapStart
	 * @param gapEnd
	 * @param shiftStart
	 * @param shiftEnd
	 * @param alignResult
	 * @param isInsertion
	 */
	protected void fixSingleGap(
			int gapStart, int gapEnd, int shiftStart,
			int shiftEnd, boolean isInsertion) {
		String seq;
		String leftCt;   // "[..]---[.]", the dots
		String rightCt;  // "..[---].", the dashes
		String leftSeq;  // "[Gl]   [u]" or "[GA]   [G]",
						 // the splitted triplet or NAs
		String rightSeq; // "Gl[   ]u" or "[GA]   [G], the insertion/deletion
		String control = controlLine;

		if (isInsertion) {
			seq = aaTripletLine;
		}
		else {
			seq = alignedNAs;
		}

		leftSeq =
			seq.substring(shiftStart, gapStart) +
			seq.substring(gapEnd, shiftEnd);
		rightSeq = seq.substring(gapStart, gapEnd);

		leftCt =
			control.substring(shiftStart, gapStart) +
			control.substring(gapEnd, shiftEnd);
		rightCt = control.substring(gapStart, gapEnd);

		String outerAAs = "";
		String outerNAs = "";
		int outerAnotherPos = shiftStart;
		boolean gapShiftToRight = true;

		// Sometime, the fixed control line doesn't reflect the correct
		// mutation paths. For example:
		//
		// Before the fix, the original input are:
		//
		//   NAs: "AAAAT   GTTT"
		//   CTL: ":::..---.:::"
		//   AAs: "LysIleLeuPhe"
		//
		// After the fix, the expected output should be:
		//
		//   NAs: "AAAATG   TTT"
		//   CTL: ":::.. ---:::"
		//   AAs: "LysIleLeuPhe"
		//
		// Note the CTL is neither "::::::---:::" nor ":::...---:::".
		//
		// To fix similar problems, we need to know the corresponding
		// NA codon ("ATG" in the example) and AA triplet ("Ile"). thus
		// we can calculate their shortest path via any valid codons.
		//
		// The difference of insertion and deletion should also be
		// addressed properly.
		if (isInsertion) {
			outerAAs = leftSeq;
			outerNAs = alignedNAs.substring(shiftStart, shiftStart + 3);
		}
		else {
			outerNAs = leftSeq;
			outerAAs = aaTripletLine.substring(shiftStart, shiftStart + 3);
		}

		if (gapStart - shiftStart < shiftEnd - gapEnd) {
			// For ".---.." or similar, swap the left and right
			// since the result should be "---..."
			gapShiftToRight = false;
			String tmp = leftSeq;
			leftSeq = rightSeq;
			rightSeq = tmp;

			tmp = leftCt;
			leftCt = rightCt;
			rightCt = tmp;
			outerAnotherPos = gapEnd - 1;
		}

		if (isInsertion) {
			outerNAs = alignedNAs.substring(
					outerAnotherPos, outerAnotherPos + 3);
		}
		else {
			outerAAs = aaTripletLine.substring(
					outerAnotherPos, outerAnotherPos + 3);
		}

		// Fix control line dots
		String tmpCt = CodonTranslation
				.generateControlString(outerNAs, outerAAs);
		int tmpCtLen = tmpCt.length();
		if (gapShiftToRight) {
			int ctLen = leftCt.length();
			if (ctLen >= tmpCtLen) {
				leftCt = tmpCt + leftCt.substring(tmpCt.length());
			}
			else {
				leftCt = tmpCt.substring(0, ctLen);
			}
		}
		else {
			int ctLen = rightCt.length();
			if (ctLen >= tmpCtLen) {
				rightCt = tmpCt + rightCt.substring(tmpCt.length());
			}
			else {
				rightCt = tmpCt.substring(0, ctLen);
			}
		}

		seq =
			seq.substring(0, shiftStart) +
			leftSeq + rightSeq +
			seq.substring(shiftEnd);
		control =
			control.substring(0, shiftStart) +
			leftCt + rightCt +
			control.substring(shiftEnd);

		// Save fixed result
		if (isInsertion) {
			aaTripletLine = seq;
		}
		else {
			alignedNAs = seq;
		}
		controlLine = control;
	}

	protected void fixAll() {
		int gapStart;
		int gapEnd;
		int shiftStart;
		int shiftEnd;
		boolean isInsertion;
		boolean isImproperlyAligned;
		// If the dashes matched ":-+|-+:" (start or end with colon),
		// it is SUFFICIENT but NOT NECESSARY to conclude that the gap is
		// aligned properly. Gap matched "[. ]-+[. ]" can also be aligned
		// properly. So we need to dive into this subset and check the triplet.
		Matcher matchedGap = maybeImproperCodonAlignment.matcher(controlLine);
		while (matchedGap.find()) {
			gapStart = matchedGap.start();
			gapEnd = matchedGap.end();
			isInsertion = aaTripletLine.charAt(gapStart) == ' ';
			shiftStart = 0;
			shiftEnd = 0;
			isImproperlyAligned = false;
			if (isInsertion) {
				// It is SUFFICIENT and NECESSARY to conclude that the gap
				// isn't aligned properly if the gap split a triplet AA.
				// Which is equivalent to either the tripletLine looks like
				// "G   lu" or "Gl   u".
				if (Character.isUpperCase(
						aaTripletLine.charAt(gapStart - 1))) {
					// for "G   lu"
					// Implicit cond: gapStart > 0 (ensured by regex) &&
					//                gapEnd < length - 1 (.. by lap)
					shiftStart = gapStart - 1;
					shiftEnd = gapEnd + 2;
					isImproperlyAligned = true;
				}
				else if (Character.isUpperCase(
						aaTripletLine.charAt(gapStart - 2))) {
					// for "Gl   u"
					// Implicit cond: gapStart > 1 (ensured by lap) &&
					//             	  gapEnd < length (.. by lap)
					shiftStart = gapStart - 2;
					shiftEnd = gapEnd + 1;
					isImproperlyAligned = true;
				}
			}
			else if (gapEnd - gapStart >= 3 &&
					Character.isLowerCase(aaTripletLine.charAt(gapStart)) &&
					Character.isLowerCase(aaTripletLine.charAt(gapEnd))) {
				// It is SUFFICIENT and NECESSARY to conclude that a
				// non-frameshift gap isn't aligned properly only if the
				// tripleLine chars at gapStart and gapEnd are both lowercases.
				// Since Either those chars is uppercase means proper
				// alignment.
				isImproperlyAligned = true;
				if (Character.isUpperCase(
						aaTripletLine.charAt(gapStart - 1))) {
					// for "G[lu...]..."
					// Implicit cond: gapStart > 0 (ensured by regex)
					shiftStart = gapStart - 1;
				}
				else {
					// for "Gl[u...]..."
					// Implicit cond: gapStart > 1 (ensured by lap)
					// 				  isUpper(aaTripletLine[gapStart - 2])
					shiftStart = gapStart - 2;
				}
				if (gapEnd + 1 == aaTripletLine.length() ||
					Character.isUpperCase(
						aaTripletLine.charAt(gapEnd + 1))) {
					// for "[...Gl]u(end)" or "[...Gl]uVal"
					// Implicit cond: gapEnd < length (ensured by lap)
					shiftEnd = gapEnd + 1;
				}
				else {
					// for "[...G]lu"
					// Implicit cond: gapEnd < length - 1 (by lap) &&
					// 			      isLower(aaTripletLine[gapEnd + 1])
					shiftEnd = gapEnd + 2;
				}
			}
			if (isImproperlyAligned) {
				touched = true;
				fixSingleGap(
					gapStart, gapEnd, shiftStart,
					shiftEnd, isInsertion);
			}
			/* String debug =
				"Type: " + (isInsertion ? "Insertion" : "Deletion") +
				" NeedFix?: " + isImproperlyAligned +
				" StartPos: " + gapStart +
				" EndPos: " + gapEnd;
			if (isImproperlyAligned) {
				debug +=
					" ShiftStartPos: " + shiftStart +
					" ShiftEndPos: " + shiftEnd;
			}
			System.out.println(debug); */
		}
	}
}

