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
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ExecCommand {
	private static final Logger LOGGER = LogManager.getLogger();


	/**
	 * Run a system command
	 * @param cmd
	 * @return true if there are no errors
	 */
	public static int run(String cmd) {
		try {
			final Runtime rt = Runtime.getRuntime();
			LOGGER.debug("Runtime environment:\n" + rt);

			final Process pr = rt.exec(cmd);
			LOGGER.debug("Commend:\n" + cmd);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

			String line = null;
			LOGGER.debug("Standard output of the command:\n");
			while((line = input.readLine()) != null) {
				LOGGER.debug("Line:" + line);
				System.out.println(line);
			}

			LOGGER.debug("Standard error of the command (if any):\n");
			while((line = error.readLine()) != null) {
				LOGGER.debug("Line:" + line);
				System.out.println(line);
			}

			final int exitVal = pr.waitFor();
			//System.out.println("Exited with error code " + exitVal);
			return exitVal;
		} catch(Exception e) {
			LOGGER.debug("Exception happended:\n");
			LOGGER.debug(e.toString());
			System.out.println(e.toString());
			return -999;
		}
	}
}
