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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementImpl;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;
import net.sf.jfasta.impl.FASTAFileWriter;

public class FastaUtils {
	private FastaUtils() {}

	/**
	 * Cleans up the FASTA file so JFASTA can tolerate errors.
	 *
	 * This method:
	 *  - Removes comments and additional identifier lines;
	 *  - Adds identifier line if absent
	 *
	 * Normally, a FASTA file (or string) can contain one comment line
	 * at the beginning of the file (or string). Note this method removes
	 * all comments following the first one.
	 *
	 * @param stream
	 * @return InputStream
	 */
	private static InputStream cleanup(final InputStream stream) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		List<String> result = new ArrayList<>();
		boolean isPrevIdentLine = false;

		for (String line : reader.lines().toArray(size -> new String[size])) {
			if (line.startsWith("#")) continue;
			if (line.startsWith(">") && isPrevIdentLine) {
				result.remove(result.size() - 1);
			} else if (line.startsWith(">")) isPrevIdentLine = true;
			else isPrevIdentLine = false;
			result.add(line);
		}

		String resultStr = String.join("\n", result);
		if (resultStr.startsWith(" Error")) resultStr = "";
		if (!(resultStr.startsWith(">") || resultStr.isEmpty())) {
			resultStr = ">UnnamedSequence\n" + resultStr;
		}
		return new ByteArrayInputStream(resultStr.getBytes());
	}

	/**
	 * Fetches a list of Genbank nucleotide sequences
	 *
	 * @param accessions
	 * @return List<Sequence>
	 */
	public static List<Sequence> fetchGenbank(Collection<String> accessions) {
		String baseUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi";
		HttpResponse<InputStream> response;
		try {
			response = Unirest.post(baseUrl)
				.queryString("db", "nucleotide")
				.queryString("rettype", "fasta")
				.queryString("retmode", "text")
				.field("id", String.join(",", accessions))
				.asBinary();
		} catch (UnirestException e) {
			return Collections.emptyList();
		}
		return readStream(response.getBody());
	}

	/**
	 * Reads in a file with FASTA sequences. Create a list of Sequences
	 * each containing a string of NAs and a header.
	 * The NAs can be on multiple lines. Each line (absent whitespace) is added to
	 *  the sequence until '>' is encountered
	 * @param filePath
	 * @return List<Sequence>
	 */
	public static List<Sequence> readFile(String filePath) {
		final File file = new File(filePath);
		try {
			InputStream stream = new FileInputStream(file);
			return readStream(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads in a input stream with FASTA sequences. Create a list of Sequences
	 * each containing a string of NAs and a header.
	 * The NAs can be on multiple lines. Each line (absent whitespace) is added to
	 * the sequence until '>' is encountered
	 * @param filePath
	 * @return List<Sequence>
	 */
	public static List<Sequence> readStream(InputStream stream) {
		final List<Sequence> sequences = new ArrayList<>();
		try (
			final FASTAFileReader reader =
				new FASTAFileReaderImpl(cleanup(stream))
		) {
			final FASTAElementIterator it = reader.getIterator();
			while (it.hasNext()) {
				final Sequence seq = new Sequence(it.next());
				sequences.add(seq);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return sequences;
	}

	public static List<Sequence> readString(String inputString) {
		InputStream stream = new ByteArrayInputStream(inputString.getBytes());
		return readStream(stream);
	}

	public static void writeStream(Collection<Sequence> sequences, OutputStream stream) {
		try (
			FASTAFileWriter writer = new FASTAFileWriter(stream);
		) {
			for (Sequence seq : sequences) {
				writer.write(new FASTAElementImpl(
					seq.getHeader(), seq.getSequence()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeFile(Collection<Sequence> sequences, String filePath) {
		File out = new File(filePath);
		try (
			FASTAFileWriter writer = new FASTAFileWriter(out)
		) {
			for (Sequence seq : sequences) {
				writer.write(new FASTAElementImpl(
					seq.getHeader(), seq.getSequence()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeFile(Sequence sequence, String filePath) {
		List<Sequence> sequences = new ArrayList<>();
		sequences.add(sequence);
		writeFile(sequences, filePath);
	}

	public static String writeString(Collection<Sequence> sequences) {
		OutputStream stream = new ByteArrayOutputStream();
		writeStream(sequences, stream);
		return stream.toString();
	}
}
