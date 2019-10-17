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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.ValidationLevel;
import edu.stanford.hivdb.utilities.ValidationResult;

public class MutationListValidator {

	private MutationSet mutations;
	private List<ValidationResult> validationResults = new ArrayList<>();
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, ValidationLevel> VALIDATION_RESULT_LEVELS;
	private static final Map<String, String> VALIDATION_RESULT_MESSAGES;

	static {
		Map<String, ValidationLevel> levels = new HashMap<>();
		Map<String, String> messages = new HashMap<>();
		levels.put("severe-warning-too-many-stop-codons", ValidationLevel.SEVERE_WARNING);
		messages.put("severe-warning-too-many-stop-codons", "The submitted mutations contain %d stop codons.");

		levels.put("note-stop-codon", ValidationLevel.WARNING);
		messages.put("note-stop-codon", "The submitted mutations contain %d stop codon.");

		levels.put("much-too-many-unusual-mutations", ValidationLevel.SEVERE_WARNING);
		messages.put("much-too-many-unusual-mutations", "There are %d unusual mutations: %s.");

		levels.put("too-many-unusual-mutations", ValidationLevel.WARNING);
		messages.put("too-many-unusual-mutations", "There are %d unusual mutations: %s.");

		levels.put("some-unusual-mutations", ValidationLevel.NOTE);
		messages.put("some-unusual-mutations", "There are %d unusual mutations: %s.");

		levels.put("unusual-mutation-at-DRP-plural", ValidationLevel.SEVERE_WARNING);
		messages.put("unusual-mutation-at-DRP-plural",
				     "There are %d unusual mutations at drug-resistance positions: %s.");

		levels.put("unusual-mutation-at-DRP", ValidationLevel.WARNING);
		messages.put("unusual-mutation-at-DRP",
				     "There is %d unusual mutation at a drug-resistance position: %s.");

		levels.put("additional-unusual-mutation", ValidationLevel.WARNING);
		messages.put("additional-unusual-mutation", "There is one additional unusual mutation: %s");

		levels.put("additional-unusual-mutations", ValidationLevel.SEVERE_WARNING);
		messages.put("additional-unusual-mutations", "There are %d additional unusual mutations: %s");

		levels.put("severe-APOBEC", ValidationLevel.SEVERE_WARNING);
		messages.put("severe-APOBEC", "%s");

		levels.put("definite-APOBEC", ValidationLevel.WARNING);
		messages.put("definite-APOBEC", "%s");

		levels.put("possible-APOBEC-influence", ValidationLevel.NOTE);
		messages.put("possible-APOBEC-influence", "%s");

		levels.put("multiple-apobec-at-DRP", ValidationLevel.SEVERE_WARNING);
		messages.put("multiple-apobec-at-DRP",
				     "There are %d APOBEC-associated mutations at drug-resistance positions: %s.");

		levels.put("single-apobec-at-DRP", ValidationLevel.WARNING);
		messages.put("single-apobec-at-DRP",
				     "There is %d APOBEC-associated mutation at a drug-resistance position: %s.");

		VALIDATION_RESULT_LEVELS = Collections.unmodifiableMap(levels);
		VALIDATION_RESULT_MESSAGES = Collections.unmodifiableMap(messages);

	}

	public MutationListValidator(MutationSet mutList) {
		this.mutations = mutList;
	}

	public List<ValidationResult> getValidationResults() {
		return validationResults;
	}

	public boolean validate() {
		boolean validated = true;
		validated = validateNoStopCodons() && validated;
		validated = validateNotApobec() && validated;
		validated = validateNoTooManyUnusualMutations() && validated;
		return validated;
	}

	private void addValidationResult(String key, Object... args) {
		ValidationLevel level = VALIDATION_RESULT_LEVELS.get(key);
		String message = String.format(
			VALIDATION_RESULT_MESSAGES.get(key),
			args);
		ValidationResult result = new ValidationResult(level, message);
		validationResults.add(result);
	}

	private boolean validateNoStopCodons() {
		boolean validated = true;
		int numStopCodons = mutations.getStopCodons().size();
		if (numStopCodons > 1) {
			addValidationResult("severe-warning-too-many-stop-codons",
					numStopCodons);
			validated = false;
		} else if (numStopCodons > 0) {
			addValidationResult("note-stop-codon",
					numStopCodons);
			validated = false;
		}

		return validated;
	}


	private boolean validateNoTooManyUnusualMutations() {
		boolean validated = true;

		MutationSet unusualMutations = mutations.getUnusualMutations();
		int numUnusual = unusualMutations.size();

		MutationSet unusualMutAtDRP = unusualMutations.getAtDRPMutations();

		int numUnusualAtDRP = unusualMutAtDRP.size();
		/*if (numUnusual != numUnusualAtDRP && numUnusual > 1) {
			System.out.println("Debug:text:" + text);
			addValidationResult("too-many-unusual-mutations",
					numUnusual, text);
			validated = false;
		} else*/

		if (numUnusualAtDRP > 1) {
			addValidationResult("unusual-mutation-at-DRP-plural",
						numUnusualAtDRP, unusualMutAtDRP.join(", ", Mutation::getHumanFormatWithGene));
			validated = false;
		} else if (numUnusualAtDRP == 1) {
			addValidationResult("unusual-mutation-at-DRP",
					numUnusualAtDRP,  unusualMutAtDRP.join(", ", Mutation::getHumanFormatWithGene));
				validated = false;
		}
		if (numUnusual > 1) {
			validated = false;
			if (numUnusualAtDRP == 0) {
				addValidationResult("too-many-unusual-mutations",
					numUnusual, unusualMutations.join(", ", Mutation::getHumanFormatWithGene));

			} else if (numUnusual - numUnusualAtDRP == 1) {
				MutationSet additionalMuts = unusualMutations.subtractsBy(unusualMutAtDRP);
				addValidationResult(
					"additional-unusual-mutation",
					additionalMuts.join(", ", Mutation::getHumanFormatWithGene));

			} else if (numUnusual -numUnusualAtDRP > 1) {
				int numAdditionalUnusual = numUnusual - numUnusualAtDRP;
				MutationSet additionalMuts = unusualMutations.subtractsBy(unusualMutAtDRP);
				addValidationResult(
					"additional-unusual-mutations", numAdditionalUnusual,
					additionalMuts.join(", ", Mutation::getHumanFormatWithGene));
			} else {
				// numUnusual == numUnusualAtDRP && numUnusual > 1
				// No  warning  necessary as it will be included in the unusual-mutation-at-DRP-plural warning
			}
		}

		return validated;

	}

	private boolean validateNotApobec() {
		Apobec apobec = new Apobec(mutations);
		boolean validated = true;

		int numApobecMuts = apobec.getNumApobecMuts();

		if (numApobecMuts > 3) {
			addValidationResult("severe-APOBEC", apobec.generateComment());
			validated = false;
		} else if (numApobecMuts > 1) {
			addValidationResult("definite-APOBEC", apobec.generateComment());
			validated = false;
		} else if (numApobecMuts == 1) {
			addValidationResult("possible-APOBEC-influence", apobec.generateComment());
			validated = false;
		}

		MutationSet apobecMutsAtDRP = apobec.getApobecMutsAtDRP();
		int numApobecMutsAtDRP = apobecMutsAtDRP.size();
		String level = null;
		if (numApobecMutsAtDRP > 1) {
			level = "multiple-apobec-at-DRP";
		} else if (numApobecMutsAtDRP == 1) {
			level = "single-apobec-at-DRP";
		}

		if (level != null) {
			addValidationResult(level, numApobecMutsAtDRP,
				apobecMutsAtDRP.join(", ", Mutation::getHumanFormatWithGene));
			validated = false;
		}
		return validated;
	}




}
