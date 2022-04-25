/*

    Copyright (C) 2021 Stanford HIVDB team

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

import java.io.IOException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

	@Override
	public void filter(
  	ContainerRequestContext request,
  	ContainerResponseContext response
  ) throws IOException {
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHeaders().add(
			"Access-Control-Allow-Headers",
			"CSRF-Token, X-Requested-By, Authorization, Content-Type, apollographql-client-name, apollographql-client-version"
		);
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add(
			"Access-Control-Allow-Methods",
			"GET, POST, PUT, DELETE, OPTIONS, HEAD"
		);
	}

}