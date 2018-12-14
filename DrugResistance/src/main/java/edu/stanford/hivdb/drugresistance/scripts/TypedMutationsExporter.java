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

package edu.stanford.hivdb.drugresistance.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.hivdb.aapcnt.HIVAminoAcidPercent;
import edu.stanford.hivdb.aapcnt.HIVAminoAcidPercents;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.TSV;

public class TypedMutationsExporter {
	private static final String OUTPUT_FILE_PREFIX =
		"__output/TypedMutations";

	public static void main(String[] args) {
		exportApobec("apobec-muts");
		exportApobec("apobec-drms");
		exportUnusuals();
	}

	private static void exportUnusuals() {
		HIVAminoAcidPercents allAAPcnts = HIVAminoAcidPercents.getInstance("all", "All");

		for (Gene gene : Gene.values()) {
			List<String> headers = new ArrayList<>();
			Map<Character, List<String>> rows = new TreeMap<>();
			headers.add("AA");
			for (int pos = 1; pos <= gene.getLength(); pos ++) {
				headers.add("" + pos);
			}
			for (HIVAminoAcidPercent aaPcnt : allAAPcnts.get(gene)) {
				if (!rows.containsKey(aaPcnt.aa)) {
					List<String> row = new ArrayList<>();
					row.add("" + aaPcnt.aa);
					rows.put(aaPcnt.aa, row);
				}
				rows.get(aaPcnt.aa).add(aaPcnt.isUnusual ? "1" : "0");
			}
			String output = TSV.dumps(headers,  rows.values());
			String outputFile = OUTPUT_FILE_PREFIX + "/unusual-mutations-" + gene + ".tsv";
			MyFileUtils.writeFile(outputFile, output);
		}
	}

	private static void exportApobec(String type) {
		List<String> headers = new ArrayList<>();
		headers.add("Gene");
		headers.add("Consensus");
		headers.add("Position");
		headers.add("AAs");

		List<List<String>> rows = new ArrayList<>();
		MutationSet mutSet;

		if (type == "apobec-muts") {
			mutSet = Apobec.getApobecMutsLU();
		}
		else {
			mutSet = Apobec.getApobecDRMsLU();
		}
		for (Mutation mut : mutSet) {
			List<String> row = new ArrayList<>();
			row.add(mut.getGene().toString());
			row.add(mut.getReference());
			row.add("" + mut.getPosition());
			row.add(mut.getAAs());
			rows.add(row);
		}
		String output = TSV.dumps(headers, rows);
		String outputFile = OUTPUT_FILE_PREFIX + "/" + type + ".tsv";
		MyFileUtils.writeFile(outputFile, output);
	}
}
