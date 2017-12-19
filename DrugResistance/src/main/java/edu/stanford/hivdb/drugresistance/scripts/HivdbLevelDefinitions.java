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

public enum HivdbLevelDefinitions {
	SUSCEPTIBLE("Susceptible", "S", 1),
	POTENTIAL_LOW_LEVEL("Potential Low-Level Resistance", "S", 2),
	LOW_LEVEL("Low-Level Resistance", "I", 3),
	INTERMEDIATE("Intermediate Resistance", "I", 4),
	HIGH_LEVEL("High-Level Resistance", "R", 5);

	private String description;
	private String sir;
	private int level;

	HivdbLevelDefinitions(final String description, final String sir, final int level) {
		this.description = description;
		this.sir = sir;
		this.level = level;
	}

	public String getDescription() { return description; }
	public String getSir() { return sir; }
	public int getLevel() { return level; }

	public static HivdbLevelDefinitions getByNumber(int level) {
		return HivdbLevelDefinitions.values()[level - 1];
	}

}
