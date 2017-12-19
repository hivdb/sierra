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
import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class PrettyPairwise {

	private List<String> positionLine = new ArrayList<>();
	private List<String> refAALine = new ArrayList<>();
	private List<String> alignedNAsLine = new ArrayList<>();
	private List<String> mutationLine = new ArrayList<>();


	private PrettyPairwise() {};
	public List<String> getPositionLine() { return positionLine; }
	public List<String> getRefAALine() { return refAALine; }
	public List<String> getAlignedNAsLine() { return alignedNAsLine; }
	public List<String> getMutationLine() { return mutationLine; }


	public static PrettyPairwise createPrettyAlignment(Gene gene, AlignedGeneSeq alignedGeneSeq) {
		PrettyPairwise prettyPairwise = new PrettyPairwise();
		String alignedNAs = alignedGeneSeq.getAlignedNAs();
		int numAAs = (int) alignedNAs.length()/3;
		int refFirstAA = alignedGeneSeq.getFirstAA();
		MutationSet mutations = alignedGeneSeq.getMutations();

		for (int i=0; i<numAAs; i++) {
			int aaPos = refFirstAA + i;
			String fmtAAPos;
			if (aaPos <100) {
				fmtAAPos = String.format("%2d ", aaPos);
			} else {
				fmtAAPos = String.format("%3d", aaPos);
			}
			String fmtAACons = String.format(" %1s ", gene.getConsensus(aaPos));
			String codon = alignedNAs.substring(i*3, (i*3) + 3);
			Mutation mut = mutations.get(gene, aaPos);

			if (mut == null) {
				prettyPairwise.positionLine.add(fmtAAPos);
				prettyPairwise.refAALine.add(fmtAACons);
				prettyPairwise.alignedNAsLine.add(codon);
				prettyPairwise.mutationLine.add(" - ");

			} else if ( mut.isInsertion()){

				// Get information about the insertion
				String insertedNAs = mut.getInsertedNAs();
				String[] insertionAAText = mut.getAAs().split("_", 2);
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
				prettyPairwise.positionLine.add(fmtAAPos);
				prettyPairwise.refAALine.add(fmtAACons);
				prettyPairwise.alignedNAsLine.add(codon);
				prettyPairwise.mutationLine.add(fmtPreInsertionAA);

				// If the insertedAAs longer than 1, split the insertion per codon
				for (int j = 0; j < insertedAAs.length; j ++) {
					String insertedSpaces = "   ";

					// There can only be one insertion AA per position as mixtures are translated to 'X'.
					String fmtInsertedAAs = " " + insertedAAs[j] + " ";

					//Handle the insertion
					prettyPairwise.positionLine.add(insertedSpaces);
					prettyPairwise.refAALine.add(insertedSpaces);
					prettyPairwise.alignedNAsLine.add(insertedNAs.substring(j * 3, j * 3 + 3));
					prettyPairwise.mutationLine.add(fmtInsertedAAs);
				}

			} else if (mut.isDeletion()) {
				prettyPairwise.positionLine.add(fmtAAPos);
				prettyPairwise.refAALine.add(fmtAACons);
				prettyPairwise.alignedNAsLine.add(codon);
				prettyPairwise.mutationLine.add("Del");

			} else {
				prettyPairwise.positionLine.add(fmtAAPos);
				prettyPairwise.refAALine.add(fmtAACons);
				prettyPairwise.alignedNAsLine.add(codon);
				String mutAAs = mut.getAAsWithConsFirst();
				String fmtMutAAs;
				if (mutAAs.length() == 1) {
					fmtMutAAs = " " + mutAAs + " ";
 				} else if (mutAAs.length() == 2) {
 					fmtMutAAs = mutAAs + " ";
 				} else {
 					fmtMutAAs = mutAAs;
 				}
				prettyPairwise.mutationLine.add(fmtMutAAs);
			}

			FrameShift fsInsertion = alignedGeneSeq.checkPosForFrameShiftInsertion(aaPos);
			if (fsInsertion != null) {
				//System.out.println("  FrameShiftInsertion: Pos:" + aaPos + " FS:" + fsInsertion);
				// Get Frameshift data
				String insertionNAs = fsInsertion.getNAs();
				int insertionSize = fsInsertion.getSize();
				int numMissingNAs = 3 - insertionSize%3;
				String allotedSpace = StringUtils.repeat(" ", insertionSize + numMissingNAs);

				prettyPairwise.positionLine.add(allotedSpace);
				prettyPairwise.refAALine.add(allotedSpace);
				prettyPairwise.alignedNAsLine.add(insertionNAs + StringUtils.repeat(" ", numMissingNAs));
				prettyPairwise.mutationLine.add(StringUtils.repeat("^", insertionSize) +
						StringUtils.repeat(" ", numMissingNAs));
			}
		}
		return prettyPairwise;
	}







}
