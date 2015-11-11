package cs.uic.edu.datamining.CS583;

import java.util.HashMap;

public class DocumentProperties {

	int no_of_tokens = 0;
	HashMap<String, Integer> unigramDoc = new HashMap<String, Integer>();
	HashMap<String, Integer> freq_pos = new HashMap<String, Integer>();
	HashMap<String, Integer> freq_neg = new HashMap<String, Integer>();
	HashMap<String, Integer> freq_neu = new HashMap<String, Integer>();
}
