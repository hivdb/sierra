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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProgramProperties {
	private static final InputStream PROPERTIES_STREAM = ProgramProperties.class.getClassLoader().getResourceAsStream("utilities.properties");
	private static Properties properties = new Properties();

	static {
		try {
			properties.load(PROPERTIES_STREAM);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty (String key) {
		String value = (properties.getProperty(key));
		return value;
	}





}
