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

public class AlignedSite {
	private final int posAA;
	private final int posNA;
	private final int lengthNA;

	public AlignedSite(int posAA, int posNA, int lengthNA) {
		this.posAA = posAA;
		this.posNA = posNA;
		this.lengthNA = lengthNA;
	}

	public int getPosAA() { return posAA; }
	public int getPosNA() { return posNA; }
	public int getLengthNA() { return lengthNA; }
}
