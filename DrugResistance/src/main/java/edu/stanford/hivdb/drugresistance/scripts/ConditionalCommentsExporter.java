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

import java.util.stream.Collectors;

import edu.stanford.hivdb.drugresistance.database.ConditionalComments;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.utilities.MyFileUtils;

public class ConditionalCommentsExporter {

	private static final String OUTPUT_FILE_PREFIX =
		"__output/ConditionalComments";

	public static void main(String[] args) {

		for (DrugClass drugClass : DrugClass.values()) {
			StringBuilder output = new StringBuilder();
			String header = "CommentName\tDrugClass\tConditionType\tGene\tPosition\tAAs\tDrugLevels\tComment\n";
			output.append(header);

			output.append(
				ConditionalComments.getAllComments()
					.stream()
					.filter(cc -> cc.getDrugClass() == drugClass)
					.map(cc -> String.format(
						"%s\t%s\t%s\t%s\t%d\t%s\t%s\t%s",
						cc.getName(), cc.getDrugClass(), cc.getConditionType(), cc.getMutationGene(),
						cc.getMutationPosition(), cc.getMutationAAs(), cc.getDrugLevelsText(), cc.getText()))
					.collect(Collectors.joining("\n")
				)
			);

			String outputFile = OUTPUT_FILE_PREFIX + "/" + drugClass + ".tsv";
			MyFileUtils.writeFile(outputFile, output.toString());
		}
	}


}
