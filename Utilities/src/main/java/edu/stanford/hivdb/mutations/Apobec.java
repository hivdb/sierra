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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.stanford.hivdb.hivfacts.HIVAPOBECMutation;
import edu.stanford.hivdb.hivfacts.HIVAPOBECMutations;

public class Apobec {
	private final static HIVAPOBECMutations hivApobecs = HIVAPOBECMutations.getInstance();

	private final static MutationSet apobecMutsLU;
	private final static MutationSet apobecDRMsLU;

	private transient final MutationSet apobecMuts;
	private transient final MutationSet apobecDRMs;
	
	static {
		List<Mutation> _apobecsLU = new ArrayList<>();
		for (HIVAPOBECMutation m : hivApobecs.getApobecMutations()) {
			_apobecsLU.add(new Mutation(
				Gene.valueOf(m.getGene()), m.getPosition(), m.getAA()));
		}
		apobecMutsLU = new MutationSet(_apobecsLU);
		List<Mutation> _apobecDRMsLU = new ArrayList<>();
		for (HIVAPOBECMutation m : hivApobecs.getApobecDRMs()) {
			_apobecDRMsLU.add(new Mutation(
				Gene.valueOf(m.getGene()), m.getPosition(), m.getAA()));
		}
		apobecDRMsLU = new MutationSet(_apobecDRMsLU);	
	}


	public static boolean isApobecMutation(Mutation mutation) {
		return apobecMutsLU.hasSharedAAMutation(mutation);
	}

	public static boolean isApobecDRM(Mutation mutation) {
		return apobecDRMsLU.hasSharedAAMutation(mutation);
	}

	public static MutationSet getApobecMutsLU() {
		return apobecMutsLU;
	}

	public static MutationSet getApobecDRMsLU() {
		return apobecDRMsLU;
	}

	public Apobec(MutationSet seqMuts) {
		apobecMuts = seqMuts.intersectsWith(apobecMutsLU);
		apobecDRMs = seqMuts.intersectsWith(apobecDRMsLU);
	}

	public MutationSet getApobecMuts() { return apobecMuts; }
	public int getNumApobecMuts() { return apobecMuts.size(); }
	public MutationSet getApobecDRMs() { return apobecDRMs; }

	public MutationSet getApobecMutsAtDRP() {
		return apobecMuts.getAtDRPMutations();
	}

	private static String generatePartialComment
			(String description, MutationSet muts) {
		StringBuffer comment = new StringBuffer();
		comment.append(String.format(description, muts.size()));
		comment.append(": ");

		comment.append(
			muts.groupByGene()
			.entrySet()
			.stream()
			.map(e -> {
				final Gene gene = e.getKey();
				final MutationSet subset = e.getValue();
				return String.format("%s: %s", gene, subset.join(", "));
			})
			.collect(Collectors.joining("; ")));
		comment.append(".");

		return comment.toString();
	}

	public String generateComment() {
		if (apobecMuts.size() < 1 && apobecDRMs.size() < 1) {
			return "There are no mutations present in this sequence.";
		}

		StringBuffer comment = new StringBuffer();

		comment.append(generatePartialComment(
			"The following %d APOBEC muts were present " +
		    "in the sequence", apobecMuts));


		if (apobecDRMs.size() > 0) {
			comment.append(generatePartialComment(
				" The following %d DRMs in this sequence " +
				"could reflect APOBEC activity", apobecDRMs));
		}

		return comment.toString();
	}
}
