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

package edu.stanford.hivdb.web;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import edu.stanford.hivdb.viruses.Virus;


public abstract class SequenceAnalysisService<VirusT extends Virus<VirusT>> {

	@FormParam("compareAlgorithms")
	protected String algorithmsCSV;

	@FormParam("compareCustomAlgorithms")
	protected String customAlgorithmsStr;
	
	@FormParam("drugResistanceAlgorithm")
	protected String drAlgorithm;

	public abstract VirusT getVirusIns();
	
	/**
	 * Service endpoint that provide multiple types of results.
	 *
	 * @param sequences The input sequences in FASTA format.
	 * @param outputOptions Comma delimited values output options
	 * @return All results packed in JSON format.
	 */
	@POST
	@Produces("application/json")
	public final Response getAll(
			@FormParam("sequences") String sequences,
			@FormParam("outputOptions") String outputOptions) {

		SequenceAnalysisServiceOutput<VirusT> output =
			new SequenceAnalysisServiceOutput<>(getVirusIns(), this, sequences, outputOptions);

		return Response.ok(output.toString()).build();
	}
}
