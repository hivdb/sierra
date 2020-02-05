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

package edu.stanford.hivdb.reports;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.reports.SequenceSummaryTSV;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.FastaUtils;

public class SequenceSummaryTSVTest {
	
	private final static HIV hiv = HIV.getInstance();
	

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.VGI);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		final List<AlignedSequence<HIV>> allSequenceResults = NucAminoAligner.getInstance(hiv).parallelAlign(sequences);
		SequenceSummaryTSV<HIV> seqSummaryTSV = SequenceSummaryTSV.getInstance(hiv);
		printOutTable(
			seqSummaryTSV.getHeaderFields(),
			seqSummaryTSV.getReportRows(allSequenceResults)
		);
	}


	private static void printOutTable(
		List<String> headerFields,
		List<Map<String, String>> tabularSequence
	) {
		Map<String, Integer> maxFieldSizes = new HashMap<>();
		for (String header : headerFields) {
			int maxFieldSize = header.length();
			for (Map<String, String> row : tabularSequence) {
				String dataItem = row.getOrDefault(header, "NA");
				if (dataItem.length() > maxFieldSize) {
					maxFieldSize = dataItem.length();
				}
			}
			maxFieldSizes.put(header, maxFieldSize + 2);
		}

		StringBuffer output = new StringBuffer();
		for (String header : headerFields) {
			int maxFieldSize = maxFieldSizes.get(header);
			output.append(String.format("%" + maxFieldSize + "s", header));
		}
		output.append("\n");

		for (Map<String, String> seqRow : tabularSequence) {
			for (String header : headerFields) {
				int maxFieldSize = maxFieldSizes.get(header);
				String dataItem = seqRow.getOrDefault(header, "NA");
				output.append(String.format("%" + maxFieldSize + "s", dataItem));
			}
			output.append("\n");
		}
		System.out.println(output.toString());
		// final String outputFile =  "TabularSequenceSummaryOutput.txt";
		// MyFileUtils.writeFile(outputFile, output.toString());
	}

}


