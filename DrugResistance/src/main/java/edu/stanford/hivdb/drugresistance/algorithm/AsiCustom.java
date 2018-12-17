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

import java.io.ByteArrayInputStream;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;

public class AsiCustom extends AsiBase {

	private final String algorithmName;


	public AsiCustom(Gene gene, MutationSet mutations, String algorithmName, String algorithmXml) {
		super(gene, mutations, new ByteArrayInputStream(algorithmXml.getBytes()));
		this.algorithmName = algorithmName;
	}


	@Override
	public String getAlgorithmName() {
		return algorithmName;
	}

}
