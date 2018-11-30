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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class PrettyPairwise {

	private List<String> positionLine = new ArrayList<>();
	private List<String> refAALine = new ArrayList<>();
	private List<String> alignedNAsLine = new ArrayList<>();
	private List<String> mutationLine = new ArrayList<>();


	public List<String> getPositionLine() { return positionLine; }
	public List<String> getRefAALine() { return refAALine; }
	public List<String> getAlignedNAsLine() { return alignedNAsLine; }
	public List<String> getMutationLine() { return mutationLine; }


	public PrettyPairwise(
			final Gene gene, final String alignedNAs, final int firstAA,
			final MutationSet mutations, Collection<FrameShift> frameShifts) {
		int numAAs = (int) alignedNAs.length()/3;
		Map<Integer, FrameShift> fsInsMap = (
			frameShifts.stream()
			.filter(fs -> fs.isInsertion())
			.collect(Collectors.toMap(
				fs -> fs.getPosition(),
				fs -> fs)));

		for (int i=0; i<numAAs; i++) {
			int aaPos = firstAA + i;
			String fmtAAPos;
			if (aaPos <100) {
				fmtAAPos = String.format("%2d ", aaPos);
			} else {
				fmtAAPos = String.format("%3d", aaPos);
			}
			String fmtAACons = String.format(" %1s ", gene.getReference(aaPos));
			String codon = alignedNAs.substring(i*3, (i*3) + 3);
			Mutation mut = mutations.get(gene, aaPos);

			if (mut == null) {
				this.positionLine.add(fmtAAPos);
				this.refAALine.add(fmtAACons);
				this.alignedNAsLine.add(codon);
				this.mutationLine.add(" - ");

			} else if (mut.isInsertion()){
				// TODO: what about NGS insertion?

				// Get information about the insertion
				String insertedNAs = mut.getInsertedNAs();
				String[] insertionAAText = mut.getDisplayAAs().split("_", 2);
				String preInsertionAA = insertionAAText[0];
				String fmtPreInsertionAA;

				if (preInsertionAA.length() == 1) {
					fmtPreInsertionAA = " " + preInsertionAA + " ";
 				} else if (preInsertionAA.length() == 2) {
 					fmtPreInsertionAA = preInsertionAA + " ";
 				} else {
 					fmtPreInsertionAA = preInsertionAA;
 				}

				char[] insertedAAs = insertionAAText[1].toCharArray();

				// Handle the position with the insertion
				this.positionLine.add(fmtAAPos);
				this.refAALine.add(fmtAACons);
				this.alignedNAsLine.add(codon);
				this.mutationLine.add(fmtPreInsertionAA);

				// If the insertedAAs longer than 1, split the insertion per codon
				for (int j = 0; j < insertedAAs.length; j ++) {
					String insertedSpaces = "   ";

					// There can only be one insertion AA per position as mixtures are translated to 'X'.
					String fmtInsertedAAs = " " + insertedAAs[j] + " ";

					//Handle the insertion
					this.positionLine.add(insertedSpaces);
					this.refAALine.add(insertedSpaces);
					this.alignedNAsLine.add(insertedNAs.substring(j * 3, j * 3 + 3));
					this.mutationLine.add(fmtInsertedAAs);
				}

			} else if (mut.isDeletion()) {
				this.positionLine.add(fmtAAPos);
				this.refAALine.add(fmtAACons);
				this.alignedNAsLine.add(codon);
				this.mutationLine.add("Del");

			} else {
				this.positionLine.add(fmtAAPos);
				this.refAALine.add(fmtAACons);
				this.alignedNAsLine.add(codon);
				String mutAAs = mut.getAAsWithRefFirst();
				String fmtMutAAs;
				if (mutAAs.length() == 1) {
					fmtMutAAs = " " + mutAAs + " ";
 				} else if (mutAAs.length() == 2) {
 					fmtMutAAs = mutAAs + " ";
 				} else {
 					fmtMutAAs = mutAAs;
 				}
				this.mutationLine.add(fmtMutAAs);
			}

			FrameShift fsIns = fsInsMap.get(aaPos);
			if (fsIns != null) {
				// Get Frameshift data
				String insertionNAs = fsIns.getNAs();
				int insertionSize = fsIns.getSize();
				int numMissingNAs = 3 - insertionSize%3;
				String allotedSpace = StringUtils.repeat(" ", insertionSize + numMissingNAs);

				this.positionLine.add(allotedSpace);
				this.refAALine.add(allotedSpace);
				this.alignedNAsLine.add(insertionNAs + StringUtils.repeat(" ", numMissingNAs));
				this.mutationLine.add(StringUtils.repeat("^", insertionSize) +
						StringUtils.repeat(" ", numMissingNAs));
			}
		}
	}

}
