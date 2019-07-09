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

package edu.stanford.hivdb.utilities;

import java.util.Arrays;

public class MyStringUtils {

	/**
	 * Only for static access. DO NOT instantiate this class
	 */
	private MyStringUtils() {}

	/**
	 * Returns true if two strings share one or more characters
	 * @param aas1, aas2
	 * @return boolean
	 */
	public static boolean hasSharedChar(String aas1, String aas2) {
		for (char aa1 : aas1.toCharArray()) {
			for (char aa2 : aas2.toCharArray()) {
				if (aa1 == aa2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns alphabetically sorted string of input.
	 *
	 * Example:
	 *   "gfedcba" => "abcdefg"
	 *
	 * @param input
	 * @return string
	 */
	public static String sortAlphabetically(String input) {
		char[] chars = input.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	

}
