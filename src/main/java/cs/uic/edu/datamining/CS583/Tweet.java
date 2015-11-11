package cs.uic.edu.datamining.CS583;

import java.util.HashMap;

public class Tweet {

	String tweet = "";
	HashMap<String, Integer> unigram = new HashMap<String, Integer>();
	String label = "";
	double[] prob = { 0, 0, 0 };
	int[] unifeatures = null;
	int aifnnScore = 0;
	double sentiScore = 0.0;
}
