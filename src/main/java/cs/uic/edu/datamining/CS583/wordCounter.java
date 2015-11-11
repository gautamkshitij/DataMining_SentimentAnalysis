package cs.uic.edu.datamining.CS583;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.process.DocumentPreprocessor;

public class wordCounter {

	ArrayList<Tweet> tweet = new ArrayList<Tweet>();

	public wordCounter() throws Exception {
		super();
	}

	DocumentProperties dp = new DocumentProperties();

	String[] classes = { "positive", "negative", "neutral" };

	public void count() throws Exception {
		System.out.println("preprocessing...");
		while (stopword != null) {
			stp.add(stopword);
			stopword = brestop.readLine();
		}

		DocumentPreprocessor dpr;
		HashMap<String, Integer> freq = new HashMap<String, Integer>();
		HashMap<String, Integer> freq_pos = new HashMap<String, Integer>();
		HashMap<String, Integer> freq_neg = new HashMap<String, Integer>();
		HashMap<String, Integer> freq_neu = new HashMap<String, Integer>();
		HashMap<String, Integer> temp = null;

		Tweet tw = null;

		for (String file : classes) {
			System.out.println("tokenizing " + file + "...");
			dpr = new DocumentPreprocessor(sentiAnalysis.DIR.concat(file));
			for (List sentence : dpr) {
				tw = new Tweet();
				temp = new HashMap<String, Integer>();

				for (Object word : sentence.toArray()) {
					String test = word.toString();
					test = replace(test); // replace abbreviations
					if (!test.matches("\\W") && !test.contains("obama") && !test.contains("romney")
							&& isNotStopword(test)) {

						if (!temp.containsKey(test)) {
							temp.put(test, 1);
						}

						if (!freq.containsKey(test)) {
							freq.put(test, 1);
						}

						if (freq.containsKey(test)) {
							freq.put(test, freq.get(test) + 1);
						}
						if (temp.containsKey(test)) {
							temp.put(test, temp.get(test) + 1);
						}

						if (file == "positive") {

							if (!freq_pos.containsKey(test)) {
								freq_pos.put(test, 1);
							}

							if (freq_pos.containsKey(test)) {
								freq_pos.put(test, freq_pos.get(test) + 1);
							}
						} else if (file == "negative") {

							if (!freq_neg.containsKey(test)) {
								freq_neg.put(test, 1);
							}

							if (freq_neg.containsKey(test)) {
								freq_neg.put(test, freq_neg.get(test) + 1);
							}
						} else if (file == "neutral") {

							if (!freq_neu.containsKey(test)) {
								freq_neu.put(test, 1);
							}

							if (freq_neu.containsKey(test)) {
								freq_neu.put(test, freq_neu.get(test) + 1);
							}
						}

					}
				}
				tw.unigram = temp;
				tw.label = file;
				tw.tweet = sentence.toString();
				tweet.add(tw);
				temp.clear();
			}
		}

		int sum = 0;
		for (String iter : freq.keySet()) {
			sum = sum + freq.get(iter);
		}

		dp.no_of_tokens = sum;
		dp.unigramDoc = freq;
		dp.freq_pos = freq_pos;
		dp.freq_neg = freq_neg;
		dp.freq_neu = freq_neu;

		genFeatures feat = new genFeatures();
		feat.genFeat(tweet, dp, sum);

		runAlgorithm algo = new runAlgorithm();
		algo.run(tweet, dp);
		algo.runFilter();

	}

	FileReader stop = new FileReader(sentiAnalysis.DIR.concat("stopwords.txt")); // stop-words
																					// dictionary
																					// from
																					// webconfs.com
	BufferedReader brestop = new BufferedReader(stop);
	String stopword = brestop.readLine();
	ArrayList<String> stp = new ArrayList<String>();

	private boolean isNotStopword(String test) {
		// TODO Auto-generated method stub
		if (stp.contains(test))
			return false;
		else
			return true;
	}

	// Abbreviation Dictionary
	String abbrdict = "/home/jitesh/Desktop/tweets/abbr.txt"; // abbreviation
																// dictionary
																// from
																// noslang.com
	HashMap<String, String> abbr = createAbbrDict(abbrdict);

	public String replace(String string) {
		// TODO Auto-generated method stub
		String[] temp = string.split("\\s+");
		for (String word : temp) {
			for (String key : abbr.keySet()) {
				if (word.equals(key)) {
					string = string.replaceAll(key, abbr.get(key).toString());
				}
			}
		}

		return string;
	}

	HashMap<String, String> createAbbrDict(String abbrdict) throws Exception {
		HashMap<String, String> abbr = new HashMap<String, String>();
		FileReader abrd = new FileReader(abbrdict);
		BufferedReader brd = new BufferedReader(abrd);
		String abbrd = brd.readLine();

		while (abbrd != null) {
			String[] temp = abbrd.split("-");
			abbr.put(temp[0].trim(), temp[1].trim());
			abbrd = brd.readLine();
		}
		brd.close();
		return abbr;
	}

}
