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

package edu.stanford.hivdb.web;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;

import edu.stanford.hivdb.comments.BoundComment;
import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.ASIDrugSusc;
import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.genotypes.BoundGenotype;
import edu.stanford.hivdb.genotypes.GenotypeResult;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.MutationType;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.CodonUtils;
import edu.stanford.hivdb.utilities.NumberFormats;


/**
 *
 */
public class XmlOutput<VirusT extends Virus<VirusT>> {
	private final DrugResistanceAlgorithm<VirusT> LATEST_ALG;
	private final String WEB_SERVICE_VERSION = "2.0";
	private final String SCHEMA_VERSION = "1.1";
	private final String SUBMISSION_NAME = "";

	private final Document document;

	private final List<AlignedSequence<VirusT>> alignedSequences;
	private final List<Map<Gene<VirusT>, GeneDR<VirusT>>> allResistanceResults;

	// Known differences with old XML output:
	// 1. <algorithmDate /> is removed due to not defined in 1.1 schema
	// 2. <comment />'s attribute id is removed due to not defined in 1.1 schema
	public XmlOutput(
			final VirusT virusIns,
			final List<AlignedSequence<VirusT>> alignmentSequences,
			final List<Map<Gene<VirusT>, GeneDR<VirusT>>> allResistanceResults) {
		LATEST_ALG = virusIns.getLatestDrugResistAlgorithm("HIVDB");
		this.alignedSequences = alignmentSequences;
		this.allResistanceResults = allResistanceResults;
		this.document = createDocument();
		this.document.appendChild(createRootElement());
	}

	@Override
	public String toString() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	private static Document createDocument() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			return docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private Element newSimpleElement(String tag, String text) {
		Element elem = document.createElement(tag);
		elem.appendChild(document.createTextNode(text));
		return elem;
	}

	private Element newSimpleElement(String tag, boolean bool) {
		return newSimpleElement(tag, "" + bool);
	}

	private Element newSimpleElement(String tag, int number) {
		return newSimpleElement(tag, "" + number);
	}

	private Element createRootElement() {
		Element rootElement = document.createElement("DrugResistance_Interpretation");
		rootElement.setAttributeNS(
			"http://www.w3.org/2001/XMLSchema-instance",
			"xsi:noNamespaceSchemaLocation",
			"http://hivdb.stanford.edu/DR/schema/sierra.xsd");

		// element name="algorithmName" type="xs:string"
		rootElement.appendChild(
			newSimpleElement("algorithmName", LATEST_ALG.getFamily()));

		// name="algorithmVersion" type="xs:string"
		rootElement.appendChild(
			newSimpleElement("algorithmVersion", LATEST_ALG.getVersion()));

		// algorithmDate is not in schema
		// rootElement.appendChild(
		// 	newSimpleElement("algorithmDate", ALG_DATE));

		// name="webServiceVersion" type="xs:string"
		rootElement.appendChild(
			newSimpleElement("webServiceVersion", WEB_SERVICE_VERSION));

		// name="schemaVersion" type="xs:string"
		rootElement.appendChild(
			newSimpleElement("schemaVersion", SCHEMA_VERSION));

		// name="submissionName" type="xs:string" minOccurs="0" maxOccurs="1"
		rootElement.appendChild(
			newSimpleElement("submissionName", SUBMISSION_NAME));

		// name="dateTime" type="xs:string" minOccurs="0" maxOccurs="1"
		LocalDate today = LocalDate.now();
		LocalTime time = LocalTime.now();
		String dateTime = today.toString() + " " + time.toString();

		rootElement.appendChild(
			newSimpleElement("dateTime", dateTime));

		// name="result" type="Result" minOccurs="0" maxOccurs="unbounded"
		for (int i=0; i < alignedSequences.size(); i ++) {
			rootElement.appendChild(
				createResultElement(
					alignedSequences.get(i),
					allResistanceResults.get(i)
				)
			);
		}

		return rootElement;
	}

	private Element createInputSequenceElement(Sequence seq) {
		String seqName = seq.getHeader();
		String seqText = seq.getSequence();

		Element inputSequence = document.createElement("inputSequence");

		// name="md5sum" type="xs:string"
		inputSequence.appendChild(
			newSimpleElement("md5sum", seq.getMD5()));

		// name="name" type="xs:string" minOccurs="0" maxOccurs="1"
		inputSequence.appendChild(
			newSimpleElement("name", seqName));

		// name="sequence" type="xs:string"
		inputSequence.appendChild(
			newSimpleElement("sequence", seqText));

		return inputSequence;
	}

	private Element createSubtypeElement(GenotypeResult<VirusT> genotypeResult) {
		Element subtype = document.createElement("subtype");
		BoundGenotype<VirusT> bestMatch = genotypeResult.getBestMatch();

		// name="type" type="xs:string"
		subtype.appendChild(
			newSimpleElement("type", bestMatch.getDisplay()));

		// name="percentSimilarity" type="xs:float"
		subtype.appendChild(
			newSimpleElement(
				"percentSimilarity",
				NumberFormats.prettyDecimalAsString(
					100 - 100 * bestMatch.getDistance())
			)
		);

		return subtype;
	}

	private Element createMutationElement(Mutation<VirusT> mut) {
		MutationType<VirusT> mutType = mut.getPrimaryType();

		Element mutElem = document.createElement("mutation");

		// name="classification" type="MutationClassification"
		mutElem.appendChild(
			newSimpleElement(
				"classification",
				mutType.getName())
		);

		// name="type" type="MutationType"
		String typeText = "mutation";
		if (mut.isInsertion()) {
			typeText = "insertion";
		} else if (mut.isDeletion()) {
			typeText = "deletion";
		}
		mutElem.appendChild(newSimpleElement("type", typeText));
		// name="mutationString" type="xs:string"
		mutElem.appendChild(
			newSimpleElement("mutationString", mut.getHumanFormat()));
		// name="wildType" type="xs:string"
		mutElem.appendChild(newSimpleElement("wildType", mut.getReference()));
		// name="position" type="xs:nonNegativeInteger"
		mutElem.appendChild(newSimpleElement("position", mut.getPosition()));
		// name="nucleicAcid" type="xs:string" minOccurs="0" maxOccurs="1"
		mutElem.appendChild(newSimpleElement("nucleicAcid", mut.getTriplet()));
		// name="translatedNA" type="xs:string"
		mutElem.appendChild(
			newSimpleElement("translatedNA", mut.getDisplayAAs().split("_")[0]));

		if (mut.isInsertion()) {
			// name="insertionString" type="xs:string" minOccurs="0" maxOccurs="1"
			mutElem.appendChild(
				newSimpleElement("insertionString", mut.getDisplayAAs()));
			// name="insertionNucleicAcid" type="xs:string" minOccurs="0" maxOccurs="1"
			mutElem.appendChild(
				newSimpleElement(
					"insertionNucleicAcid", mut.getInsertedNAs()));
			// name="insertionTranslatedNA" type="xs:string" minOccurs="0" maxOccurs="1"
			mutElem.appendChild(
				newSimpleElement(
					"insertionTranslatedNA", mut.getDisplayAAs().split("_")[1]));
		}

		// name="atypical" type="xs:boolean" minOccurs="0" maxOccurs="1"
		if (mut.isUnusual()) {
			mutElem.appendChild(
				newSimpleElement("atypical", mut.isUnusual()));
		}

		// name="GAHypermutated" type="xs:boolean" minOccurs="0" maxOccurs="1"
		if (mut.isApobecMutation()) {
			mutElem.appendChild(
				newSimpleElement("GAHypermutated", mut.isApobecMutation()));
		}

		return mutElem;
	}

	private Element createFrameShiftElement(FrameShift<VirusT> fs) {
		Element frameShift = document.createElement("frameshift");

		// name="position" type="xs:nonNegativeInteger"
		frameShift.appendChild(
			newSimpleElement("position", fs.getPosition()));

		// name="length" type="xs:nonNegativeInteger"
		frameShift.appendChild(
			newSimpleElement("length", fs.getSize()));

		// name="rawAlignBlock" type="xs:string"
		frameShift.appendChild(
			newSimpleElement("rawAlignBlock", "not available"));

		return frameShift;
	}

	private Element createQualityElement(AlignedGeneSeq<VirusT> alignedGeneSeq) {
		Element quality = document.createElement("quality");

		// name="frameshift" type="Frameshift" minOccurs="0" maxOccurs="unbounded"
		for (FrameShift<VirusT> fs : alignedGeneSeq.getFrameShifts()) {
			quality.appendChild(createFrameShiftElement(fs));
		}
		// name="ambiguous" type="xs:nonNegativeInteger" minOccurs="0" maxOccurs="unbounded"
		for (Mutation<VirusT> mut : alignedGeneSeq.getHighlyAmbiguousCodons()) {
			quality.appendChild(newSimpleElement("ambiguous", mut.getPosition()));
		}

		// name="stop" type="xs:nonNegativeInteger" minOccurs="0" maxOccurs="unbounded"
		for (Mutation<VirusT> mut : alignedGeneSeq.getStopCodons()) {
			quality.appendChild(newSimpleElement("stop", mut.getPosition()));
		}

		// name="stopAndFrameshiftPositions" type="xs:string" minOccurs="0" maxOccurs="1"
		// quality.appendChild(
		// 	newSimpleElement("stopAndFrameShiftPositions", "Not available"));

		// name="GAHypermutatedPositions" type="xs:string" minOccurs="0" maxOccurs="1"
		MutationSet<VirusT> apobecMuts = alignedGeneSeq.getMutations().getApobecMutations();
		if (!apobecMuts.isEmpty()) {
			quality.appendChild(
				newSimpleElement("GAHypermutatedPositions", apobecMuts.join()));
		}

		// name="href" type="xs:string" minOccurs="0" maxOccurs="1"

		// name="criteriaFailed" type="xs:string" minOccurs="0" maxOccurs="unbounded"
		// for (ValidationResult result : alignedGeneSeq.getValidationResults()) {
		// 	quality.appendChild(newSimpleElement("criteriaFailed", result.toString()));
		// }

		return quality;
	}

	private Element createGeneDataElement(
			Gene<VirusT> gene, AlignedGeneSeq<VirusT> alignedGeneSeq,
			GeneDR<VirusT> geneDR, GenotypeResult<VirusT> genotypeResult) {
		Element geneData = document.createElement("geneData");

		// name="gene" type="xs:string"
		geneData.appendChild(newSimpleElement("gene", gene.getName()));

		boolean isPresent = alignedGeneSeq != null;
		// name="present" type="xs:boolean"
		geneData.appendChild(newSimpleElement("present", isPresent));
		if (!isPresent) {
			return geneData;
		}

		String naSeq = alignedGeneSeq.getAlignedNAs();
		String aaSeq = CodonUtils.simpleTranslate(naSeq);

		// name="consensus" type="xs:string" minOccurs="0" maxOccurs="1"
		geneData.appendChild(
			newSimpleElement("consensus", gene.getRefSequence()));

		// name="alignedNASequence" type="xs:string" minOccurs="0" maxOccurs="1"
		geneData.appendChild(newSimpleElement("alignedNASequence", naSeq));

		// name="alignedAASequence" type="xs:string" minOccurs="0" maxOccurs="1"
		geneData.appendChild(newSimpleElement("alignedAASequence", aaSeq));

		// name="firstAA" type="xs:nonNegativeInteger" minOccurs="0" maxOccurs="1"
		geneData.appendChild(
			newSimpleElement("firstAA", alignedGeneSeq.getFirstAA()));

		// name="lastAA" type="xs:nonNegativeInteger" minOccurs="0" maxOccurs="1"
		geneData.appendChild(
			newSimpleElement("lastAA", alignedGeneSeq.getLastAA()));

		// name="subtype" type="Subtype" minOccurs="0" maxOccurs="1"
		geneData.appendChild(createSubtypeElement(genotypeResult));

		// name="mutation" type="Mutation" minOccurs="0" maxOccurs="unbounded"
		for (Mutation<VirusT> mutation : alignedGeneSeq.getMutations()) {
			geneData.appendChild(createMutationElement(mutation));
		}

		// name="quality" type="QualityAnalysis" minOccurs="0" maxOccurs="1"
		geneData.appendChild(createQualityElement(alignedGeneSeq));

		return geneData;
	}

	private Element createSequenceQualityCountsElement(
		AlignedSequence<VirusT> alignedSeq
	) {

		Element sequenceQualityCounts =
			document.createElement("sequenceQualityCounts");

		// name="insertions" type="xs:nonNegativeInteger"
		int insCount = alignedSeq
			.getAlignedGeneSequences().stream()
			.mapToInt(aligned -> aligned.getInsertions().size()).sum();
		sequenceQualityCounts.appendChild(
			newSimpleElement("insertions", insCount));

		// name="deletions" type="xs:nonNegativeInteger"
		int delCount = alignedSeq
			.getAlignedGeneSequences().stream()
			.mapToInt(aligned -> aligned.getDeletions().size()).sum();
		sequenceQualityCounts.appendChild(
			newSimpleElement("deletions", delCount));

		// name="ambiguous" type="xs:nonNegativeInteger"
		int abgCount = alignedSeq
			.getAlignedGeneSequences().stream()
			.mapToInt(aligned -> aligned.getHighlyAmbiguousCodons().size())
			.sum();
		sequenceQualityCounts.appendChild(
			newSimpleElement("ambiguous", abgCount));

		// name="stops" type="xs:nonNegativeInteger"
		int stopCount = alignedSeq
			.getAlignedGeneSequences().stream()
			.mapToInt(aligned -> aligned.getStopCodons().size()).sum();
		sequenceQualityCounts.appendChild(
			newSimpleElement("stops", stopCount));

		// name="frameshifts" type="xs:nonNegativeInteger"
		int fsCount = alignedSeq
			.getAlignedGeneSequences().stream()
			.mapToInt(aligned -> aligned.getFrameShifts().size()).sum();
		sequenceQualityCounts.appendChild(
			newSimpleElement("frameshifts", fsCount));

		return sequenceQualityCounts;
	}

	private Element createPartialScore(Collection<Mutation<VirusT>> muts, Double mutScore) {
		Element partialScore = document.createElement("partialScore");

		// name="mutation" type="xs:string" minOccurs="1" maxOccurs="unbounded"
		for (Mutation<VirusT> mut : muts) {
			partialScore.appendChild(
				newSimpleElement("mutation", mut.getHumanFormat()));
		}

		// name="score" type="xs:float" minOccurs="0" maxOccurs="1"
		partialScore.appendChild(
			newSimpleElement("score", mutScore.intValue()));

		return partialScore;
	}

	private Element createDrugScoreElement(Drug<VirusT> drug, GeneDR<VirusT> geneDR) {
		Element drugScore = document.createElement("drugScore");
		ASIDrugSusc<VirusT> drugSusc = geneDR.getDrugSusc(drug);

		// name="drugCode" type="xs:string"
		drugScore.appendChild(
			newSimpleElement("drugCode", drug.getDisplayAbbr()));

		// name="genericName" type="xs:string"
		drugScore.appendChild(
			newSimpleElement("genericName", drug.getFullName().toString()));

		// name="type" type="xs:string"
		drugScore.appendChild(
			newSimpleElement("type", drug.getDrugClass().toString()));

		// name="score" type="xs:float" minOccurs="0" maxOccurs="1"
		drugScore.appendChild(
			newSimpleElement("score", drugSusc.getScore().intValue()));

		// name="resistanceLevel" type="xs:float" minOccurs="0" maxOccurs="1"
		drugScore.appendChild(newSimpleElement(
			"resistanceLevel", drugSusc.getLevel()));

		// name="resistanceLevelText" type="xs:string" minOccurs="0" maxOccurs="1"
		drugScore.appendChild(newSimpleElement(
			"resistanceLevelText", drugSusc.getLevelText()));

		// name="threeStepResistanceLevel" type="xs:string"
		drugScore.appendChild(newSimpleElement(
			"threeStepResistanceLevel", drugSusc.getSIR().toString()));

		// name="partialScore" type="PartialScore" minOccurs="0" maxOccurs="unbounded"
		for (
			Pair<MutationSet<VirusT>, Double> pair :
			geneDR.getDrugSusc(drug).getParialScorePairs()
		) {
			MutationSet<VirusT> mut = pair.getKey();
			Double mutScore = pair.getValue();
			drugScore.appendChild(
				createPartialScore(Lists.newArrayList(mut), mutScore));
		}
		return drugScore;
	}

	private Element createScoreRowElement(List<Map<String, String>> cols) {
		Element scoreRow = document.createElement("scoreRow");

		// name="score" type="ScoreCell" minOccurs="1" maxOccurs="unbounded"
		for (Map<String, String>col : cols) {
			Element scoreCol = document.createElement("score");
			for (Map.Entry<String, String> entry : col.entrySet()) {
				scoreCol.setAttribute(entry.getKey(), entry.getValue());
			}
			scoreRow.appendChild(scoreCol);
		}
		return scoreRow;
	}

	private Element createScoreRowElement(DrugClass<VirusT> drugClass) {
		Collection<Drug<VirusT>> drugs = drugClass.getDrugs();
		List<Map<String, String>> cols = new ArrayList<>();
		Map<String, String> titleCol = new LinkedHashMap<>();

		// name="value" type="xs:string"
		titleCol.put("value", drugClass.toString());
		cols.add(titleCol);

		for (Drug<VirusT> drug : drugs) {
			Map<String, String> col = new LinkedHashMap<>();
			// name="value" type="xs:string"
			col.put("value", drug.getDisplayAbbr());
			cols.add(col);
		}
		return createScoreRowElement(cols);
	}

	private Element createScoreRowElement(
		String title,
		DrugClass<VirusT> drugClass,
		Function<Drug<VirusT>, Double> scoreGetter
		
	) {
		Collection<Drug<VirusT>> drugs = drugClass.getDrugs();
		List<Map<String, String>> cols = new ArrayList<>();
		Map<String, String> titleCol = new LinkedHashMap<>();

		// name="value" type="xs:string"
		titleCol.put("value", title);
		cols.add(titleCol);

		for (Drug<VirusT> drug: drugs) {
			Map<String, String> col = new LinkedHashMap<>();
			Double score = scoreGetter.apply(drug);
			// name="value" type="xs:string"
			col.put("value", "" + score.intValue());
			// name="class" type="xs:string" use="optional"
			col.put("class", drugClass.toString());
			// name="drug" type="xs:string" use="optional"
			col.put("drug", drug.getDisplayAbbr());
			cols.add(col);
		}
		return createScoreRowElement(cols);
	}

	private Element createScoreTableElement(DrugClass<VirusT> drugClass, GeneDR<VirusT> geneDR) {
		Element scoreTable = document.createElement("scoreTable");

		// header
		scoreTable.appendChild(createScoreRowElement(drugClass));

		// body for partial scores
		Set<MutationSet<VirusT>> scoredMuts = geneDR.getScoredMutations(ds -> ds.drugClassIs(drugClass));
		for (MutationSet<VirusT> muts : scoredMuts) {
			scoreTable.appendChild(createScoreRowElement(
				muts.join('+'), drugClass,
				drug -> geneDR.getDrugSusc(drug).getPartialScore(muts)
			));
		}

		// body for total score
		scoreTable.appendChild(createScoreRowElement(
			"Total:",
			drugClass,
			drug -> geneDR.getDrugSusc(drug).getScore()
		));

		return scoreTable;
	}

	private Element createCommentElement(Mutation<VirusT> mut, BoundComment<VirusT> bc) {
		Element comment = document.createElement("comment");
		// name="gene" type="xs:string"
		comment.appendChild(newSimpleElement("gene", mut.getGene().getName()));

		// name="grouping" type="xs:string" minOccurs="0" maxOccurs="1"
		comment.appendChild(
			newSimpleElement("grouping", bc.getType().toString()));

		// name="position" type="xs:nonNegativeInteger"
		comment.appendChild(newSimpleElement("position", mut.getPosition()));

		// name="commentString" type="xs:string"
		comment.appendChild(newSimpleElement("commentString", bc.getText()));

		// name="mutationString" type="xs:string"
		comment.appendChild(newSimpleElement(
			"mutationString", mut.getHumanFormat()));

		return comment;
	}

	private Element createResultElement(
			AlignedSequence<VirusT> alignedSeq,
			Map<Gene<VirusT>, GeneDR<VirusT>> resistanceResults) {

		Element result = document.createElement("result");

		// name="success" type="xs:boolean"
		boolean isSuccess =
			alignedSeq != null &&
			!alignedSeq.isEmpty();
		result.appendChild(newSimpleElement("success", isSuccess));

		// name="inputSequence" type="InputSequence" minOccurs="0" maxOccurs="1"
		result.appendChild(createInputSequenceElement(alignedSeq.getInputSequence()));

		// name="errorMessage" type="xs:string" minOccurs="0" maxOccurs="1"
		if (!isSuccess) {
			result.appendChild(
				newSimpleElement(
					"errorMessage",
					"There were no Protease, Reverse Transcriptase, or " +
					"Integrase genes found, refuse to process (sequence " +
					String.format(
						"length = %s).", alignedSeq.getInputSequence().getSequence().length())
				)
			);
			return result;
		}

		GenotypeResult<VirusT> genotypeResult = alignedSeq.getGenotypeResult();
		int numApobec = alignedSeq.getMutations().getApobecMutations().size();

		// name="GAHypermutated" type="xs:boolean" minOccurs="0" maxOccurs="1"
		result.appendChild(
			newSimpleElement("GAHypermutated", numApobec > 1));

		// name="geneData" type="GeneData" minOccurs="0" maxOccurs="3"
		for (Gene<VirusT> gene : alignedSeq.getAvailableGenes()) {
			result.appendChild(createGeneDataElement(
				gene, alignedSeq.getAlignedGeneSequence(gene),
				resistanceResults.get(gene), genotypeResult));
		}

		// name="sequenceQualityCounts" type="SequenceQualityCounts" minOccurs="0" maxOccurs="1"
		result.appendChild(
			createSequenceQualityCountsElement(alignedSeq));

		// name="drugScore" type="DrugScore" minOccurs="0" maxOccurs="unbounded"
		for (Gene<VirusT> gene : resistanceResults.keySet()) {
			GeneDR<VirusT> geneDR = resistanceResults.get(gene);
			for (DrugClass<VirusT> drugClass : gene.getDrugClasses()) {
				for (Drug<VirusT> drug : drugClass.getDrugs()) {
					result.appendChild(createDrugScoreElement(drug, geneDR));
				}
			}
		}

		// name="scoreTable" type="ScoreTable" minOccurs="0" maxOccurs="unbounded"
		for (Gene<VirusT> gene : resistanceResults.keySet()) {
			GeneDR<VirusT> geneDR = resistanceResults.get(gene);
			for (DrugClass<VirusT> drugClass : gene.getDrugClasses()) {
				result.appendChild(createScoreTableElement(drugClass, geneDR));
			}
		}

		// name="comment" type="MutationComment" minOccurs="0" maxOccurs="unbounded"
		for (Gene<VirusT> gene : resistanceResults.keySet()) {
			GeneDR<VirusT> geneDR = resistanceResults.get(gene);
			for (Map.Entry<CommentType, List<BoundComment<VirusT>>> entry :
					geneDR.groupCommentsByTypes().entrySet()) {
				for (BoundComment<VirusT> bc : entry.getValue()) {
					if (bc.getType() == CommentType.Dosage) {
						continue;
					}
					Mutation<VirusT> mut = bc.getBoundMutation();
					result.appendChild(createCommentElement(mut, bc));
				}
			}

		}

		return result;
	}

}
