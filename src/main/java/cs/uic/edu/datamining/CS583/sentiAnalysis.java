package cs.uic.edu.datamining.CS583;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class sentiAnalysis {

	static String DIR = "/home/jitesh/Desktop/tweets/";
	static String input = "romney.csv";
	static String outout = input.replace(".csv", "");
	static int mode = 0; // mode 0: train, mode 1: test

	public static void main(String args[]) throws Exception {

		// Required objects
		cleanTweets clt = new cleanTweets();

		// Reading File input csv file
		FileReader fr = new FileReader(DIR.concat(input));
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		// Writing pos, neg and neu because tokenizer needs file path as input
		FileWriter positive = new FileWriter(DIR.concat("positive"));
		FileWriter negative = new FileWriter(DIR.concat("negative"));
		FileWriter neutral = new FileWriter(DIR.concat("neutral"));

		// splitting data and taking only from above 3 classes
		System.out.println("cleaning and separating tweets...");
		while (line != null) {
			String[] tweets = line.split("\t");
			tweets[0] = clt.clean(tweets[0].toLowerCase()); // removing html
															// tags, URLs, name
															// tags

			if (!tweets[1].trim().contains("irrevelant") && !tweets[1].trim().contains("IR")
					&& !tweets[1].trim().contains("!!!!")) {
				if (Integer.parseInt(tweets[1].trim()) == 1) {
					positive.append(tweets[0] + "\n");
				} else if (Integer.parseInt(tweets[1].trim()) == 0) {
					neutral.append(tweets[0] + "\n");
				} else if (Integer.parseInt(tweets[1].trim()) == -1) {
					negative.append(tweets[0] + "\n");
				}
			}
			line = br.readLine();
		}
		br.close();
		positive.close();
		negative.close();
		neutral.close();

		// calling word counter for unigram generation
		wordCounter wc = new wordCounter();
		wc.count();

	}

}
