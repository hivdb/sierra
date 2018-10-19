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

package edu.stanford.hivdb.drugresistance.database;

import java.io.InputStream;

public enum HivdbVersion {
	V7_0("AlgXMLs/HIVDB_7.0.xml", "2014-02-27", "7.0"),
	V7_5("AlgXMLs/HIVDB_7.5.xml", "2015-03-16", "7.5"),
	V7_6("AlgXMLs/HIVDB_7.6.xml", "2015-03-22", "7.6"),
	V7_8("AlgXMLs/HIVDB_7.8.xml", "2016-05-22", "7.8"),
	V7_9("AlgXMLs/HIVDB_7.9.xml", "2016-05-27", "7.9"),
	V7_10("AlgXMLs/HIVDB_7.10.xml", "2016-05-31", "7.10"),
	V8_0("AlgXMLs/HIVDB_8.0.xml", "2016-06-01", "8.0"),
	V8_0_1("AlgXMLs/HIVDB_8.0.1.xml", "2016-06-08", "8.0.1"),
	V8_1("AlgXMLs/HIVDB_8.1.xml", "2016-09-15", "8.1"),
	V8_1_1("AlgXMLs/HIVDB_8.1.1.xml", "2016-09-23", "8.1.1"),
	V8_2("AlgXMLs/HIVDB_8.2.xml", "2016-12-09", "8.2"),
	V8_3("AlgXMLs/HIVDB_8.3.xml", "2017-03-02", "8.3"),
	V8_4("AlgXMLs/HIVDB_8.4.xml", "2017-06-16", "8.4"),
	V8_5("AlgXMLs/HIVDB_8.5.xml", "2018-04-16", "8.5"),
	V8_6("AlgXMLs/HIVDB_8.6.xml", "2018-07-03", "8.6"),
	V8_6_1("AlgXMLs/HIVDB_8.6.1.xml", "2018-07-18", "8.6.1"),
	V8_7("AlgXMLs/HIVDB_8.7.xml", "2018-10-19", "8.7");

	public final String resourcePath;
	public final String versionDate;
	public final String readableVersion;

	private HivdbVersion(
			String resourcePath, String versionDate, String readableVersion) {
		this.resourcePath = resourcePath;
		this.versionDate = versionDate;
		this.readableVersion = readableVersion;
	}

	public String getFullName() {
		return "HIVdb " + this.readableVersion;
	}

	public String getPublishDate() {
		return this.versionDate;
	}

	@Override
	public String toString() {
		return this.readableVersion;
	}

	public static HivdbVersion getPrevMajorVersion() {
		return V7_0;
	}

	public static HivdbVersion[][] getVersionComparisonPairs() {
		return new HivdbVersion[][] {
			new HivdbVersion[] {
				getPrevMajorVersion(),
				getLatestVersion()
			},
			new HivdbVersion[] {
				V8_6_1, V8_7
			}
		};
	}

	public static HivdbVersion getLatestVersion() {
		return V8_7;
	}

	public InputStream getResource() {
		return HivdbVersion.class.getClassLoader().getResourceAsStream(this.resourcePath);
	}
}
