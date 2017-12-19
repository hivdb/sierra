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
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.UnusualMutations;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.CodonTranslation;
import edu.stanford.hivdb.utilities.Sequence;

/**
 * Extends a submitted gene sequence beyond the part of a sequence aligned to the
 * gene's reference (consensus B). This allows mutations just upstream and downstream
 * of the aligned sequence to be identified. Only those mutations with a prevalence of
 * >0.1% in HIVDB cause the alignment to be extended.
 *
 * The constructor receives:
 * - Sequence sequence
 * - Gene gene
 * - Integer firstAA
 * - Integer lastAA
 * - Integer firstNA
 * - Integer lastNA
 * - String alignedNAs
 * - String controlLine
 * - String aaTripletLine
 *
 * Seven get Methods return:
 * - Integer getFirstAA()
 * - Integer getFirstNA()
 * - Integer getLastAA()
 * - Integer getLastNA()
 * - String getAlignedNAs()
 * - String getControlLine()
 * - String getAATripletLine()
 */
public class AlignmentExtension {

	private static final Double MIN_PREVALENCE_FOR_EXTENDED_MUTS = 0.1;

	private final Gene gene;
	private final Sequence sequence;
	private Integer firstAA;
	private Integer firstNA;
	private Integer lastAA;
	private Integer lastNA;
	private String alignedNAs;
	private String controlLine;
	private String aaTripletLine;

	public AlignmentExtension(
			Sequence sequence, Gene gene,
			int firstAA, int lastAA, int firstNA, int lastNA,
			String alignedNAs, String controlLine, String aaTripletLine) {
		this.gene = gene;
		this.sequence = sequence;
		this.firstAA = firstAA;
		this.firstNA = firstNA;
		this.lastAA = lastAA;
		this.lastNA = lastNA;
		this.alignedNAs = alignedNAs;
		this.controlLine = controlLine;
		this.aaTripletLine = aaTripletLine;
		process();
	}

	public int getFirstAA() { return firstAA; }
	public int getLastAA() { return lastAA; }
	public int getFirstNA() { return firstNA; }
	public int getLastNA() { return lastNA; }
	public String getAlignedNAs() { return alignedNAs; }
	public String getControlLine() { return controlLine; }
	public String getAATripletLine() { return aaTripletLine; }

	protected void process() {
		if (firstAA == 1 && lastAA == gene.getLength() ||
			firstNA == 1 && lastNA == sequence.getLength()) {
			return;
		}
		String rawSeq = sequence.getSequence();
		String upstream = getUpstream(rawSeq, firstAA - 1, firstNA);
		String downstream =
			getDownstream(rawSeq, gene.getLength() - lastAA, lastNA);
		upstream = removeUnusuals(gene, firstAA - 1, -1, upstream);
		downstream = removeUnusuals(gene, lastAA + 1, 1, downstream);

		int leftNA = upstream.length();
		int rightNA = downstream.length();
		int leftAA = leftNA / 3;
		int rightAA = rightNA / 3;

		// update alignment result
		alignedNAs = upstream + alignedNAs + downstream;

		firstAA -= leftAA;
		firstNA -= leftNA;
		String upstreamAATriplet =
			CodonTranslation.translateToTripletAA(
			gene.getConsensus(firstAA, leftAA));

		String downstreamAATriplet =
			CodonTranslation.translateToTripletAA(
			gene.getConsensus(lastAA + 1, rightAA));
		lastAA += rightAA;
		lastNA += rightNA;

		aaTripletLine =
			upstreamAATriplet + aaTripletLine + downstreamAATriplet;

		controlLine =
			CodonTranslation.generateControlString(
				upstream, upstreamAATriplet) +
			controlLine +
			CodonTranslation.generateControlString(
				downstream, downstreamAATriplet);
	}

	protected static Boolean isUnusual(Gene gene, Integer aaPos, String codon) {
		String aa = CodonTranslation.translateNATriplet(codon);
		String cons = gene.getConsensus(aaPos);
		if (cons.equals(aa)) {
			return false;
		}
		Mutation mut = new Mutation(gene, aaPos, aa);
		return UnusualMutations.getHighestMutPrevalence(mut) < MIN_PREVALENCE_FOR_EXTENDED_MUTS;
	}

	protected static String removeUnusuals(
			Gene gene, int aaStart, int direction, String stream) {
		final int streamAALen = stream.length() / 3;
		List<String> result = new ArrayList<>();

		for (int i = 0; i < streamAALen; i ++) {
			int pos;
			if (direction == -1) {
				pos = streamAALen - i - 1;
			}
			else {
				pos = i;
			}
			String nas = stream.substring(pos * 3, pos * 3 + 3);
			if (isUnusual(gene, aaStart + direction * i, nas)) {
				break;
			}
			if (direction == -1) {
				result.add(0, nas);
			}
			else {
				result.add(nas);
			}
		}

		return String.join("", result);

	}

	protected static String getUpstream(
			final String rawSeq, final int maxExtAA, final int firstNA) {
		String upstream = rawSeq.substring(0, firstNA - 1);
		int upstreamExtAA = Math.min(maxExtAA, upstream.length() / 3);
		return upstream.substring(upstream.length() - upstreamExtAA * 3);
	}

	protected static String getDownstream(
			final String rawSeq, final int maxExtAA, final int lastNA) {
		String downstream = rawSeq.substring(lastNA);
		final int downstreamExtAA = Math.min(maxExtAA, downstream.length() / 3);
		return downstream.substring(0, downstreamExtAA * 3);
	}

}
