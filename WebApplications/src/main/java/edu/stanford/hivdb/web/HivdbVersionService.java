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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import edu.stanford.hivdb.drugresistance.reports.TabularPatternsComparison;
import edu.stanford.hivdb.drugresistance.reports.TabularRulesComparison;
import edu.stanford.hivdb.drugs.DrugClass;

@Path("hivdb-version")
public class HivdbVersionService {

	@GET
	@Path("rules-comparison/{versionPair}/{drugClass}.tsv")
	public Response getRulesComparison(@PathParam("versionPair") String versionPair, @PathParam("drugClass") String drugClassStr) {
		DrugClass dc = null;
		try {
			dc = DrugClass.valueOf(drugClassStr);
		} catch (IllegalArgumentException | NullPointerException e) {
			return Response
				.status(Response.Status.NOT_FOUND)
				.entity("Drug Class not found: " + dc).build();
		}
		final DrugClass drugClass = dc;
		String output = TabularRulesComparison.getInstance(versionPair, drugClass).toString();
		String[] versions = versionPair.split(":");
		String fileName = String.format(
			"%sRulesComparison.%s.vs.%s.tsv", drugClass,
			versions[0], versions[1]);
		return Response
		.ok(output, "text/tab-separated-values")
		.header("Content-Disposition", "attachment; filename=" + fileName)
		.build();
	}

	@GET
	@Path("patterns-comparison/{versionPair}/{drugClass}.tsv")
	public Response getPatternsComparison(@PathParam("versionPair") String versionPair, @PathParam("drugClass") String drugClassStr) {
		DrugClass dc = null;
		try {
			dc = DrugClass.valueOf(drugClassStr);
		} catch (IllegalArgumentException | NullPointerException e) {
			return Response
				.status(Response.Status.NOT_FOUND)
				.entity("Drug Class not found: " + dc).build();
		}
		final DrugClass drugClass = dc;
		String output = TabularPatternsComparison.getInstance(drugClass).toString();
		String fileName = String.format(
			"%sPatternsComparison.%s.vs.%s.tsv", drugClass,
			TabularPatternsComparison.VERSIONS[0],
			TabularPatternsComparison.VERSIONS[1]);
		return Response
		.ok(output, "text/tab-separated-values")
		.header("Content-Disposition", "attachment; filename=" + fileName)
		.build();
	}

}
