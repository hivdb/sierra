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

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.stanford.hivdb.drugresistance.database.ConditionalComments;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.ConditionType;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.ConditionalComment;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.AA;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.utilities.MyEnumUtils;


/**
 *
 * Create the ASI xml specification for the latest version of the HIVdb algorithm.
 * This will use the cached results of MutationScores and MutationComboScores.
 * Use command "graddle updateCachable" to run this script properly.
 *
 * This version should be the same as HivdbVersion.getCurrentVersion().
 * Although the comments could also be specified by the ASI xml, we have not been
 *   doing this because there are still some aspects of this that are not fully
 *   documented. Therefore the comments are obtained directly from tblComments.
 *
 */
public class AsiConstructor {
	private static final String ALG_NAME = "HIVDB";
	private static final String GLOBALRANGE_CONTENTS = "(-INF TO 9 => 1,  " +
		"10 TO 14 => 2,  " +
		"15 TO 29 => 3,  " +
		"30 TO 59 => 4,  " +
		"60 TO INF => 5)";
	private static final String INDENT_FOR_RULES = StringUtils.repeat(" ", 25);


	public static void main(String [] args) throws SQLException {
		AsiConstructor.createXML(HivdbVersion.getLatestVersion());
	}


	public static void createXML(HivdbVersion version) throws SQLException {
		final String ALG_VERSION = version.readableVersion;
		final String ALG_DATE = version.versionDate;
		final String OUTPUT_FILE = "src/main/resources/" + version.resourcePath;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			DOMImplementation domImpl = doc.getImplementation();
			DocumentType docType = domImpl.createDocumentType("ALGORITHM", "",
					"http://sierra2.stanford.edu/sierra/ASI2.1.dtd");
			doc.appendChild(docType);


			Node rootElement = doc.createElement("ALGORITHM");
			doc.appendChild(rootElement);

			Node algName = doc.createElement("ALGNAME");
			algName.appendChild(doc.createTextNode(ALG_NAME));
			rootElement.appendChild(algName);

			Node algVersion = doc.createElement("ALGVERSION");
			algVersion.appendChild(doc.createTextNode(ALG_VERSION));
			rootElement.appendChild(algVersion);

			Node algDate = doc.createElement("ALGDATE");
			algDate.appendChild(doc.createTextNode(ALG_DATE));
			rootElement.appendChild(algDate);

			Node definition = doc.createElement("DEFINITIONS");
			rootElement.appendChild(definition);

			for (Gene gene : Gene.values()) {
				List<DrugClass> drugClasses = gene.getDrugClasses();
				String drugClassListString = MyEnumUtils.join(drugClasses, ",");

				Node geneDefinition = doc.createElement("GENE_DEFINITION");
				definition.appendChild(geneDefinition);

				Node geneName = doc.createElement("NAME");
				geneName.appendChild(doc.createTextNode(gene.toString()));
				geneDefinition.appendChild(geneName);

				Node drugClassList = doc.createElement("DRUGCLASSLIST");
				drugClassList.appendChild(doc.createTextNode(drugClassListString));
				geneDefinition.appendChild(drugClassList);
			}

			for (HivdbLevelDefinitions levelDef : HivdbLevelDefinitions.values()) {
				Node levelDefinition = doc.createElement("LEVEL_DEFINITION");
				definition.appendChild(levelDefinition);

				Node levelOrder = doc.createElement("ORDER");
				levelOrder.appendChild(doc.createTextNode(String.valueOf(levelDef.getLevel())));
				levelDefinition.appendChild(levelOrder);

				Node levelDescription = doc.createElement("ORIGINAL");
				levelDescription.appendChild(doc.createTextNode(levelDef.getDescription()));
				levelDefinition.appendChild(levelDescription);

				Node sir = doc.createElement("SIR");
				sir.appendChild(doc.createTextNode(levelDef.getSir()));
				levelDefinition.appendChild(sir);

			}

			for (DrugClass drugClass : DrugClass.values()) {
				List<Drug> drugs = drugClass.getDrugsForHivdbTesting();
				String drugList = MyEnumUtils.join(drugs, ",", Drug::getDisplayAbbr);
				Node drugClassNode = doc.createElement("DRUGCLASS");
				definition.appendChild(drugClassNode);

				Node drugClassName = doc.createElement("NAME");
				drugClassName.appendChild(doc.createTextNode(drugClass.toString()));
				drugClassNode.appendChild(drugClassName);

				Node drugClassDrugs = doc.createElement("DRUGLIST");
				drugClassDrugs.appendChild(doc.createTextNode(drugList));
				drugClassNode.appendChild(drugClassDrugs);
			}

			Node globalRange = doc.createElement("GLOBALRANGE");
			globalRange.appendChild(doc.createCDATASection(GLOBALRANGE_CONTENTS));
			definition.appendChild(globalRange);
			definition.appendChild(createCommentDefinitions(doc));


			for (DrugClass drugClass : DrugClass.values()) {
				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					Node drugs = doc.createElement("DRUG");
					rootElement.appendChild(drugs);

					Node drugName = doc.createElement("NAME");
					drugName.appendChild(doc.createTextNode(drug.getDisplayAbbr()));
					drugs.appendChild(drugName);

					Node fullDrugName = doc.createElement("FULLNAME");
					fullDrugName.appendChild(doc.createTextNode(drug.getFullName()));
					drugs.appendChild(fullDrugName);

					Node rule = doc.createElement("RULE");
					drugs.appendChild(rule);

					HivdbRulesForAsiConstructor hivdbAsiRulesForDrug = new HivdbRulesForAsiConstructor(version, drug);
					List<String> allRules = hivdbAsiRulesForDrug.getAllRules();
					String allRulesText = "SCORE FROM(" +
									String.join(",\n" + INDENT_FOR_RULES, allRules) +
									")";
					Node condition = doc.createElement("CONDITION");
					condition.appendChild(doc.createCDATASection(allRulesText));
					rule.appendChild(condition);

					Node actions = doc.createElement("ACTIONS");
					rule.appendChild(actions);

					Node scoreRange = doc.createElement("SCORERANGE");
					actions.appendChild(scoreRange);
					scoreRange.appendChild(doc.createElement("USE_GLOBALRANGE"));

				}
			}
			rootElement.appendChild(createMutationCommentRules(doc));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());

			DOMSource source = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File(OUTPUT_FILE));
			transformer.transform(source, streamResult);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}


	private static Element createCommentDefinitions(Document doc) {
		Element commentDefs = doc.createElement("COMMENT_DEFINITIONS");
		for (ConditionalComment comment : ConditionalComments.getAllComments()) {
			if (comment.getConditionType() == ConditionType.DRUGLEVEL) {
				continue;
			}
			Element commentStr = doc.createElement("COMMENT_STRING");
			commentStr.setAttribute("id", comment.getName());
			Element text = doc.createElement("TEXT");
			text.appendChild(doc.createCDATASection(comment.getText()));
			Element sortTag = doc.createElement("SORT_TAG");
			sortTag.appendChild(doc.createTextNode("1"));
			commentStr.appendChild(text);
			commentStr.appendChild(sortTag);
			commentDefs.appendChild(commentStr);
		}
		return commentDefs;
	}

	private static Element createMutationCommentRules(Document doc) {
		Element mutComments = doc.createElement("MUTATION_COMMENTS");
		Map<Gene, List<ConditionalComment>> commentsByGenes =
			ConditionalComments.getAllComments()
			.stream()
			.collect(Collectors.groupingBy(
				cmt -> cmt.getGene(), LinkedHashMap::new, Collectors.toList()));
		for (Gene gene : commentsByGenes.keySet()) {
			List<ConditionalComment> comments = commentsByGenes.get(gene);
			Element geneNode = doc.createElement("GENE");
			Element geneNameNode = doc.createElement("NAME");
			geneNameNode.appendChild(doc.createTextNode(gene.toString()));
			geneNode.appendChild(geneNameNode);
			for (ConditionalComment cmt : comments) {
				if (cmt.getConditionType() == ConditionType.DRUGLEVEL) {
					continue;
				}
				Element ruleNode = doc.createElement("RULE");
				Element condNode = doc.createElement("CONDITION");
				condNode.appendChild(doc.createTextNode(
					String.format("%d%s", cmt.getMutationPosition(), AA.toASIFormat(cmt.getMutationAAs()))));
				ruleNode.appendChild(condNode);
				Element actionsNode = doc.createElement("ACTIONS");
				Element commentNode = doc.createElement("COMMENT");
				commentNode.setAttribute("ref", cmt.getName());
				actionsNode.appendChild(commentNode);
				ruleNode.appendChild(actionsNode);
				geneNode.appendChild(ruleNode);
			}
			mutComments.appendChild(geneNode);
		}
		return mutComments;
	}

}
