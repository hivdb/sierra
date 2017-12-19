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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MyEnumUtilsTest {

	enum TestEnum { A, B, C, D }

	@Test
	public void testJoin() {
		List<TestEnum> enums = new ArrayList<>();
		enums.add(TestEnum.A);
		enums.add(TestEnum.B);
		enums.add(TestEnum.D);
		assertEquals("A,B,D", MyEnumUtils.join(enums));
		assertEquals("A + B + D", MyEnumUtils.join(enums, " + "));
		assertEquals("A+B+D", MyEnumUtils.join(enums, '+'));
		assertEquals(
			"a,b,d", MyEnumUtils.join(enums, e -> e.toString().toLowerCase()));
		assertEquals(
			"a + b + d",
			MyEnumUtils.join(enums, " + ", e -> e.toString().toLowerCase()));
		assertEquals(
			"a+b+d",
			MyEnumUtils.join(enums, '+', e -> e.toString().toLowerCase()));
	}

}
