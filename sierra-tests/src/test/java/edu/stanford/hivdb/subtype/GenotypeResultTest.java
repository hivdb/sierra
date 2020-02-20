package edu.stanford.hivdb.subtype;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.genotypes.BoundGenotype;
import edu.stanford.hivdb.genotypes.Genotype;
import edu.stanford.hivdb.genotypes.GenotypeResult;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.testutils.TestUtils;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;

public class GenotypeResultTest {
    private static final Logger LOGGER = LogManager.getLogger();
	private static final String filePath = "SubtypeResults.txt";

    private static final HIV hiv = HIV.getInstance();

    @Test
    public void test() {
		final boolean isTravisBuild = System.getenv().getOrDefault("TRAVIS", "false").equals("true");
		if (isTravisBuild) {
			return;
		}
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SUBTYPE_TESTS_ALL);
		List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		StringBuffer output = new StringBuffer();
		sequences = sequences.subList(0, 1000);

		List<AlignedSequence<HIV>> allAligneds = NucAminoAligner.getInstance(hiv).parallelAlign(sequences);

		for (AlignedSequence<HIV> alignedSeq : allAligneds) {

			// Example header: >B|107258|PR_RT|2253|3555|EU255372|Spain|2005|Holguin08|18691031|7|0
			// Subtype|PID|Genes|firstNA|lastNA|GB|Country|Year|AuthorYr|PMID|NumMix|NumDRM
			// System.out.println("SequenceNumber:" + i + "  " + sequence.getHeader());

			Sequence sequence = alignedSeq.getInputSequence();
			String[] headerFields = StringUtils.split(sequence.getHeader(), "|");
			Genotype<HIV> knownGenotype = hiv.getGenotype(headerFields[0]);
			int firstNAFromHeader = Integer.parseInt(headerFields[3]);

			LOGGER.debug("Sequence header:" + sequence.getHeader());
			LOGGER.debug("From header: Subtype:" + knownGenotype + " FirstNA:" + firstNAFromHeader);

			String completeSequence = alignedSeq.getConcatenatedSeq();
			int firstNA = alignedSeq.getStrain().getAbsoluteFirstNA();
			MutationSet<HIV> sdrms = alignedSeq.getSdrms();
			LOGGER.debug("Genes:" + alignedSeq.getAvailableGenes() + " firstNA:" + firstNA);
			LOGGER.debug("completeSequence:" + completeSequence);
			LOGGER.debug("SDRMs:" + sdrms.join());

			GenotypeResult<HIV> genotypeResult = alignedSeq.getGenotypeResult();

			BoundGenotype<HIV> bestMatch = genotypeResult.getBestMatch();
			//Subtype closestSubtype = closestSubtypes.get(0);
			//Double closestDistance = closestDistances.get(0);

			if (bestMatch.getGenotype() != knownGenotype) {
				output.append("Conflict" + "\t");
				output.append("Subtype:" + bestMatch.getDisplay() + "\t");
				output.append("SubtypeInDB:" + knownGenotype + "\t" +  sequence.getHeader());
				output.append("\n");
			} else {
				//output.append("OK" + "\t");
			}


		}

		TestUtils.writeFile(filePath, output.toString());
    }

	/*private static String printOutSubypeResults(Map<Integer, Map<Subtype, Map<String, Double>>> subtypeResults) {
	StringBuffer output = new StringBuffer();
	for (int i=1; i<= NUM_MATCHES_TO_SHOW; i++) {
		for (Subtype subtype : subtypeResults.get(i).keySet()) {
			for (String gb : subtypeResults.get(i).get(subtype).keySet()) {
				Double distance = subtypeResults.get(i).get(subtype).get(gb);
				output.append(i + "-" + subtype + "-" + gb + "-" + NumberFormats.prettyDecimal(distance) + "\t");
				LOGGER.debug("Rank:" + i + " Subtype:" + subtype + " GenBank:" + gb + " Distance:" + distance);
			}
		}
	}
	return output.toString();

}*/

}