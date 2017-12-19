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

package edu.stanford.hivdb.blast;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BlastXmlOutputParser {
	private  List<String> matchLines = new ArrayList<>();
	private  List<Integer> numMatches = new ArrayList<>();
	private  List<Double> pcntMatches = new ArrayList<>();
	private  List<Integer> medianMismatchPositions = new ArrayList<>();
	private  List<String> subtypes = new ArrayList<>();
	private  List<String> countries = new ArrayList<>();
	private  List<String> genBankAccessions = new ArrayList<>();
	private  List<String> authorYrs = new ArrayList<>();
	private  List<Integer> startNAs = new ArrayList<>();
	private  List<Integer> endNAs = new ArrayList<>();
	private  String tabularOutput;
	private static final Logger LOGGER = LogManager.getLogger();


	public BlastXmlOutputParser (String filePath) throws ParserConfigurationException, SAXException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		SAXBlastHandler handler = new SAXBlastHandler();

		try {
			parser.parse(new FileInputStream(filePath), handler);

			for (SAXBlastResult result : handler.results) {
				LOGGER.debug(result.toString());
				String[] definitionFields = result.hitDefinition.split("\\|");
				numMatches.add(result.numMatches);
				double pcnt = 100.0 * (double) result.numMatches / result.numPositions;
				pcntMatches.add(pcnt);
				matchLines.add(result.matchLine);
				medianMismatchPositions.add(medianMisMatch(result.matchLine));
				subtypes.add(definitionFields[0]);
				countries.add(definitionFields[1]);
				authorYrs.add(definitionFields[2]);
				genBankAccessions.add(definitionFields[4]);
				startNAs.add(Integer.valueOf(definitionFields[6]));
				endNAs.add(Integer.valueOf(definitionFields[7]));

			}

			tabularOutput = createTabularOutput();
			LOGGER.debug("\n" + tabularOutput);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> getNumMatches() { return numMatches; }
	public List<Double> getPcntMatches() { return pcntMatches; }
	public List<Integer> getMedianMismatchPos() { return medianMismatchPositions; }
	public List<String> getSubtypes() { return subtypes; }
	public List<String> getCountries() { return countries; }
	public List<String> getGenBankAccessions() { return genBankAccessions; }
	public List<String> getAuthorYrs() { return authorYrs; }
	public List<Integer> startNAs() { return startNAs; }
	public List<Integer> endNAs() { return endNAs; }
	public String getTabularOutput() { return tabularOutput; }

	private String createTabularOutput() {
		final int numHits = numMatches.size();
		StringBuilder output = new StringBuilder();
		for (int i=0; i<numHits; i++) {
			String row = String.format("%5s %20s %30s %8d %8.1f %8d%n",
					subtypes.get(i), countries.get(i), genBankAccessions.get(i),
					numMatches.get(i), pcntMatches.get(i), medianMismatchPositions.get(i));
			output.append(row);
		}
		return output.toString();
	}

	private static int medianMisMatch (String matchLine) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (int i=0; i<matchLine.length(); i++) {
			if  (matchLine.substring(i, i+1).equals(" ")) {
				stats.addValue(i+1);
			}
		}
		double median = 100 * (double) stats.getPercentile(50) / (double) matchLine.length();
		return (int) median;
	}

}


class SAXBlastHandler extends DefaultHandler {
	List<SAXBlastResult> results = new ArrayList<SAXBlastResult>();
	SAXBlastResult result = null;
	String content = null;

	@Override
	// Triggered when the start of tag is found
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("Hit_def")) {
			result = new SAXBlastResult();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("Hit")) {
			results.add(result);
		} else if (qName.equals("Hit_def")) {
			result.hitDefinition = content;
		} else if (qName.equals("Hsp_identity")) {
			result.numMatches = Integer.parseInt(content);
		} else if (qName.equals("Hsp_align-len")) {
			result.numPositions = Integer.parseInt(content);
		} else if (qName.equals("Hsp_hseq")) {
			result.seqHit=content;
		} else if (qName.equals("Hsp_midline")) {
			result.matchLine = content;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content = String.copyValueOf(ch, start, length).trim();
	}
}

class SAXBlastResult {
	String hitDefinition;
	int numMatches;
	int numPositions;
	String seqHit;
	String matchLine;

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(hitDefinition + "\n");
		output.append("NumMatches:" + numMatches + " numPositions:" + numPositions + "\n");
		output.append(" SeqHit: " + seqHit + "\n");
		output.append("matchLine: " + matchLine + "\n");
		return output.toString();
	}
}


