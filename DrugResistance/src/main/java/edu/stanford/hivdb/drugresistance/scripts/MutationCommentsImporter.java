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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.GeneEnum;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.utilities.MyFileUtils;


/**
 * Comment file is named as ("Comments_%s.tsv", VERSION)
 * The VERSION must also first be created in HivdbVersion
 * and be assigned to the currentVersion
 *
 * Headers:
 *
 * Gene Pos Rank AAs MutType Comment
 *
 * This class will create a SQL file which populates all of the comments
 * into tblCommentsWithVersions.
 *
 */
public class MutationCommentsImporter {
	private static final String INPUT_FILE_DIR = "__input/MutationComments";
	private static final String OUTPUT_FILE = "__output/mutationComments.sql";
	private static final boolean HEADER_FLAG = true;
	private static final HivdbVersion VERSION = HivdbVersion.getLatestVersion();
	private static final Logger LOGGER = LogManager.getLogger();


	public static void main(String[] args) {
		StringBuilder statements = new StringBuilder();
		String fileName = String.format("Comments_%s.tsv", VERSION);
		File file = new File(INPUT_FILE_DIR, fileName);
		try {
			InputStream inputStream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String rowLine;
			boolean headerFlag = HEADER_FLAG;
			while ((rowLine = br.readLine()) != null) {
				if (rowLine.length()>0 && !rowLine.substring(0,1).equals("#")){
					LOGGER.debug("  line:" + rowLine);
				}
				if (headerFlag) {
					headerFlag = false;
					continue;
				}
				rowLine.trim();
				statements.append(insertRowIntoDB(rowLine));
				statements.append('\n');
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Cannot locate " + file);
			throw new RuntimeException(e);
		} catch (IOException e) {
			System.err.println("Cannot read line");
			throw new RuntimeException(e);
		}
		MyFileUtils.writeFile(OUTPUT_FILE, statements.toString());
		System.out.println(String.format("%s created.", OUTPUT_FILE));
	}

	private static String insertRowIntoDB(String rowLine) {
		List<String> rowFields =
			new ArrayList<String>(Arrays.asList(rowLine.split("\t")));
		GeneEnum gene = GeneEnum.valueOf(rowFields.remove(0));
		DrugClass drugClass = DrugClass.valueOf(rowFields.remove(0));
		int pos = Integer.parseInt(rowFields.remove(0));
		int rank = Integer.parseInt(rowFields.remove(0));
		String aas = rowFields.remove(0);
		MutType mutType = MutType.valueOf(rowFields.remove(0));
		String comment = rowFields.remove(0);
		StringBuilder statements = new StringBuilder();
		statements.append("INSERT INTO `tblCommentsWithVersions` ");
		statements.append(
			"(Gene, DrugClass, Pos, AAs, Type, " +
			"Display, Version, Date, Comment) VALUES ");
		statements.append(String.format(
			"('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
			gene, drugClass, pos, aas, mutType,
			rank, VERSION, VERSION.versionDate,
			StringEscapeUtils.escapeSql(comment.trim())));
		statements.append(';');
		return statements.toString();
	}

}
