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

package edu.stanford.hivdb.testutils;

public class SampleSubstitution {

	private final int startPos;
	private final int endPos;
	private final String replaceTo;

	public SampleSubstitution(
			int startPos, int endPos, String replaceTo) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.replaceTo = replaceTo;
	}

	public String apply(String sample) {
		return sample.substring(0, startPos) +
			replaceTo + sample.substring(endPos);
	}

	public StringBuilder apply(StringBuilder sample) {
		return sample.replace(startPos, endPos, replaceTo);
	}
}
