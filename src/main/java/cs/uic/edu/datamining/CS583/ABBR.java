package cs.uic.edu.datamining.CS583;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class ABBR {

	public ABBR() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	String abbrdict = "/home/jitesh/Desktop/tweets/abbr.txt";
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
