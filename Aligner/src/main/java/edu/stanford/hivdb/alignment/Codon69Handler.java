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

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.mutations.CodonTranslation;

public class Codon69Handler {

	private static final int AA_WINDOW_LEFT_POS = 63;
	private static final int AA_WINDOW_RIGHT_POS = 73;
	private static final int AA_MAGNET_POS = 69;

	private final int firstAA;
	protected String alignedNAs;
	protected String controlLine;
	protected String aaTripletLine;

	public Codon69Handler (
			final int firstAA, final String alignedNAs,
			final String controlLine, final String aaTripletLine) {
		this.firstAA = firstAA;
		this.alignedNAs = alignedNAs;
		this.controlLine = controlLine;
		this.aaTripletLine = aaTripletLine;
	}

	public static CLapAlignResult process(
			int firstAA, String alignedNAs,
			String controlLine, String aaTripletLine) {
		// use interface in the future
		Codon69Handler self = new Codon69Handler(
			firstAA, alignedNAs, controlLine, aaTripletLine);
		self.apply();
		return new CLapAlignResult(
			self.alignedNAs, self.controlLine, self.aaTripletLine);
	}

	public void apply() {
		// #26: leftAA and rightAA shouldn't overflow current sequence
		// or it will cause StringIndexOutOfBoundsException error
		int leftAA = Math.max(AA_WINDOW_LEFT_POS, firstAA);
		int rightAA = Math.min(
			AA_WINDOW_RIGHT_POS,
			firstAA + aaTripletLine.replace(" ", "").length() / 3);
		int magnetAA = AA_MAGNET_POS;
		if (magnetAA <= leftAA || magnetAA >= rightAA) {
			// magnetAA is outside of given sequence,
			// the code is still working but no need to re-align codons
			return;
		}

		int insertionCount;
		int leftNA = aaToNAPos(leftAA - firstAA);
		int rightNA = aaToNAPos(rightAA - firstAA);

		String control = "";
		String nas = alignedNAs.substring(leftNA, rightNA);
		String aaTriplet = aaTripletLine.substring(leftNA, rightNA);

		// step 1: remove all insertion dashes and spaces
		aaTriplet = aaTriplet.replace(" ", "");
		insertionCount = nas.length() - aaTriplet.length();
		if (insertionCount == 0) {
			return;
		}

		// step 2: re-generate control strings
		int tmpAALen = aaTriplet.length() / 3;
		for (int i=0; i < tmpAALen; i ++) {
			String na = nas.substring(i * 3, i * 3 + 3);
			String aa = aaTriplet.substring(i * 3, i * 3 + 3);
			// TODO: This may pollute the control line if there's any
			// frameshift inside the area. It's a bug and need to be fixed.
			control += CodonTranslation.generateControlString(na, aa);
		}

		// step 3: insert dashes
		int relMagnetNA = (magnetAA + 1 - leftAA) * 3;
		control =
			control.substring(0, relMagnetNA) +
			StringUtils.repeat("-", insertionCount) +
			control.substring(relMagnetNA);
		aaTriplet =
			aaTriplet.substring(0, relMagnetNA) +
			StringUtils.repeat(" ", insertionCount) +
			aaTriplet.substring(relMagnetNA);

		controlLine =
			controlLine.substring(0, leftNA) +
			control +
			controlLine.substring(rightNA);

		aaTripletLine =
			aaTripletLine.substring(0, leftNA) +
			aaTriplet +
			aaTripletLine.substring(rightNA);

		return;
	}

	private boolean isInsertion(int pos) {
		return aaTripletLine.charAt(pos) == ' ';
	}

	private int aaToNAPos(int aaPos) {
		int expectedNAPos = aaPos * 3;
		int controlLineLen = controlLine.length();
		for (int i=0; i < controlLineLen && i < expectedNAPos; i ++) {
			if (isInsertion(i)) {
				expectedNAPos ++;
			}
		}
		if (expectedNAPos > controlLineLen) {
			expectedNAPos = controlLineLen;
		}
		return expectedNAPos;
	}

}
