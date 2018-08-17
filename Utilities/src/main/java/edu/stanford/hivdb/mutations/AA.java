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

public class AA {

	private AA() {}

	public static String toHIVDBFormat(String input) {
		System.out.println("testing Travis on forked repo");
		return input
			.replace("Insertion", "#")
			.replace("Deletion", "~")
			.replace("ins", "#")
			.replace("del", "~")
			.replace('i', '#')
			.replace('_', '#')
			.replace('d', '~')
			.replace('-', '~');
	}

	public static String toInternalFormat(String input) {
		return input
			.replace("Insertion", "_")
			.replace("Deletion", "-")
			.replace("ins", "_")
			.replace("del", "-")
			.replace('i', '_')
			.replace('#', '_')
			.replace('d', '-')
			.replace('~', '-');
	}

	public static String toASIFormat(String input) {
		return input
			.replace("Insertion", "i")
			.replace("Deletion", "d")
			.replace("ins", "i")
			.replace("del", "d")
			.replace('#', 'i')
			.replace('_', 'i')
			.replace('~', 'd')
			.replace('-', 'd');
	}

}


