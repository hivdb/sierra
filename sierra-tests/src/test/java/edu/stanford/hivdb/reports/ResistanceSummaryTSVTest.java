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

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.reports.ResistanceSummaryTSV;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.viruses.Gene;


public class ResistanceSummaryTSVTest {

	final static HIV hiv = HIV.getInstance();

	@Test
	public void testGetInstance() {
		assertNotNull(ResistanceSummaryTSV.getInstance(hiv));
	}
	
	@Test
	public void testGetHeaderFields() {
		ResistanceSummaryTSV<HIV> rs = ResistanceSummaryTSV.getInstance(hiv);
		List<String> headerFields = rs.getHeaderFields();
		assertEquals("Sequence Name", headerFields.get(0));
		assertEquals("Strain", headerFields.get(1));
		assertEquals("Genes", headerFields.get(2));
		assertEquals("PI Major", headerFields.get(3));
		assertEquals("PI Accessory", headerFields.get(4));
		assertEquals("Algorithm Name", headerFields.get(59));
		assertEquals("Algorithm Version", headerFields.get(60));
		assertEquals("Algorithm Date", headerFields.get(61));

	}
	
	@Test
	public void testGetReport() {
		ResistanceSummaryTSV<HIV> rs = ResistanceSummaryTSV.getInstance(hiv);
		
		DrugResistanceAlgorithm<HIV> algo = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		
		
		
	 	final InputStream testSequenceInputStream =
	 			TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
	 	final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
	 	
	 	List<AlignedSequence<HIV>> alignedSeqs = new ArrayList<>();
	 	List<Map<Gene<HIV>, GeneDR<HIV>>> allResistanceResults = new ArrayList<>();
	 	
	 	for (Sequence seq : sequences) {
	 		AlignedSequence<HIV> alignedSeq = NucAminoAligner.getInstance(hiv).align(seq);
	 		List<Gene<HIV>> genes = alignedSeq.getAvailableGenes();
	 		
	 		Map<Gene<HIV>, GeneDR<HIV>> resistanceResults = new TreeMap<>();
	 		for (Gene<HIV> gene: genes) {
	 			GeneDR<HIV> geneDR = new GeneDR<HIV>(gene, new MutationSet<HIV>(), algo);
	 			resistanceResults.put(gene, geneDR);
	 		}
	 		
	 		allResistanceResults.add(resistanceResults);
	 		alignedSeqs.add(alignedSeq);	
	 	}
	 

	 	rs.getReport(alignedSeqs, allResistanceResults, algo);
		
	}
}
