package cs.uic.edu.datamining.CS583;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Seperate {

	public ArrayList<String> seperateTweets(String filename, String label) throws IOException {

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		ArrayList<String> cleaned = null;

		while (line != null) {

			cleaned.add(cleanTweet(line));

		}

		return null;

	}

	private String cleanTweet(String line) {

		return line;

	}

}
