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

package edu.stanford.hivdb.drugresistance.algorithm;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public interface Asi {

	public enum SIREnum { S, I, R };

	public static class AsiDrugComparableResult {
		public final SIREnum SIR;
		public final String Interpretation;
		public final String Explanation;

		public AsiDrugComparableResult(
				String SIR, String Interpretation, String Explanation) {
			this.SIR = SIREnum.valueOf(SIR);
			this.Interpretation = Interpretation;
			this.Explanation = Explanation;
		}

		public String getInterpretation() { return Interpretation; }
		public String getExplanation() { return Explanation; }

		@Override
		public boolean equals(Object o) {
			if (o == this) { return true; }
			if (o == null) { return false; }
			if (!(o instanceof AsiDrugComparableResult)) { return false;}
			AsiDrugComparableResult a = (AsiDrugComparableResult) o;

			return new EqualsBuilder()
				.append(SIR, a.SIR)
				.append(Interpretation, a.Interpretation)
				.append(Explanation, a.Explanation)
				.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(84535, 467345)
				.append(SIR)
				.append(Interpretation)
				.append(Explanation)
				.toHashCode();
		}

		@Override
		public String toString() {
			return String.format(
				"AsiDrugComparableResult(%s, %s, %s)",
				"\"" + SIR + "\"",
				"\"" + Interpretation.replace("\"", "\\\"") + "\"",
				"\"" + Explanation.replace("\"", "\\\"") + "\"");
		}
	}

	public Gene getGene();

	public String getAlgorithmName();

	public int getDrugLevel(Drug drug);

	public String getDrugLevelText(Drug drug);

	public String getDrugLevelSir(Drug drug);

	public Double getTotalScore(Drug drug);

	public AsiDrugComparableResult getDrugComparableResult(Drug drug);

	public Map<DrugClass, Map<Drug, Double>> getDrugClassTotalDrugScores();
	public Map<Drug, Double> getDrugClassTotalDrugScores(DrugClass drugClass);

	public MutationSet getTriggeredMutations();

	public MutationSet getTriggeredMutations(DrugClass drugClass);

	public Map<DrugClass, Map<Drug, Map<Mutation, Double>>> getDrugClassDrugMutScores();

	public Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> getDrugClassDrugComboMutScores();

	public Map<Drug, Map<String, String>> getTriggeredDrugRules();

	public Map<Drug, Map<Mutation, Double>> getDrugMutScores();

	public Map<Drug, Map<MutationSet, Double>> getDrugComboMutScores();

}
