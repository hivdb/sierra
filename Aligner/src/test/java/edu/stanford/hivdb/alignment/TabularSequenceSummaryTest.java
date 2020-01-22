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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.hivfacts.extras.TabularSequenceSummary;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;

public class TabularSequenceSummaryTest {
	private static List<AlignedSequence> allSequenceResults = new ArrayList<>();
	private static String[] headerFields;
	private static Map<String, Map<String, String>> tabularSequence = new HashMap<>();

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.VGI);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		allSequenceResults = NucAminoAligner.parallelAlign(sequences);
		TabularSequenceSummary tabularSequenceSummary = new TabularSequenceSummary(allSequenceResults);
		tabularSequence= tabularSequenceSummary.getTable();
		headerFields = tabularSequenceSummary.getHeaderFields();
		printOutTable();
	}


	private static void printOutTable() {
		List<Integer> maxFieldSizes = new ArrayList<>();
		for (int i=0; i<headerFields.length; i++) {
			int maxFieldSize = headerFields[i].length();
			for (String seqName : tabularSequence.keySet()) {
				String dataItem = tabularSequence.get(seqName).get(headerFields[i]);
				if (dataItem.length() > maxFieldSize) {
					maxFieldSize = dataItem.length();
				}
			}
			maxFieldSizes.add(maxFieldSize+2);
		}

		StringBuffer output = new StringBuffer();
		String header = String.format("%25s", "Sequence Names");
		for (int i=0; i<headerFields.length; i++) {
			int maxFieldSize = maxFieldSizes.get(i);
			header += String.format("%" + maxFieldSize + "s", headerFields[i]);
		}
		output.append(header + "\n");

		for (String seqName : tabularSequence.keySet()) {
			output.append(String.format("%25s", seqName));
			for (int i=0; i<headerFields.length; i++) {
				int maxFieldSize = maxFieldSizes.get(i);
				String dataItem = tabularSequence.get(seqName).get(headerFields[i]);
				output.append(String.format("%" + maxFieldSize + "s", dataItem));
			}
			output.append("\n");
		}
		System.out.println(output.toString());
		final String outputFile =  "TabularSequenceSummaryOutput.txt";
		MyFileUtils.writeFile(outputFile, output.toString());
	}

}


