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

package edu.stanford.hivdb.genotypes;

import edu.stanford.hivdb.genotypes.BoundGenotype;
import edu.stanford.hivdb.genotypes.Genotype;
import edu.stanford.hivdb.hivfacts.HIV;

@Deprecated
public enum Subtype {
	A,
	A2,
	B,
	C,
	D,
	F,
	F2,
	G,
	H,
	J,
	K,
	X01,
	X02,
	X03,
	X04,
	X05,
	X06,
	X07,
	X08,
	X09,
	X10,
	X11,
	X12,
	X13,
	X14,
	X15,
	X16,
	X17,
	X18,
	X19,
	X20,
	X21,
	X22,
	X23,
	X24,
	X25,
	X26,
	X27,
	X28,
	X29,
	X30,
	X31,
	X32,
	X33,
	X34,
	X35,
	X36,
	X37,
	X38,
	X39,
	X40,
	X41,
	X42,
	X43,
	X44,
	X45,
	X46,
	X47,
	X48,
	X49,
	X50,
	X51,
	X52,
	X53,
	X54,
	X55,
	X56,
	X57,
	X58,
	X59,
	X60,
	X61,
	X62,
	X63,
	X64,
	X65,
	X66,
	X67,
	X68,
	X69,
	X70,
	X71,
	X72,
	X73,
	X74,
	X77,
	X78,
	X82,
	X83,
	X85,
	X86,
	X87,
	U,
	O,
	N,
	P,
	HIV2A,
	HIV2B,
	HIV2C,
	HIV2D,
	HIV2E,
	HIV2F,
	HIV2G,
	HIV2H,
	HIV2I,
	HIV2X01;

	public static Subtype valueOf(Genotype<HIV> genotype) {
		return valueOf(genotype.getIndexName());
	}

	public static Subtype valueOf(BoundGenotype<HIV> genotype) {
		return valueOf(genotype.getGenotype().getIndexName());
	}
}










