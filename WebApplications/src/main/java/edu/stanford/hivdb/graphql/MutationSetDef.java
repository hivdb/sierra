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

package edu.stanford.hivdb.graphql;

import graphql.schema.*;
import static graphql.Scalars.*;
import graphql.schema.GraphQLFieldDefinition.Builder;

import static graphql.schema.GraphQLArgument.newArgument;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Sdrms;
import edu.stanford.hivdb.mutations.Tsms;
import edu.stanford.hivdb.mutations.WithGene;

import static edu.stanford.hivdb.graphql.MutationDef.oMutation;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class MutationSetDef {

	private enum mutsFilterOption {
		APOBEC, APOBEC_DRM,
		DRM, notDRM, PI_DRM, NRTI_DRM, NNRTI_DRM, INSTI_DRM, SDRM, DRP,
		PI_TSM, NRTI_TSM, NNRTI_TSM, INSTI_TSM,
		GENE_PR, GENE_RT, GENE_IN,
		TYPE_MAJOR, TYPE_ACCESSORY, TYPE_NRTI, TYPE_NNRTI, TYPE_OTHER,
		INSERTION, DELETION, UNUSUAL, AMBIGUOUS, STOPCODON,
		CUSTOMLIST
	};

	private static GraphQLEnumType oMutationSetFilterOption =
		GraphQLEnumType.newEnum()
		.name("MutationSetFilterOption")
		.description("Filter option for mutation set.")
		.value(
			"APOBEC", mutsFilterOption.APOBEC,
			"List only mutations which are APOBEC-mediated " +
			"G-to-A hypermutation.")
		.value(
			"APOBEC_DRM", mutsFilterOption.APOBEC_DRM,
			"List only drug resistance mutations which are " +
			"APOBEC-mediated G-to-A hypermutation.")
		.value(
			"DRM", mutsFilterOption.DRM,
			"List only mutations which are drug resistance mutation (DRM).")
		.value(
			"DRP", mutsFilterOption.DRP,
			"List all mutations at DRM positions (no need to be DRMs).")
		.value(
			"notDRM", mutsFilterOption.notDRM,
			"List only mutations which are not drug resistance mutation (DRM).")
		.value(
			"PI_DRM", mutsFilterOption.PI_DRM,
			"List only mutations which are PI DRM.")
		.value(
			"NRTI_DRM", mutsFilterOption.NRTI_DRM,
			"List only mutations which are NRTI DRM.")
		.value(
			"NNRTI_DRM", mutsFilterOption.NNRTI_DRM,
			"List only mutations which are NNRTI DRM.")
		.value(
			"INSTI_DRM", mutsFilterOption.INSTI_DRM,
			"List only mutations which are INSTI DRM.")
		.value(
			"SDRM", mutsFilterOption.SDRM,
			"List only mutations which are surveillance drug resistance " +
			"mutations (SDRM).")
		.value(
			"PI_TSM", mutsFilterOption.PI_TSM,
			"List only mutations which are treatment-selected mutations " +
			"(TSM) for drug class PI.")
		.value(
			"NRTI_TSM", mutsFilterOption.NRTI_TSM,
			"List only mutations which are treatment-selected mutations " +
			"(TSM) for drug class NRTI.")
		.value(
			"NNRTI_TSM", mutsFilterOption.NNRTI_TSM,
			"List only mutations which are treatment-selected mutations " +
			"(TSM) for drug class NNRTI.")
		.value(
			"INSTI_TSM", mutsFilterOption.INSTI_TSM,
			"List only mutations which are treatment-selected mutations " +
			"(TSM) for drug class INSTI.")
		.value("GENE_PR", mutsFilterOption.GENE_PR)
		.value("GENE_RT", mutsFilterOption.GENE_RT)
		.value("GENE_IN", mutsFilterOption.GENE_IN)
		.value("TYPE_MAJOR", mutsFilterOption.TYPE_MAJOR)
		.value("TYPE_ACCESSORY", mutsFilterOption.TYPE_ACCESSORY)
		.value("TYPE_NRTI", mutsFilterOption.TYPE_NRTI)
		.value("TYPE_NNRTI", mutsFilterOption.TYPE_NNRTI)
		.value("TYPE_OTHER", mutsFilterOption.TYPE_OTHER)
		.value("INSERTION", mutsFilterOption.INSERTION)
		.value("DELETION", mutsFilterOption.DELETION)
		.value("UNUSUAL", mutsFilterOption.UNUSUAL)
		.value(
			"AMBIGUOUS", mutsFilterOption.AMBIGUOUS,
			"List all highly-ambiguous (HBDVN) mutations.")
		.value(
			"STOPCODON", mutsFilterOption.STOPCODON,
			"List only mutations with stop codon(s).")
		.value(
			"CUSTOMLIST", mutsFilterOption.CUSTOMLIST,
			"Accept a custom list of mutations and find the intersects.")
		.build();

	public static Builder newMutationSet(String name) {

		final class MutationSetDataFetcher extends ExtendedPropertyDataFetcher {

			public MutationSetDataFetcher(String propertyName) {
				super(propertyName);
			}

			@Override
			protected Object postProcess(Object object, DataFetchingEnvironment environment) {
				MutationSet mutations = (MutationSet) object;
				List<?> filterOptions = environment.getArgument("filterOptions");
				if (filterOptions == null) { filterOptions = new ArrayList<>(); }
				for (Object filterOption : filterOptions) {
					switch((mutsFilterOption) filterOption) {
					case APOBEC:
						mutations = new Apobec(mutations).getApobecMuts();
						break;
					case APOBEC_DRM:
						mutations = new Apobec(mutations).getApobecDRMs();
						break;
					case DRM:
						mutations = mutations.getDRMs();
						break;
					case DRP:
						mutations = mutations.getAtDRPMutations();
					case notDRM:
						mutations = mutations.subtractsBy(mutations.getDRMs());
						break;
					case PI_DRM:
						mutations = mutations.getDRMs(DrugClass.PI);
						break;
					case NRTI_DRM:
						mutations = mutations.getDRMs(DrugClass.NRTI);
						break;
					case NNRTI_DRM:
						mutations = mutations.getDRMs(DrugClass.NNRTI);
						break;
					case INSTI_DRM:
						mutations = mutations.getDRMs(DrugClass.INSTI);
						break;
					case SDRM:
						mutations = Sdrms.getSdrms(mutations);
						break;
					case PI_TSM:
						mutations = Tsms.getTsmsForDrugClass(DrugClass.PI, mutations);
						break;
					case NRTI_TSM:
						mutations = Tsms.getTsmsForDrugClass(DrugClass.NRTI, mutations);
						break;
					case NNRTI_TSM:
						mutations = Tsms.getTsmsForDrugClass(DrugClass.NNRTI, mutations);
						break;
					case INSTI_TSM:
						mutations = Tsms.getTsmsForDrugClass(DrugClass.INSTI, mutations);
						break;
					case GENE_PR:
						mutations = mutations.getGeneMutations(Gene.PR);
						break;
					case GENE_RT:
						mutations = mutations.getGeneMutations(Gene.RT);
						break;
					case GENE_IN:
						mutations = mutations.getGeneMutations(Gene.IN);
						break;
					case TYPE_MAJOR:
						mutations = mutations.getByMutType(MutType.Major);
						break;
					case TYPE_ACCESSORY:
						mutations = mutations.getByMutType(MutType.Accessory);
						break;
					case TYPE_NRTI:
						mutations = mutations.getByMutType(MutType.NRTI);
						break;
					case TYPE_NNRTI:
						mutations = mutations.getByMutType(MutType.NNRTI);
						break;
					case TYPE_OTHER:
						mutations = mutations.getByMutType(MutType.Other);
						break;
					case DELETION:
						mutations = mutations.getDeletions();
						break;
					case INSERTION:
						mutations = mutations.getInsertions();
						break;
					case UNUSUAL:
						mutations = mutations.getUnusualMutations();
						break;
					case AMBIGUOUS:
						mutations = mutations.getAmbiguousCodons();
						break;
					case STOPCODON:
						mutations = mutations.getStopCodons();
						break;
					case CUSTOMLIST:
						List<String> customList = environment.getArgument("customList");
						Object source = environment.getSource();
						Gene gene = null;
						if (source instanceof WithGene) {
							gene = ((WithGene) source).getGene();
						}
						MutationSet filterSet = new MutationSet(gene, customList);
						System.out.println(filterSet);
						mutations = mutations.intersectsWith(filterSet);
						break;
					}
				}
				return mutations;
			}
		}

		return newFieldDefinition()
			.name(name)
			.type(new GraphQLList(oMutation))
			.argument(arg -> arg
				.name("filterOptions")
				.type(new GraphQLList(oMutationSetFilterOption))
				.description("List of filter options for the mutation set."))
			.argument(arg -> arg
				.name("customList")
				.type(new GraphQLList(GraphQLString))
				.description(
					"List of possible mutation strings that should be " +
					"included in this query if presented. Gene need to be " +
					"prepend if the gene is not able to be inferred from " +
					"the context."))
			.dataFetcher(new MutationSetDataFetcher(name));
	}
}
