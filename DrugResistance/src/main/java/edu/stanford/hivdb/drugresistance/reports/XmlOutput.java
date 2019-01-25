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

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.database.CommentType;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.BoundComment;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.CodonTranslation;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.utilities.NumberFormats;
import edu.stanford.hivdb.utilities.Sequence;


/**
 *
 */
public class XmlOutput {
	private static final String ALG_NAME = "HIVDB";
	private static final String ALG_VERSION;
	// private static final String ALG_DATE;
	private static final String WEB_SERVICE_VERSION = "2.0";
	private static final String SCHEMA_VERSION = "1.1";
	private static final String SUBMISSION_NAME = "";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map <Gene, Map<MutType, String>> mutClassificationConversion;

	static {
		final HivdbVersion curVersion = HivdbVersion.getLatestVersion();
		ALG_VERSION = curVersion.readableVersion;
		// ALG_DATE = curVersion.versionDate;
		Map<Gene, Map<MutType, String>> tmpMap = new TreeMap<>();
		for (Gene gene : Gene.values(Strain.HIV1)) {
			tmpMap.put(gene, new EnumMap<>(MutType.class));
		}
		tmpMap.get(Gene.valueOf("HIV1PR")).put(MutType.Major, "PI_MAJOR");
		tmpMap.get(Gene.valueOf("HIV1PR")).put(MutType.Accessory, "PI_MINOR");
		tmpMap.get(Gene.valueOf("HIV1PR")).put(MutType.Other, "OTHER");
		tmpMap.get(Gene.valueOf("HIV1RT")).put(MutType.NRTI, "NRTI");
		tmpMap.get(Gene.valueOf("HIV1RT")).put(MutType.NNRTI, "NNRTI");
		tmpMap.get(Gene.valueOf("HIV1RT")).put(MutType.Other, "OTHER");
		tmpMap.get(Gene.valueOf("HIV1IN")).put(MutType.Major, "INI_MAJOR");
		tmpMap.get(Gene.valueOf("HIV1IN")).put(MutType.Accessory, "INI_MINOR");
		tmpMap.get(Gene.valueOf("HIV1IN")).put(MutType.Other, "OTHER");
		mutClassificationConversion = tmpMap;
	}

	private final Document document;

	private final List<AlignedSequence> alignedSequences;
	private final List<Map<Gene, GeneDR>> allResistanceResults;

	// Known differences with old XML output:
	// 1. <algorithmDate /> is removed due to not defined in 1.1 schema
	// 2. <comment />'s attribute id is removed due to not defined in 1.1 schema
	public XmlOutput(
			final List<AlignedSequence> alignmentSequences,
			final List<Map<Gene, GeneDR>> allResistanceResults) {
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
			newSimpleElement("algorithmName", ALG_NAME));

		// name="algorithmVersion" type="xs:string"
		rootElement.appendChild(
			newSimpleElement("algorithmVersion", ALG_VERSION));

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
			newSimpleElement("md5sum", DigestUtils.md5Hex(seqText)));

		// name="name" type="xs:string" minOccurs="0" maxOccurs="1"
		inputSequence.appendChild(
			newSimpleElement("name", seqName));

		// name="sequence" type="xs:string"
		inputSequence.appendChild(
			newSimpleElement("sequence", seqText));

		return inputSequence;
	}

	private Element createSubtypeElement(HIVGenotypeResult genotypeResult) {
		Element subtype = document.createElement("subtype");
		BoundGenotype bestMatch = genotypeResult.getBestMatch();

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

	private Element createMutationElement(Mutation mut) {
		Gene gene = mut.getGene();
		MutType mutType = mut.getPrimaryType();

		Element mutElem = document.createElement("mutation");

		// name="classification" type="MutationClassification"
		mutElem.appendChild(
			newSimpleElement(
				"classification",
				mutClassificationConversion.get(gene).get(mutType)));

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

	private Element createFrameShiftElement(FrameShift fs) {
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

	private Element createQualityElement(AlignedGeneSeq alignedGeneSeq) {
		Element quality = document.createElement("quality");

		// name="frameshift" type="Frameshift" minOccurs="0" maxOccurs="unbounded"
		for (FrameShift fs : alignedGeneSeq.getFrameShifts()) {
			quality.appendChild(createFrameShiftElement(fs));
		}
		// name="ambiguous" type="xs:nonNegativeInteger" minOccurs="0" maxOccurs="unbounded"
		for (Mutation mut : alignedGeneSeq.getHighlyAmbiguousCodons()) {
			quality.appendChild(newSimpleElement("ambiguous", mut.getPosition()));
		}

		// name="stop" type="xs:nonNegativeInteger" minOccurs="0" maxOccurs="unbounded"
		for (Mutation mut : alignedGeneSeq.getStopCodons()) {
			quality.appendChild(newSimpleElement("stop", mut.getPosition()));
		}

		// name="stopAndFrameshiftPositions" type="xs:string" minOccurs="0" maxOccurs="1"
		// quality.appendChild(
		// 	newSimpleElement("stopAndFrameShiftPositions", "Not available"));

		// name="GAHypermutatedPositions" type="xs:string" minOccurs="0" maxOccurs="1"
		MutationSet apobecMuts =
			new Apobec(alignedGeneSeq.getMutations()).getApobecMuts();
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
			Gene gene, AlignedGeneSeq alignedGeneSeq,
			GeneDR geneDR, HIVGenotypeResult genotypeResult) {
		Element geneData = document.createElement("geneData");

		// name="gene" type="xs:string"
		geneData.appendChild(newSimpleElement("gene", gene.getShortName()));

		boolean isPresent = alignedGeneSeq != null;
		// name="present" type="xs:boolean"
		geneData.appendChild(newSimpleElement("present", isPresent));
		if (!isPresent) {
			return geneData;
		}

		String naSeq = alignedGeneSeq.getAlignedNAs();
		String aaSeq = CodonTranslation.simpleTranslate(naSeq);

		// name="consensus" type="xs:string" minOccurs="0" maxOccurs="1"
		geneData.appendChild(
			newSimpleElement("consensus", gene.getReference()));

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
		for (Mutation mutation : alignedGeneSeq.getMutations()) {
			geneData.appendChild(createMutationElement(mutation));
		}

		// name="quality" type="QualityAnalysis" minOccurs="0" maxOccurs="1"
		geneData.appendChild(createQualityElement(alignedGeneSeq));

		return geneData;
	}

	private Element createSequenceQualityCountsElement(
			AlignedSequence alignedSeq) {

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

	private Element createPartialScore(Mutation[] muts, Double mutScore) {
		Element partialScore = document.createElement("partialScore");

		// name="mutation" type="xs:string" minOccurs="1" maxOccurs="unbounded"
		for (Mutation mut : muts) {
			partialScore.appendChild(
				newSimpleElement("mutation", mut.getHumanFormat()));
		}

		// name="score" type="xs:float" minOccurs="0" maxOccurs="1"
		partialScore.appendChild(
			newSimpleElement("score", mutScore.intValue()));

		return partialScore;
	}

	private Element createDrugScoreElement(Drug drug, GeneDR geneDR) {
		Element drugScore = document.createElement("drugScore");

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
			newSimpleElement("score", geneDR.getTotalDrugScore(drug).intValue()));

		// name="resistanceLevel" type="xs:float" minOccurs="0" maxOccurs="1"
		drugScore.appendChild(newSimpleElement(
			"resistanceLevel", geneDR.getDrugLevel(drug)));

		// name="resistanceLevelText" type="xs:string" minOccurs="0" maxOccurs="1"
		drugScore.appendChild(newSimpleElement(
			"resistanceLevelText", geneDR.getDrugLevelText(drug)));

		// name="threeStepResistanceLevel" type="xs:string"
		drugScore.appendChild(newSimpleElement(
			"threeStepResistanceLevel", geneDR.getDrugLevelSIR(drug)));

		// name="partialScore" type="PartialScore" minOccurs="0" maxOccurs="unbounded"
		if (geneDR.drugHasScoredIndividualMuts(drug)) {
			for (Map.Entry<Mutation, Double> entry :
					geneDR.getScoredIndividualMutsForDrug(drug).entrySet()) {
				Mutation mut = entry.getKey();
				Double mutScore = entry.getValue();
				drugScore.appendChild(
					createPartialScore(new Mutation[]{mut}, mutScore));
			}
		}

		if (geneDR.drugHasScoredComboMuts(drug)) {
			for (Map.Entry<MutationSet, Double> entry :
					geneDR.getScoredComboMutsForDrug(drug).entrySet()) {
				MutationSet comboMuts = entry.getKey();
				Double comboMutScore = entry.getValue();
				drugScore.appendChild(createPartialScore(
					comboMuts.toArray(new Mutation[0]), comboMutScore));
			}
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

	private Element createScoreRowElement(DrugClass drugClass) {
		List<Drug> drugs = drugClass.getDrugsForHivdbTesting();
		List<Map<String, String>> cols = new ArrayList<>();
		Map<String, String> titleCol = new LinkedHashMap<>();

		// name="value" type="xs:string"
		titleCol.put("value", drugClass.toString());
		cols.add(titleCol);

		for (Drug drug : drugs) {
			Map<String, String> col = new LinkedHashMap<>();
			// name="value" type="xs:string"
			col.put("value", drug.getDisplayAbbr());
			cols.add(col);
		}
		return createScoreRowElement(cols);
	}

	private Element createScoreRowElement(
			String title,
			DrugClass drugClass,
			Map<Drug, Double> drugScores,
			Map<String, String> addAttrs) {

		List<Drug> drugs = drugClass.getDrugsForHivdbTesting();
		List<Map<String, String>> cols = new ArrayList<>();
		Map<String, String> titleCol = new LinkedHashMap<>();

		// name="value" type="xs:string"
		titleCol.put("value", title);
		titleCol.putAll(addAttrs);
		cols.add(titleCol);

		for (Drug drug: drugs) {
			Map<String, String> col = new LinkedHashMap<>();
			Double score = drugScores.getOrDefault(drug, 0.0);
			// name="value" type="xs:string"
			col.put("value", "" + score.intValue());
			// name="class" type="xs:string" use="optional"
			col.put("class", drugClass.toString());
			// name="drug" type="xs:string" use="optional"
			col.put("drug", drug.getDisplayAbbr());
			col.putAll(addAttrs);
			cols.add(col);
		}
		return createScoreRowElement(cols);
	}

	private Element createScoreRowElement(
			Mutation mut, DrugClass drugClass,
			Map<Drug, Double> drugScores) {

		int pos = mut.getPosition();
		Map<String, String> addAttrs = new LinkedHashMap<>();
		// name="pos" type="xs:nonNegativeInteger" use="optional"
		addAttrs.put("pos", "" + pos);

		return createScoreRowElement(
				mut.getHumanFormat(), drugClass, drugScores, addAttrs);
	}

	private Element createScoreRowElement(
			MutationSet muts, DrugClass drugClass,
			Map<Drug, Double> drugScores) {

		return createScoreRowElement(
				muts.join('+'), drugClass, drugScores, new LinkedHashMap<>());
	}

	private Element createScoreTableElement(DrugClass drugClass, GeneDR geneDR) {
		Element scoreTable = document.createElement("scoreTable");

		// header
		scoreTable.appendChild(createScoreRowElement(drugClass));

		// body for individual muts
		if (geneDR.drugClassHasScoredIndividualMuts(drugClass)) {
			for (Map.Entry<Mutation, Map<Drug, Double>> entry :
					geneDR
					.getIndividualMutAllDrugScoresForDrugClass(drugClass)
					.entrySet()) {
				scoreTable.appendChild(createScoreRowElement(
					entry.getKey(), drugClass, entry.getValue()));
			}
		}

		// body for combo muts
		if (geneDR.drugClassHasScoredComboMuts(drugClass)) {
			for (Map.Entry<MutationSet, Map<Drug, Double>> entry :
					geneDR
					.getComboMutAllDrugScoresForDrugClass(drugClass)
					.entrySet()) {
				scoreTable.appendChild(createScoreRowElement(
					entry.getKey(), drugClass, entry.getValue()));
			}
		}

		// body for total score
		scoreTable.appendChild(createScoreRowElement(
			"Total:",
			drugClass,
			geneDR.getDrugClassTotalDrugScores(drugClass),
			new LinkedHashMap<>()));

		return scoreTable;
	}

	private Element createCommentElement(Mutation mut, BoundComment bc) {
		Element comment = document.createElement("comment");
		// name="gene" type="xs:string"
		comment.appendChild(newSimpleElement("gene", mut.getGene().getShortName()));

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
			AlignedSequence alignedSeq,
			Map<Gene, GeneDR> resistanceResults) {

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

		HIVGenotypeResult genotypeResult = alignedSeq.getSubtypeResult();
		Apobec apobec = alignedSeq.getApobec();
		int numApobec = apobec.getNumApobecMuts();

		// name="GAHypermutated" type="xs:boolean" minOccurs="0" maxOccurs="1"
		result.appendChild(
			newSimpleElement("GAHypermutated", numApobec > 1));

		// name="geneData" type="GeneData" minOccurs="0" maxOccurs="3"
		for (Gene gene : Gene.values(Strain.HIV1)) {
			result.appendChild(createGeneDataElement(
				gene, alignedSeq.getAlignedGeneSequence(gene),
				resistanceResults.get(gene), genotypeResult));
		}

		// name="sequenceQualityCounts" type="SequenceQualityCounts" minOccurs="0" maxOccurs="1"
		result.appendChild(
			createSequenceQualityCountsElement(alignedSeq));

		// name="drugScore" type="DrugScore" minOccurs="0" maxOccurs="unbounded"
		for (Gene gene : resistanceResults.keySet()) {
			GeneDR geneDR = resistanceResults.get(gene);
			for (DrugClass drugClass : gene.getDrugClasses()) {
				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					result.appendChild(createDrugScoreElement(drug, geneDR));
				}
			}
		}

		// name="scoreTable" type="ScoreTable" minOccurs="0" maxOccurs="unbounded"
		for (Gene gene : resistanceResults.keySet()) {
			GeneDR geneDR = resistanceResults.get(gene);
			for (DrugClass drugClass : gene.getDrugClasses()) {
				result.appendChild(createScoreTableElement(drugClass, geneDR));
			}
		}

		// name="comment" type="MutationComment" minOccurs="0" maxOccurs="unbounded"
		for (Gene gene : resistanceResults.keySet()) {
			GeneDR geneDR = resistanceResults.get(gene);
			for (Map.Entry<CommentType, List<BoundComment>> entry :
					geneDR.groupCommentsByTypes().entrySet()) {
				for (BoundComment bc : entry.getValue()) {
					if (bc.getType() == CommentType.Dosage) {
						continue;
					}
					Mutation mut = bc.getBoundMutation();
					result.appendChild(createCommentElement(mut, bc));
				}
			}

		}

		return result;
	}

}
