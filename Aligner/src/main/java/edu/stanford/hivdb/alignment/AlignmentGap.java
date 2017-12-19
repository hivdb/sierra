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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class AlignmentGap {
	public enum AlignmentGapType {
		INSERTION,
		DELETION,
		FRAME_SHIFT_INSERTION,
		FRAME_SHIFT_DELETION;
	}

	private int gapPos;
	private int gapFixedPos;
	private int gapSize;
	private int ordinal;
	private AlignmentGapType gapType;

	public AlignmentGap(
			int pos, int fixedPos, int size, int ordinal,
			AlignmentGapType gapType) {
		this.gapPos = pos;
		this.gapFixedPos = fixedPos;
		this.gapSize = size;
		this.ordinal = ordinal;
		this.gapType = gapType;
	}

	public AlignmentGap(
			int pos, int fixedPos, int size,
			AlignmentGapType gapType) {
		this(pos, fixedPos, size, 1, gapType);
	}

	@Override
	public String toString() {
		String output = "";
		output += "NA Position: " + gapPos + "   ";
		output += "NA Position (fixed): " + gapFixedPos + "   ";
		output += "Type: " + gapType + "   ";
		output += "Size: " + gapSize + "   ";
		output += "Ordinal: " + ordinal;
		return output;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(423, 62) // any two random prime numbers
			.append(gapPos)
			.append(gapFixedPos)
			.append(gapType)
			.append(ordinal)
			.append(gapSize)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AlignmentGap)) {
			return false;
		}
		AlignmentGap other = (AlignmentGap) obj;
		return new EqualsBuilder()
			.append(gapPos, other.getGapPos())
			.append(gapFixedPos, other.getGapFixedPos())
			.append(gapType, other.getGapType())
			.append(ordinal, other.getOrdinal())
			.append(gapSize, other.getGapSize())
			.isEquals();
	}

	public int getOrdinal() {
		return ordinal;
	}

	public int getGapPos() {
		return gapPos;
	}

	public int getGapFixedPos() {
		return gapFixedPos;
	}

	public void setGapPos(int pos) {
		gapFixedPos += pos - gapPos;
		this.gapPos = pos;
	}

	public int getGapSize() {
		return gapSize;
	}
	public AlignmentGapType getGapType() {
		return gapType;
	}

}

