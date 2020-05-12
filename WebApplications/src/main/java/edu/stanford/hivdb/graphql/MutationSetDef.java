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

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;
import edu.stanford.hivdb.viruses.WithGene;

import static edu.stanford.hivdb.graphql.MutationDef.oMutation;
import static edu.stanford.hivdb.graphql.ExtGraphQL.*;

public class MutationSetDef {

	private enum mutsFilterOption {
		APOBEC, APOBEC_DRM,
		DRM, notDRM, PI_DRM, NRTI_DRM, NNRTI_DRM, INSTI_DRM,
		SDRM, notSDRM, PI_SDRM, NRTI_SDRM, NNRTI_SDRM, INSTI_SDRM,
		DRP,
		TSM, notTSM, PI_TSM, NRTI_TSM, NNRTI_TSM, INSTI_TSM,
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
			"notSDRM", mutsFilterOption.notSDRM,
			"List only mutations which are not sruveillance drug resistance " +
			"mutation (SDRM).")
		.value(
			"PI_SDRM", mutsFilterOption.PI_SDRM,
			"List only mutations which are PI SDRM.")
		.value(
			"NRTI_SDRM", mutsFilterOption.NRTI_SDRM,
			"List only mutations which are NRTI SDRM.")
		.value(
			"NNRTI_SDRM", mutsFilterOption.NNRTI_SDRM,
			"List only mutations which are NNRTI SDRM.")
		.value(
			"INSTI_SDRM", mutsFilterOption.INSTI_SDRM,
			"List only mutations which are INSTI SDRM.")
		.value(
			"TSM", mutsFilterOption.TSM,
			"List only mutations which are treatment-selected mutations (TSM).")
		.value(
			"notTSM", mutsFilterOption.notTSM,
			"List only mutations which are not treatment-selected mutations (TSM).")
		.value(
			"PI_TSM", mutsFilterOption.PI_TSM,
			"List only mutations which are PI TSM.")
		.value(
			"NRTI_TSM", mutsFilterOption.NRTI_TSM,
			"List only mutations which are NRTI TSM.")
		.value(
			"NNRTI_TSM", mutsFilterOption.NNRTI_TSM,
			"List only mutations which are NNRTI TSM.")
		.value(
			"INSTI_TSM", mutsFilterOption.INSTI_TSM,
			"List only mutations which are INSTI TSM.")
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


	final public static class MutationSetDataFetcher<VirusT extends Virus<VirusT>> extends ExtPropertyDataFetcher<MutationSet<VirusT>> {

		private final VirusT virusIns;
		
		public MutationSetDataFetcher(VirusT virusIns, String propertyName) {
			super(propertyName);
			this.virusIns = virusIns;
		}

		@Override
		protected MutationSet<VirusT> postProcess(MutationSet<VirusT> mutations, DataFetchingEnvironment environment) {
			List<?> filterOptions = environment.getArgument("filterOptions");
			if (filterOptions == null) { filterOptions = new ArrayList<>(); }
			for (Object filterOption : filterOptions) {
				switch((mutsFilterOption) filterOption) {
				case APOBEC:
					mutations = mutations.getApobecMutations();
					break;
				case APOBEC_DRM:
					mutations = mutations.getApobecDRMs();
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
					mutations = mutations.getDRMs(virusIns.getDrugClass("PI"));
					break;
				case NRTI_DRM:
					mutations = mutations.getDRMs(virusIns.getDrugClass("NRTI"));
					break;
				case NNRTI_DRM:
					mutations = mutations.getDRMs(virusIns.getDrugClass("NNRTI"));
					break;
				case INSTI_DRM:
					mutations = mutations.getDRMs(virusIns.getDrugClass("INSTI"));
					break;
				case SDRM:
					mutations = mutations.getSDRMs();
					break;
				case notSDRM:
					mutations = mutations.subtractsBy(mutations.getSDRMs());
					break;
				case PI_SDRM:
					mutations = mutations.getSDRMs(virusIns.getDrugClass("PI"));
					break;
				case NRTI_SDRM:
					mutations = mutations.getSDRMs(virusIns.getDrugClass("NRTI"));
					break;
				case NNRTI_SDRM:
					mutations = mutations.getSDRMs(virusIns.getDrugClass("NNRTI"));
					break;
				case INSTI_SDRM:
					mutations = mutations.getSDRMs(virusIns.getDrugClass("INSTI"));
					break;
				case TSM:
					mutations = mutations.getTSMs();
					break;
				case notTSM:
					mutations = mutations.subtractsBy(mutations.getTSMs());
					break;
				case PI_TSM:
					mutations = mutations.getTSMs(virusIns.getDrugClass("PI"));
					break;
				case NRTI_TSM:
					mutations = mutations.getTSMs(virusIns.getDrugClass("NRTI"));
					break;
				case NNRTI_TSM:
					mutations = mutations.getTSMs(virusIns.getDrugClass("NNRTI"));
					break;
				case INSTI_TSM:
					mutations = mutations.getTSMs(virusIns.getDrugClass("INSTI"));
					break;
				case GENE_PR:
					// TODO: HIV2 support
					mutations = mutations.getGeneMutations(virusIns.getGene("HIV1PR"));
					break;
				case GENE_RT:
					mutations = mutations.getGeneMutations(virusIns.getGene("HIV1RT"));
					break;
				case GENE_IN:
					mutations = mutations.getGeneMutations(virusIns.getGene("HIV1IN"));
					break;
				case TYPE_MAJOR:
					mutations = mutations.getByMutType(virusIns.getMutationType("Major"));
					break;
				case TYPE_ACCESSORY:
					mutations = mutations.getByMutType(virusIns.getMutationType("Accessory"));
					break;
				case TYPE_NRTI:
					mutations = mutations.getByMutType(virusIns.getMutationType("NRTI"));
					break;
				case TYPE_NNRTI:
					mutations = mutations.getByMutType(virusIns.getMutationType("NNRTI"));
					break;
				case TYPE_OTHER:
					mutations = mutations.getByMutType(virusIns.getMutationType("Other"));
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
					Gene<VirusT> gene = null;
					if (WithGene.class.isInstance(environment.getSource())) {
						WithGene<VirusT> source = environment.getSource();
						gene = source.getGene();
					}
					MutationSet<VirusT> filterSet = virusIns.newMutationSet(gene, customList);
					mutations = mutations.intersectsWith(filterSet);
					break;
				}
			}
			return mutations;
		}
	}

	public static Builder newMutationSet(String virusName, Builder field, String name) {
		return field
			.name(name)
			.type(new GraphQLList(oMutation.get(virusName)))
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
					"the context."));
	}
}
