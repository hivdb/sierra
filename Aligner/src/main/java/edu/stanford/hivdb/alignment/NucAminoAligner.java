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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.IUPACMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.Sequence;

/**
 * Receives unaligned sequence objects; uses the Go program
 * NucAmino to align the input sequences and returns a list of
 * aligned sequences.
 *
 * The aligned sequence is empty when the amount of POL
 * sites below MIN_NUM_OF_SITES (50). Certain gene (PR, RT, or
 * IN) is ignored when the the length of gene sequence
 * below MIN_NUM_OF_SITES_PER_GENE.
 *
 */
public class NucAminoAligner {
	private static final Map<Gene, Integer[]> GENE_AA_RANGE;

	private static final Map<Gene, Integer> MIN_NUM_OF_SITES_PER_GENE;
	private static final int MIN_MATCH_PCNT = 60;
	private static final int SEQUENCE_SHRINKAGE_WINDOW = 15;
	private static final int SEQUENCE_SHRINKAGE_CUTOFF_PCNT = 30;
	private static final Executor executor = Executors.newFixedThreadPool(20);
	private static final Map<Strain, String[]> NUCAMINO_LOCAL_COMMANDS;

	static {
		String executable = System.getenv("NUCAMINO_PROGRAM");
		if (executable == null) {
			// use "nucamino" as default program path
			executable = "nucamino";
		}
		Map<Strain, String[]> nucaminoCommands = new EnumMap<>(Strain.class);
		nucaminoCommands.put(
			Strain.HIV1,
			new String[] {
				/* Command */
				executable,	 	// path to nucamino binary
				"align", 		// sub-command: use built-in alignment profile
				"hiv1b", 		// specify built-in profile choice
				"pol", 			// specify gene to align against

				/* Flags */
				"-q", 			// quiet mode
				"-f", "json", 	// return output format as json
			}
		);
		nucaminoCommands.put(
			Strain.HIV2A,
			new String[] {
				/* Command */
				executable,	 	// path to nucamino binary
				"align", 		// sub-command: use built-in alignment profile
				"hiv2a", 		// specify built-in profile choice
				"pol", 			// specify gene to align against

				/* Flags */
				"-q", 			// quiet mode
				"-f", "json", 	// return output format as json
			}
		);

		nucaminoCommands.put(
			Strain.HIV2B,
			new String[] {
				/* Command */
				executable,	 	// path to nucamino binary
				"align", 		// sub-command: use built-in alignment profile
				"hiv2b", 		// specify built-in profile choice
				"pol", 			// specify gene to align against

				/* Flags */
				"-q", 			// quiet mode
				"-f", "json", 	// return output format as json
			}
		);
		
		NUCAMINO_LOCAL_COMMANDS = Collections.unmodifiableMap(nucaminoCommands);
	
		/* initialize GENE_AA_RANGE */
		Map<Gene, Integer[]> geneAARange = new HashMap<>();
		geneAARange.put(Gene.valueOf("HIV1PR"), new Integer[] {
			56 + 1,
			56 + Gene.valueOf("HIV1PR").getLength()
		});
		geneAARange.put(Gene.valueOf("HIV1RT"), new Integer[] {
			geneAARange.get(Gene.valueOf("HIV1PR"))[1] + 1,
			geneAARange.get(Gene.valueOf("HIV1PR"))[1] + Gene.valueOf("HIV1RT").getLength()
		});
		geneAARange.put(Gene.valueOf("HIV1IN"), new Integer[] {
			geneAARange.get(Gene.valueOf("HIV1RT"))[1] + 1,
			geneAARange.get(Gene.valueOf("HIV1RT"))[1] + Gene.valueOf("HIV1IN").getLength()
		});

		geneAARange.put(Gene.valueOf("HIV2APR"), new Integer[] {
			85 + 1,
			85 + Gene.valueOf("HIV2APR").getLength()
		});
		geneAARange.put(Gene.valueOf("HIV2ART"), new Integer[] {
			geneAARange.get(Gene.valueOf("HIV2APR"))[1] + 1,
			geneAARange.get(Gene.valueOf("HIV2APR"))[1] + Gene.valueOf("HIV2ART").getLength()
		});
		geneAARange.put(Gene.valueOf("HIV2AIN"), new Integer[] {
			geneAARange.get(Gene.valueOf("HIV2ART"))[1] + 1,
			geneAARange.get(Gene.valueOf("HIV2ART"))[1] + Gene.valueOf("HIV2AIN").getLength()
		});

		geneAARange.put(Gene.valueOf("HIV2BPR"), new Integer[] {
			84 + 1,
			84 + Gene.valueOf("HIV2BPR").getLength()
		});
		geneAARange.put(Gene.valueOf("HIV2BRT"), new Integer[] {
			geneAARange.get(Gene.valueOf("HIV2BPR"))[1] + 1,
			geneAARange.get(Gene.valueOf("HIV2BPR"))[1] + Gene.valueOf("HIV2BRT").getLength()
		});
		geneAARange.put(Gene.valueOf("HIV2BIN"), new Integer[] {
			geneAARange.get(Gene.valueOf("HIV2BRT"))[1] + 1,
			geneAARange.get(Gene.valueOf("HIV2BRT"))[1] + Gene.valueOf("HIV2BIN").getLength()
		});
		GENE_AA_RANGE = Collections.unmodifiableMap(geneAARange);

		/* initialize minNumOfSitesPerGene */
		Map<Gene, Integer> minNumOfSitesPerGene = new HashMap<>();
		minNumOfSitesPerGene.put(Gene.valueOf("HIV1PR"), 40);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV1RT"), 60);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV1IN"), 30);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV2APR"), 40);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV2ART"), 60);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV2AIN"), 30);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV2BPR"), 40);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV2BRT"), 60);
		minNumOfSitesPerGene.put(Gene.valueOf("HIV2BIN"), 30);

		MIN_NUM_OF_SITES_PER_GENE = Collections.unmodifiableMap(minNumOfSitesPerGene);
	}

	private static class MisAlignedException extends IllegalArgumentException {
		/**
		 *
		 */
		private static final long serialVersionUID = 46495128315347L;
		private final boolean suppressible;

		public MisAlignedException(String message, boolean suppressible) {
			super(message);
			this.suppressible = suppressible;
		}

		public boolean isSuppressible() { return suppressible; }
	}

	/**
	 * Receives a sequence and aligns it to each HIV gene by NucAmino.
	 *
	 * @Param sequence
	 * @return an AlignedSequence object
	 */
	public static AlignedSequence align(Sequence sequence) {
		List<Sequence> seqs = new ArrayList<>();
		seqs.add(sequence);
		List<AlignedSequence> result = parallelAlign(seqs);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Receives set of sequences and aligns them to each HIV gene in parallel by NucAmino.
	 *
	 * @Param sequences
	 * @return list of AlignedSequence objects
	 */
	public static List<AlignedSequence> parallelAlign(Collection<Sequence> sequences) {
		return parallelAlign(sequences, false);
	}

	/**
	 * Uses locally installed NucAmino to align HIV sequences.
	 *  
	 * @param sequences
	 * @return
	 */
	private static Map<Strain, List<String>> localNucamino(Collection<Sequence> sequences) {
		Map<Strain, CompletableFuture<List<String>>> futures = new EnumMap<>(Strain.class);
		
		for (Strain strain : Strain.values()) {
			String[] cmd = NUCAMINO_LOCAL_COMMANDS.get(strain);
			CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
				List<String> jsonStrings = new ArrayList<>();
				Iterable<List<Sequence>> partialSets = Iterables.partition(sequences, 10);
				for (List<Sequence> partialSet : partialSets) {
					try {
						Process proc = Runtime.getRuntime().exec(cmd);
						OutputStream stdin = proc.getOutputStream();
						BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
						FastaUtils.writeStream(partialSet, stdin);
						jsonStrings.add(stdout.lines().collect(Collectors.joining()));
						stdout.close();
						proc.waitFor();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				return jsonStrings;
			}, executor);
			futures.put(strain, future);
		}

		CompletableFuture.allOf(futures.values().toArray(new CompletableFuture<?>[0])).join();
		
		Map<Strain, List<String>> results = new EnumMap<>(Strain.class);
		for (Strain strain : Strain.values()) {
			CompletableFuture<List<String>> future = futures.get(strain);
			try {
				results.put(strain, future.get());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		return results;
	}

	private static List<String> awsNucamino(Collection<Sequence> sequences, String awsFuncAndQual) {
		String[] funcAndQual = awsFuncAndQual.split(":");
		Iterable<List<Sequence>> partialSets = Iterables.partition(sequences, 5);
		List<CompletableFuture<String>> futures = Streams.stream(partialSets).map(partialSet -> {
			Map<String, String> payload = new HashMap<>();
			payload.put("profile", "hiv1b");
			payload.put("genes", "pol");
			payload.put("fasta", FastaUtils.writeString(partialSet));
			String payloadText = Json.dumps(payload);
			AWSLambda client = AWSLambdaClientBuilder.standard().build();
			InvokeRequest request = new InvokeRequest()
				.withFunctionName(funcAndQual[0])
				.withPayload(payloadText)
				.withQualifier(funcAndQual[1]);
			return CompletableFuture.supplyAsync(() -> {
				InvokeResult response = client.invoke(request);
				ByteBuffer respPayload = response.getPayload();
				return new String(respPayload.array(), Charset.forName("UTF-8"));
			}, executor);
		}).collect(Collectors.toList());

		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();

		return futures.stream().map(f -> {
			try {
				return f.get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}
	
	private static Map<Sequence, AlignedSequence> selectBestAlignments(
		List<AlignedSequence> newAlignments,
		Map<Sequence, AlignedSequence> knownAlignments
	) {
		for (AlignedSequence alignedSeq : newAlignments) {
			Sequence inputSeq = alignedSeq.getInputSequence();
			if (knownAlignments.containsKey(inputSeq)) {
				if (alignedSeq.isEmpty()) {
					// no overwrite
					continue;
				}
				AlignedSequence knownAlignedSeq = knownAlignments.get(inputSeq);
				// if (knownAlignedSeq.getAvailableGenes().size() < alignedSeq.getAvailableGenes().size()) {
				// 	knownAlignments.put(inputSeq, alignedSeq);
				// }
				if (knownAlignedSeq.getNumMatchedNAs() < alignedSeq.getNumMatchedNAs()) {
					knownAlignments.put(inputSeq, alignedSeq);
				}
			}
			else {
				knownAlignments.put(inputSeq, alignedSeq);
			}
		}
		return knownAlignments;
	}

	private static List<AlignedSequence> parallelAlign(Collection<Sequence> sequences, boolean reversingSequence) {
		Map<Sequence, Map<Strain, StringBuilder>> errors = new LinkedHashMap<>();
		Collection<Sequence> preparedSeqs = sequences;
		if (reversingSequence) {
			preparedSeqs = preparedSeqs.stream()
				.map(s -> s.reverseCompliment())
				.collect(Collectors.toList());
		}
		Map<Strain, List<String>> jsonStrings;
		
		// TODO: temporarily disable AWS NUCAMINO because we want to support multiple strains
		//
		// String awsFunc = System.getenv("NUCAMINO_AWS_LAMBDA");
		// if (awsFunc == null || awsFunc.equals("")) {
		// 	jsonStrings = localNucamino(preparedSeqs);
		// } else {
		// 	jsonStrings = awsNucamino(preparedSeqs, awsFunc);
		// }

		jsonStrings = localNucamino(preparedSeqs);
		
		Map<Sequence, AlignedSequence> results = new LinkedHashMap<>();
		for (Strain strain : jsonStrings.keySet()) {
			for (String jsonString : jsonStrings.get(strain)) {
				List<AlignedSequence> alignedSeqs = processCommandOutput(
					strain, sequences, jsonString,
					reversingSequence, errors
				);
				results = selectBestAlignments(alignedSeqs, results);
			}
		}
		if (!reversingSequence && !errors.isEmpty()) {
			// a second run for reverse complement

			int numStrains = Strain.values().length;
			List<Sequence> errorSeqs = errors
				.entrySet().stream()
				.filter(e -> e.getValue().size() == numStrains)
				.map(e -> e.getKey())
				.collect(Collectors.toList());
			if (!errorSeqs.isEmpty()) {
				List<AlignedSequence> reversedResults = parallelAlign(errorSeqs, true);
				results = selectBestAlignments(reversedResults, results);
			}
		}
		return Lists.newArrayList(results.values());
	}

	private static AlignedGeneSeq geneSeqFromReport(
			Sequence sequence, Gene gene, Map<?, ?> report) {
		Integer[] aaRange = GENE_AA_RANGE.get(gene);
		int aaStart = aaRange[0];
		int aaEnd = aaRange[1];
		int geneLength = gene.getLength();
		int polFirstAA = ((Double) report.get("FirstAA")).intValue();
		int polLastAA = ((Double) report.get("LastAA")).intValue();
		int firstAA = Math.max(polFirstAA - aaStart + 1, 1);
		int lastAA = Math.min(polLastAA - aaStart + 1, geneLength);
		int aaSize = Math.max(0, lastAA - firstAA + 1);
		final int minNumOfSites = MIN_NUM_OF_SITES_PER_GENE.get(gene);
		if (aaSize < minNumOfSites) {
			throw new MisAlignedException(String.format(
				"Alignment of gene %s was discarded " +
				"since the length of alignment was too short (< %d).",
				gene, minNumOfSites
			), aaSize == 0);
		}

		List<?> polAlignedSites = (List<?>) report.get("AlignedSites");
		List<AlignedSite> alignedSites = polAlignedSites.stream()
			.map(m -> (Map<?, ?>) m)
			.filter(m -> {
				int posAA = ((Double) m.get("PosAA")).intValue();
				return posAA >= aaStart && posAA <= aaEnd;
			})
			.map(m -> new AlignedSite(
				((Double) m.get("PosAA")).intValue() - aaStart + 1,
				((Double) m.get("PosNA")).intValue(),
				((Double) m.get("LengthNA")).intValue()
			))
			.collect(Collectors.toList());

		int firstNA = alignedSites.get(0).getPosNA();
		AlignedSite lastSite = alignedSites.get(alignedSites.size() - 1);
		int lastNA = lastSite.getPosNA() - 1 + lastSite.getLengthNA();

		List<?> polMutations = (List<?>) report.get("Mutations");
		List<Mutation> mutations = polMutations.stream()
			.map(m -> (Map<?, ?>) m)
			.filter(m -> {
				int posAA = ((Double) m.get("Position")).intValue();
				return posAA >= aaStart && posAA <= aaEnd;
			})
			.map(m -> IUPACMutation.fromNucAminoMutation(gene, aaStart, m))
			.collect(Collectors.toList());

		List<?> polFrameShifts = (List<?>) report.get("FrameShifts");
		List<FrameShift> frameShifts = polFrameShifts.stream()
			.map(fs -> (Map<?, ?>) fs)
			.filter(fs -> {
				int posAA = ((Double) fs.get("Position")).intValue();
				return posAA >= aaStart && posAA <= aaEnd;
			})
			.map(fs -> FrameShift.fromNucAminoFrameShift(gene, aaStart, fs))
			.collect(Collectors.toList());

		int[] trimDels = trimGaps(sequence, firstAA, lastAA, mutations, frameShifts);
		int trimDelsLeft = trimDels[0];
		int trimDelsRight = trimDels[1];

		AlignedGeneSeq geneSeq = new AlignedGeneSeq(
			sequence, gene,
			firstAA + trimDelsLeft,
			lastAA - trimDelsRight,
			firstNA + trimDelsLeft * 3,
			lastNA - trimDelsRight * 3,
			alignedSites, mutations, frameShifts, 0, 0);
		if (geneSeq.getMatchPcnt() < MIN_MATCH_PCNT) {
			throw new MisAlignedException(String.format(
				"Alignment of gene %s was discarded " +
				"since the discordance rate was too high (%.1f%% > %d%%).",
				gene, 100 - geneSeq.getMatchPcnt(), 100 - MIN_MATCH_PCNT
			), false);
		}

		int[] trimUUs = trimLowQualities(
			sequence,
			geneSeq.getFirstAA(),
			geneSeq.getLastAA(),
			geneSeq.getMutations(),
			geneSeq.getFrameShifts());
		int trimUUsLeft = trimUUs[0];
		int trimUUsRight = trimUUs[1];
		if (trimUUsLeft > 0 || trimUUsRight > 0) {
			geneSeq = new AlignedGeneSeq(
				sequence, gene,
				geneSeq.getFirstAA() + trimUUsLeft,
				geneSeq.getLastAA() - trimUUsRight,
				geneSeq.getFirstNA() + trimUUsLeft * 3,
				geneSeq.getLastNA() - trimUUsRight * 3,
				geneSeq.getAlignedSites(),
				geneSeq.getMutations(),
				geneSeq.getFrameShifts(), trimUUsLeft, trimUUsRight);
		}

		if (geneSeq.getSize() < minNumOfSites) {
			throw new MisAlignedException(String.format(
				"Alignment of gene %s was discarded " +
				"since the length of alignment (%d) was too short (< %d).",
				gene, aaSize, minNumOfSites
			), false);
		}

		return geneSeq;
	}

	/**
	 * Remove only deletion/NNNs from the beginning and the end of alignment
	 *
	 * @param sequence
	 * @param firstAA
	 * @param lastAA
	 * @param mutations
	 * @param frameShifts
	 * @return
	 */
	private static int[] trimGaps(
			Sequence sequence, int firstAA, int lastAA,
			Collection<Mutation> mutations, Collection<FrameShift> frameShifts) {
		int trimLeft = 0;
		int trimRight = 0;
		int proteinSize = lastAA - firstAA + 1;
		List<Boolean> gapSites = new ArrayList<>(Collections.nCopies(proteinSize, false));
		for (Mutation mut : mutations) {
			int idx = mut.getPosition() - firstAA;
			if (mut.isDeletion() || mut.isUnsequenced()) {
				gapSites.set(idx, true);
			}
		}
		// remove initial deletions
		for (int idx=0; idx < proteinSize; idx ++) {
			if (!gapSites.get(idx)) {
				if (idx > trimLeft) {
					trimLeft = idx;
				}
				break;
			}
		}
		// remove trailing deletions
		for (int idx=proteinSize-1; idx > -1; idx --) {
			if (!gapSites.get(idx)) {
				if (proteinSize - idx - 1 > trimRight) {
					trimRight = proteinSize - idx - 1;
				}
				break;
			}
		}
		return new int[]{trimLeft, trimRight};
	}

	/**
	 *  Input sequence may contain non-POL NAs in the beginning and the end, e.g. AF442565, KF134931.
	 *
	 * Following code did two things:
	 *
	 *   1. Remove large (length > SEQUENCE_TRIM_SITES_CUTOFF) low quality pieces from gene sequence
	 *   2. Keep small (length <= SEQUENCE_TRIM_SITES_CUTOFF) low quality pieces from gene sequence
	 *
	 * A site is considered "low quality" if it:
	 *   - is unusual mutation;
	 *   - has "X" in aas; or
	 *   - has stop codon
	 */
	private static int[] trimLowQualities(
			Sequence sequence, int firstAA, int lastAA,
			Collection<Mutation> mutations, Collection<FrameShift> frameShifts) {
		int badPcnt;
		int trimLeft = 0;
		int trimRight = 0;
		int problemSites = 0;
		int sinceLastBadQuality = 0;
		int proteinSize = lastAA - firstAA + 1;
		List<Integer> candidates = new ArrayList<>();
		List<Boolean> invalidSites = new ArrayList<>(Collections.nCopies(proteinSize, false));
		for (Mutation mut : mutations) {
			int idx = mut.getPosition() - firstAA;
			if (!mut.isUnsequenced() && (
					mut.isUnusual()
					|| mut.getDisplayAAs().equals("X") || mut.isApobecMutation() || mut.hasStop())) {
				invalidSites.set(idx, true);
			}
		}
		for (FrameShift fs : frameShifts) {
			int idx = fs.getPosition() - firstAA;
			invalidSites.set(idx,  true);
		}
		// forward scan for trimming left
		for (int idx=0; idx < proteinSize; idx ++) {
			if (sinceLastBadQuality > SEQUENCE_SHRINKAGE_WINDOW) {
				break;
			} else if (invalidSites.get(idx)) {
				problemSites ++;
				trimLeft = idx + 1;
				badPcnt = trimLeft > 0 ? problemSites * 100 / trimLeft : 0;
				if (badPcnt > SEQUENCE_SHRINKAGE_CUTOFF_PCNT) {
					candidates.add(trimLeft);
				}
				sinceLastBadQuality = 0;
			} else {
				sinceLastBadQuality ++;
			}
		}
		trimLeft = candidates.size() > 0 ? candidates.get(candidates.size() - 1) : 0;
		candidates.clear();
		// backward scan for trimming right
		problemSites = 0;
		sinceLastBadQuality = 0;
		for (int idx=proteinSize-1; idx > -1; idx --) {
			if (sinceLastBadQuality > SEQUENCE_SHRINKAGE_WINDOW) {
				break;
			} else if (invalidSites.get(idx)) {
				problemSites ++;
				trimRight = proteinSize - idx;
				badPcnt = trimRight > 0 ? problemSites * 100 / trimRight : 0;
				if (badPcnt > SEQUENCE_SHRINKAGE_CUTOFF_PCNT) {
					candidates.add(trimRight);
				}
				sinceLastBadQuality = 0;
			} else {
				sinceLastBadQuality ++;
			}
		}
		trimRight = candidates.size() > 0 ? candidates.get(candidates.size() - 1) : 0;
		return new int[]{trimLeft, trimRight};
	}

	/**
	 * Process the JSON output of NucAmino
	 * @param sequences - input unaligned sequences
	 * @param jsonString - string output of NucAmino
	 * @param errors - map of failed sequences and the errors
	 * @return list of AlignedSequence for all input sequences
	 */
	private static List<AlignedSequence> processCommandOutput(
			Strain strain, Collection<Sequence> sequences, String jsonString,
			boolean sequenceReversed, Map<Sequence, Map<Strain, StringBuilder>> errors) {

		Map<?, ?> jsonObj = Json.loads(
			jsonString, new TypeToken<Map<?, ?>>(){}.getType());
		List<?> alignmentResults = (List<?>) jsonObj.get("POL");
		List<AlignedSequence> alignedSequences = new ArrayList<>();
		Map<String, Sequence> sequenceMap = sequences.stream()
			.collect(Collectors.toMap(seq -> seq.getHeader(), seq -> seq));
		for (Object _result : alignmentResults) {
			Map<?, ?> result = (Map<?, ?>) _result;
			// TODO: should we use hash key to prevent name conflict?
			String name = (String) result.get("Name");
			Sequence sequence = sequenceMap.get(name);
			Map<?, ?> report = (Map<?, ?>) result.get("Report");
			Map<Gene, AlignedGeneSeq> alignedGeneSeqs = new TreeMap<>();
			Map<Gene, String> discardedGenes = new LinkedHashMap<>();
			String error = (String) result.get("Error");
			if (!error.isEmpty()) {
				errors.putIfAbsent(sequence, new EnumMap<>(Strain.class));
				errors.get(sequence).putIfAbsent(strain, new StringBuilder());
				errors.get(sequence).get(strain).append(error);
			} else {
				for (Gene gene : Gene.values(strain)) {
					try {
						alignedGeneSeqs.put(gene, geneSeqFromReport(sequence, gene, report));
					} catch (MisAlignedException e) {
						if (!e.isSuppressible()) {
							discardedGenes.put(gene, e.getMessage());
						}
					}
				}
				if (alignedGeneSeqs.isEmpty()) {
					errors.putIfAbsent(sequence, new EnumMap<>(Strain.class));
					errors.get(sequence).putIfAbsent(strain, new StringBuilder());
					errors.get(sequence).get(strain).append("No aligned results were found.");
				}
			}
			alignedSequences.add(
				new AlignedSequence(
					strain, sequence, alignedGeneSeqs,
					discardedGenes, sequenceReversed)
			);
		}
		return alignedSequences;

		// AlignmentExtension extResult = new AlignmentExtension(
		// 	sequence, gene, firstAA, lastAA, firstNA, lastNA,
		// 	alignResult.getAlignedNAs(),
		// 	alignResult.getControlLine(),
		// 	alignResult.getAATripletLine());
	}

}
