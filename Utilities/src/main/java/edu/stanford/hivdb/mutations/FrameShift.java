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

package edu.stanford.hivdb.mutations;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class FrameShift implements Comparable<FrameShift> {
	public static enum Type {INSERTION, DELETION};
	private Gene gene;
	private int position;
	private int size;
	private String nas;
	private Type type;

	private FrameShift(Gene gene, int position, int size, String nas, Type type) {
		this.gene = gene;
		this.position = position;
		this.nas = nas;
		this.type = type;
		this.size = Math.abs(size);
	}

	public int compareTo (FrameShift fs) {
		return new Integer(position).compareTo(new Integer(fs.position));
	}

	public static String getHumanReadableList(List<FrameShift> frameShifts) {
		StringBuilder output = new StringBuilder();
		if (frameShifts.size() == 0) {
			output.append("None");
		} else {
			for (FrameShift fs : frameShifts) {
				output.append(getHumanReadable(fs) + ", ");
			}
			output.setLength(output.length() - 2);
		}
		return output.toString();
	}

	public boolean isInsertion() {
		return type == Type.INSERTION;
	}

	public boolean isDeletion() {
		return type == Type.DELETION;
	}


	public static String getHumanReadable(FrameShift fs) {
		String output;
		if (fs.getType() == Type.INSERTION) {
			output = "" + fs.getGene() + fs.getPosition() + "ins" + fs.getSize() + "bp_" + fs.getNAs();
		} else {
			output = "" + fs.getGene() + fs.getPosition() + "del" + fs.getSize() + "bp";
		}
		return output;
	}

	public static FrameShift createDeletion (Gene gene, int aaPosition, int size) {
		return new FrameShift(gene, aaPosition, size, "", Type.DELETION);
	}

	public static FrameShift createInsertion (Gene gene, int aaPosition, int size, String nas) {
		return new FrameShift(gene, aaPosition, size, nas, Type.INSERTION);
	}

	public static FrameShift fromNucAminoFrameShift(Gene gene, int aaStart, Map<?, ?> fs) {
		return new FrameShift(
			gene,
			/* aaPosition */ ((Double) fs.get("Position")).intValue() - aaStart + 1,
			/* size */ ((Double) fs.get("GapLength")).intValue(),
			/* nas */ (String) fs.get("NucleicAcidsText"),
			/* type */ (Boolean) fs.get("IsInsertion") ? Type.INSERTION : Type.DELETION
		);
	}


	public Gene getGene() {return gene;}
	public int getPosition() {return position;}
	public Type getType() {return type;}
	public int getSize() {return size;}
	public String getNAs() {return nas;}

	public String getText() {
		// same as `toString()`, only for GraphQL
		return getHumanReadable(this);
	}

	@Override
	public String toString() {
		return getHumanReadable(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(6434, 15675) // any two random prime numbers
			.append(gene)
			.append(position)
			.append(type)
			.append(size)
			.append(nas)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FrameShift)) {
			return false;
		}
		FrameShift other = (FrameShift) obj;
		return new EqualsBuilder()
			.append(gene, other.getGene())
			.append(position, other.getPosition())
			.append(type, other.getType())
			.append(size, other.getSize())
			.append(nas, other.getNAs())
			.isEquals();
	}

}
