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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class MyFileUtils {

	public static void writeFile(File file, String output) {
		try {
			file.getParentFile().mkdirs();
		} catch (NullPointerException e) {
			// pass
		}

		try {
			Files.write(
				file.toPath(), output.getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeFile(String filePath, String output) {
		writeFile(new File(filePath), output);
	}

	public static void appendFile(String filePath, String output) {
		File file = new File(filePath);
		try {
			file.getParentFile().mkdirs();
		} catch (NullPointerException e) {
			// pass
		}
		try {
			Files.write(
				file.toPath(), output.getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader readResource(Class<?> classObj, String resourcePath) {
		InputStream stream = classObj.getClassLoader().getResourceAsStream(resourcePath);
		return new BufferedReader(new InputStreamReader(stream));
	}
}
