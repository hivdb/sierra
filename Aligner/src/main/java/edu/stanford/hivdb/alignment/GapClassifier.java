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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.hivdb.alignment.AlignmentGap.AlignmentGapType;

public class GapClassifier {


	private final String controlLine;
	private final String alignedNAs;
	private final Map<Integer, Integer> gapOrdinal;

	public GapClassifier (String controlLine, String alignedNAs) {
		this.controlLine = controlLine;
		this.alignedNAs = alignedNAs;
		this.gapOrdinal = new HashMap<Integer, Integer>();
	}

	private int incrementAndGetGapOrdinal(int pos) {
		int ordinal = 0;
		if (gapOrdinal.containsKey(pos)) {
			ordinal = gapOrdinal.get(pos);
		}
		ordinal ++;
		gapOrdinal.put(pos, ordinal);
		return ordinal;
	}

	private List<AlignmentGap> findGapsBetween(
			int gapStart, int gapEnd, AlignmentGapType typeShift,
			AlignmentGapType typeCodon, int codonOffset,
			boolean isInsertion) {
		AlignmentGap gap;
		int ordinal;
		int gapSize = gapEnd - gapStart;
		int fixedStart = gapStart + codonOffset;
		int fixedEnd = gapEnd + codonOffset;
		List<AlignmentGap> resultGaps = new ArrayList<>();

		if (gapSize < 3) {
			// situation that the gap can only be a FrameShift
			ordinal = incrementAndGetGapOrdinal(fixedStart);
			gap = new AlignmentGap(
					gapStart, fixedStart, gapSize, ordinal, typeShift);
			resultGaps.add(gap);
		}
		else if (gapSize == 3 && fixedStart % 3 > 0) {
			// this situation shouldn't happen. These shifting gaps are
			// caused by a bug of CLap program and is already addressed
			// properly in class CLapAlign.
			//
			// Please leave this message here as explanation.
		}
		else {
			// situations that there must be one or more codons in the
			// gap, and there may be FrameShift(s) on the left side
			// side or/and right side of the continuous codons.
			int leftShiftSize = (3 - fixedStart % 3) % 3;
			int rightShiftSize = fixedEnd % 3;
			if (leftShiftSize > 0) {
				ordinal = incrementAndGetGapOrdinal(fixedStart);
				gap = new AlignmentGap(
					gapStart, fixedStart, leftShiftSize, ordinal, typeShift);
				resultGaps.add(gap);
			}

			// these actually are start, end and size of continuous codons.
			//
			// for insertion, only one insertion gap is needed since there's
			// also only one single mutation (for example: I54I_KK) should be
			// created later.
			// in another hand, number of deletion gaps should be consistent
			// to the number of codons in the gap.
			gapStart += leftShiftSize;
			gapEnd -= rightShiftSize;
			gapSize = gapEnd - gapStart;
			if (isInsertion) {
				// even gap been separated the fixed position shouldn't be
				// changed for insertion. since they are "new".
				fixedEnd = fixedStart;
				ordinal = incrementAndGetGapOrdinal(fixedStart);
				gap = new AlignmentGap(
					gapStart, fixedStart, gapSize, ordinal, typeCodon);
				resultGaps.add(gap);
			}
			else {
				// fixed positions should be updated only for deletion
				fixedStart = gapStart + codonOffset;
				fixedEnd = gapEnd + codonOffset;
				for (; gapStart < gapEnd; gapStart += 3) {
					fixedStart = gapStart + codonOffset;
					ordinal = incrementAndGetGapOrdinal(fixedStart);
					gap = new AlignmentGap(
						gapStart, fixedStart, 3, ordinal, typeCodon);
					resultGaps.add(gap);
				}
			}
			if (rightShiftSize > 0) {
				ordinal = incrementAndGetGapOrdinal(fixedEnd);
				gap = new AlignmentGap(
					gapEnd, fixedEnd, rightShiftSize, ordinal, typeShift);
				resultGaps.add(gap);
			}
		}
		return resultGaps;
	}

	public List<AlignmentGap> classifyGaps() {
		// insertion interfered the position calculation of codons,
		// this's the offset variable so we can shift them back
		int codonOffset = 0;
		List<AlignmentGap> gaps = new ArrayList<AlignmentGap>();
		String dashesRegex = "(\\-+)";
		Pattern dashes = Pattern.compile(dashesRegex);
		Matcher matchedDashes = dashes.matcher(controlLine);
		while (matchedDashes.find()) {
			int gapStart = matchedDashes.start();
			int gapEnd = matchedDashes.end();
			if (IsInsertion(gapStart)) {
				gaps.addAll(findGapsBetween(
					gapStart, gapEnd, AlignmentGapType.FRAME_SHIFT_INSERTION,
					AlignmentGapType.INSERTION, codonOffset, true));
				codonOffset -= gapEnd - gapStart;
			}
			else {
				gaps.addAll(findGapsBetween(
					gapStart, gapEnd, AlignmentGapType.FRAME_SHIFT_DELETION,
					AlignmentGapType.DELETION, codonOffset, false));
			}
		}
		return gaps;
	}

	public boolean IsInsertion(int startPos) {
		return ' ' != alignedNAs.charAt(startPos);
	}

}
