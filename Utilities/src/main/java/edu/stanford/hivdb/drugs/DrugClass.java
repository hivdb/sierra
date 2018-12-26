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

package edu.stanford.hivdb.drugs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.hivdb.mutations.Gene;

public enum DrugClass {
	NRTI(Gene.RT, "Nucleoside Reverse Transcriptase Inhibitor"),
	NNRTI(Gene.RT, "Non-nucleoside Reverse Transcriptase Inhibitor"),
	PI(Gene.PR, "Protease Inhibitor"),
	INSTI(Gene.IN, "Integrase Strand Transfer Inhibitor");

	private final Gene gene;
	private final String fullName;

	private DrugClass(final Gene gene, final String fullName) {
		this.gene = gene;
		this.fullName = fullName;
	}

	public List<Drug> getAllDrugs() {
		return Stream.of(Drug.values())
			   .filter(d -> d.getDrugClass() == this)
			   .collect(Collectors.toList());
	}

	public List<Drug> getDrugsForHivdbTesting() {
		return this.getAllDrugs()
				   .stream()
				   .filter(d -> d.forHivdbResistanceTesting)
				   .collect(Collectors.toList());
	}

	public Gene gene() {
		return this.gene;
	}

	public String getFullName() {
		return this.fullName;
	}

	public static DrugClass getSynonym(String synonym) {
		Map<String, DrugClass> drugSynonyms = new HashMap<>();
		drugSynonyms.put("INI", DrugClass.INSTI);
		if (!drugSynonyms.containsKey(synonym)) {
			return DrugClass.valueOf(synonym);
		} else {
			return drugSynonyms.get(synonym);
		}
	}
}
