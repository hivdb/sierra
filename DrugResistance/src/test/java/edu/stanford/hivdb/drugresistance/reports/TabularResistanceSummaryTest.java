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

package edu.stanford.hivdb.drugresistance.reports;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.alignment.Aligner;
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.drugresistance.reports.TabularResistanceSummary;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class TabularResistanceSummaryTest {
	private static Map<String, Map<String, String>> tabularResistance = new HashMap<>();
	private static List<String> headerFields;

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		List<Map<Gene, GeneDR>> allResistanceResults = determineAllResistanceResults(sequences);
		TabularResistanceSummary tabularResistanceSummary = new TabularResistanceSummary(sequences, allResistanceResults);
		tabularResistance = tabularResistanceSummary.getTable();
		headerFields = tabularResistanceSummary.getHeaderFields();
		printOutTable();
	}

	private static void printOutTable() {
		List<Integer> maxFieldSizes = new ArrayList<>();
		for (String header : headerFields) {
			int maxFieldSize = header.length();
			for (String seqName : tabularResistance.keySet()) {
				String dataItem = tabularResistance.get(seqName).get(header);
				if (dataItem.length() > maxFieldSize) {
					maxFieldSize = dataItem.length();
				}
			}
			maxFieldSizes.add(maxFieldSize+2);
		}

		StringBuffer output = new StringBuffer();
		String header = String.format("%25s", "Sequence Names");
		for (int i=0; i<headerFields.size(); i++) {
			int maxFieldSize = maxFieldSizes.get(i);
			header += String.format("%" + maxFieldSize + "s", headerFields.get(i));
		}
		output.append(header + "\n");

		for (String seqName : tabularResistance.keySet()) {
			output.append(String.format("%25s", seqName));
			for (int i=0; i<headerFields.size(); i++) {
				int maxFieldSize = maxFieldSizes.get(i);
				String dataItem = tabularResistance.get(seqName).get(headerFields.get(i));
				output.append(String.format("%" + maxFieldSize + "s", dataItem));
			}
			output.append("\n");
		}
		System.out.println(output.toString());
		final String outputFile =  "TabularResistanceSummaryOutput.txt";
		MyFileUtils.writeFile(outputFile, output.toString());
	}


	private static List<Map<Gene, GeneDR>> determineAllResistanceResults(List<Sequence> sequences) {
		Map<Gene, GeneDR> resistanceResults = new HashMap<>();
		List<Map<Gene, GeneDR>> allResistanceResults = new ArrayList<>();
		for (Sequence seq : sequences) {
			AlignedSequence alignedSeq = Aligner.align(seq);
			List<AlignedGeneSeq> alignmentResults = alignedSeq.getAlignedGeneSequences();
			resistanceResults = GeneDRFast.getResistanceByGeneFromAlignedGeneSeqs(alignmentResults);
			allResistanceResults.add(resistanceResults);
		}
		return allResistanceResults;
	}

}