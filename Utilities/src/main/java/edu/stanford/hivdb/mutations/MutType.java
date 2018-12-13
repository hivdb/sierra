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

import java.util.Set;
import java.util.EnumSet;
import java.util.Collections;

public enum MutType {
	NRTI, NNRTI, Major, Other, Accessory;

	private static final Set<MutType> DRM_TYPES;

	static {
		Set<MutType> drmTypes = EnumSet.of(NRTI, NNRTI, Major);
		DRM_TYPES = Collections.unmodifiableSet(drmTypes);
	}

	public boolean isDRMType() {
		return DRM_TYPES.contains(this);
	}
}
