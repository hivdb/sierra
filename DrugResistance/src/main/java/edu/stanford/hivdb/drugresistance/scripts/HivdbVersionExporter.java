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

package edu.stanford.hivdb.drugresistance.scripts;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;

public class HivdbVersionExporter {

	public static void main(String[] args) {
		HivdbVersion latest = HivdbVersion.getLatestVersion();
		Map<String, Object> version = new LinkedHashMap<>();
		version.put("text", latest.toString());
		version.put("publishDate", latest.getPublishDate());
		MyFileUtils.writeFile("__output/hivdb-version.json", Json.dumps(version));
	}

}
