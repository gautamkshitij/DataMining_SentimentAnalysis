
package cs.uic.edu.datamining.CS583;

public class cleanTweets {

	public String clean(String string) throws Exception {

		// Remove <> @ http https numbers
		string = string.replaceAll("<\\w>|<\\/\\w>|@\\w+.|\"|https?.+\\s|(https?.+)$|\\d+|[^\\x00-\\x7F]", "");
		return string;
	}

}
