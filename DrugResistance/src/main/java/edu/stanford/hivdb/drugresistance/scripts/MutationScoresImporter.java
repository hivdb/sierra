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

// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// 
// import org.apache.commons.lang3.StringUtils;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
// 
// import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
// import edu.stanford.hivdb.hivfacts.HIVDrug;
// import edu.stanford.hivdb.hivfacts.HIVDrugClass;
// import edu.stanford.hivdb.hivfacts.HIVGene;
// import edu.stanford.hivdb.hivfacts.HIVStrain;
// import edu.stanford.hivdb.mutations.ConsensusMutation;
// import edu.stanford.hivdb.hivfacts.HIVAAMutation;
// import edu.stanford.hivdb.utilities.AAUtils;
// import edu.stanford.hivdb.utilities.MyFileUtils;


/**
 * Score files are named as ("Scores%s_%s.tsv", drugClass, VERSION)
 * The VERSION must also first be created in HivdbVersion and be assigned to the currentVersion
 * The first column contains a mutation indicated by a consensusAA . pos . mutationAA
 *   or by a rule containing a position . one or more AAs + position . one or more AAs ...
 * The second column indicates whether the first column has a mutation ("Individual")
 *   or a rule ("Combination").
 * The remaining columns contain scores for each drug.
 *
 * This class will create a SQL file which populates all of the scores and
 * combination scores for that version into tblScoresWithVersions,
 * tblCombinationScoresWithVersions and tblCompoundScoresWithVersions.
 *
 */
@Deprecated
public class MutationScoresImporter {
// 	private static final String INPUT_FILE_DIR = "__input/MutationScores";
// 	private static final String OUTPUT_FILE = "__output/mutationScores.sql";
// 	private static final boolean HEADER_FLAG = true;
// 	private static final HivdbVersion VERSION = HivdbVersion.getLatestVersion();
// 	private static final Logger LOGGER = LogManager.getLogger();
// 
// 
// 	/**
// 	 * Read the tab-delimited text files containing each of individual and combination mutation scores
// 	 * for each of the drug classes. The header contains: Mutation, Type(Individual vs Combination),
// 	 * followed by each of the drugs in that class in alphabetical order. The file contents are insert into
// 	 * tblScores and tblCombinatonScores
// 	 *
// 	 * For now tblCompoundScores are done manually
// 	 *
// 	 */
// 	public static void main(String[] args) {
// 		StringBuilder statements = new StringBuilder();
// 		for (HIVDrugClass drugClass : HIVDrugClass.values()) {
// 			String fileName = String.format("Scores%s_%s.tsv",drugClass, VERSION);
// 			File file = new File(INPUT_FILE_DIR, fileName);
// 			try {
// 				InputStream inputStream = new FileInputStream(file);
// 				BufferedReader br = new BufferedReader (new InputStreamReader(inputStream));
// 				String rowLine;
// 				boolean headerFlag = HEADER_FLAG;
// 				while ((rowLine = br.readLine()) != null) {
// 					if (rowLine.length()>0 && !rowLine.substring(0,1).equals("#")){
// 						LOGGER.debug("  line:" + rowLine);
// 					}
// 					if (headerFlag) {
// 						headerFlag = false;
// 						continue;
// 					}
// 					rowLine.trim();
// 					statements.append(insertRowIntoDB(drugClass, rowLine));
// 					statements.append('\n');
// 				}
// 				br.close();
// 			} catch (FileNotFoundException e) {
// 				System.err.println("Cannot locate " + file);
// 				throw new RuntimeException(e);
// 			} catch (IOException e) {
// 				System.err.println("Cannot read line");
// 				throw new RuntimeException(e);
// 			}
// 		}
// 		MyFileUtils.writeFile(OUTPUT_FILE, statements.toString());
// 		System.out.println(String.format("%s created.", OUTPUT_FILE));
// 	}
// 
// 	private static String insertRowIntoDB(HIVDrugClass drugClass, String rowLine) {
// 		List<String> rowFields =
// 			new ArrayList<String>(Arrays.asList(rowLine.split("\t")));
// 		String mutText = rowFields.remove(0);
// 		String type = rowFields.remove(0);
// 		List<HIVDrug> drugList = drugClass.getDrugs();
// 		// TODO: HIV2 support
// 		HIVGene gene = HIVGene.valueOf(HIVStrain.HIV1, drugClass.gene());
// 		StringBuilder statements = new StringBuilder();
// 		boolean noScores = true;
// 		if ("Individual".equals(type)) {
// 			HIVAAMutation mut = ConsensusMutation.parseString(gene, mutText);
// 			int pos = mut.getPosition();
// 			String aa = AAUtils.toHIVDBFormat(mut.getAAs());
// 			String tblName = "tblScoresWithVersions";
// 			statements.append(String.format("INSERT INTO `%s` ", tblName));
// 			statements.append(
// 				"(Gene, DrugClass, Pos, AA, Drug, Score, Version) VALUES ");
// 			List<String> values = new ArrayList<>();
// 			for (int i = 0; i<drugList.size(); i++) {
// 				HIVDrug drug = drugList.get(i);
// 				int score = Integer.parseInt(rowFields.get(i));
// 				if (score == 0) {
// 					continue;
// 				}
// 				noScores = false;
// 				values.add(String.format(
// 					"('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
// 					gene, drugClass, pos, aa, drug, score, VERSION
// 				));
// 			}
// 			statements.append(String.join(",", values));
// 			statements.append(';');
// 		} else if ("Combination".equals(type)) {
// 			String tblName = "tblCombinationScoresWithVersions";
// 			statements.append(String.format("INSERT INTO `%s` ", tblName));
// 			statements.append(
// 				"(Gene, DrugClass, Rule, Drug, Score, Version, CompoundRule) VALUES ");
// 			List<String> values = new ArrayList<>();
// 			for (int i = 0; i<drugList.size(); i++) {
// 				HIVDrug drug = drugList.get(i);
// 				int score = Integer.parseInt(rowFields.get(i));
// 				if (score == 0) {
// 					continue;
// 				}
// 				noScores = false;
// 				values.add(String.format(
// 					"('%s', '%s', '%s', '%s', '%s', '%s', 0)",
// 					gene, drugClass,
// 					StringUtils.replace(AAUtils.toHIVDBFormat(mutText), "'", "''"),
// 					drug, score, VERSION
// 				));
// 			}
// 			statements.append(String.join(",", values));
// 			statements.append(';');
// 		}
// 		return noScores ? "" : statements.toString();
// 	}



}
