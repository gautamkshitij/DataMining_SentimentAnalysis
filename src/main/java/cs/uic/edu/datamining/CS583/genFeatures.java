package cs.uic.edu.datamining.CS583;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class genFeatures {

	
	
	public void genFeat(ArrayList<Tweet> tweet, DocumentProperties dp, int sum) throws Exception{
		
		System.out.println("calculating features...");
		String[] feat=new String[dp.unigramDoc.size()];
		int[] fval=null;
		
		int index=0;
		
		for(String doc : dp.unigramDoc.keySet()){
			feat[index]=doc;
			index++;
		}
		
		for(int i=0; i<tweet.size(); i++){
			fval= new int[dp.unigramDoc.size()];
			for(int j=0; j<fval.length; j++){
				
				if(tweet.get(i).tweet.contains(feat[j])){
					fval[j]=1;
				}
				else fval[j]=0;
				
			}
		tweet.get(i).unifeatures=fval;
		
		
		tweet.get(i).prob=calcProb(tweet.get(i).tweet, dp);
		
		
		tweet.get(i).aifnnScore=calcAIFNN(tweet.get(i).tweet);
		
		
		tweet.get(i).sentiScore=calcSentiWordNetScore(tweet.get(i).tweet);
		}
				
		
	}
	
	
	
	

	private double calcSentiWordNetScore(String tweet) throws IOException {
		
		HashMap<String, Double> senti= createSentiWordDict();
		tweet=tweet.replaceAll("\\[|\\]", "");
		String[] temp=tweet.split(",");
		double score=0;
		
		for(String word : temp){
			if(senti.containsKey(word)){
				score=score+senti.get(word);
			}
		}
		
		return score;
	}





	private HashMap<String, Double> createSentiWordDict() throws IOException {

		HashMap<String, Double> senti=new HashMap<String, Double>();
		FileReader frsenti=new FileReader(sentiAnalysis.DIR.concat("sentiwordnet.txt"));
		BufferedReader brsenti=new BufferedReader(frsenti);
		String sentiword=brsenti.readLine();
		
		while(sentiword!=null){
			
			
			if(sentiword.startsWith("a")){
				String[] sentiwordnet=sentiword.split("\t");
				String[] words=sentiwordnet[4].split("\\s");
				for(String word : words){
					word=word.replace("#", "");
					word=word.replaceAll("\\d", "");
					senti.put(word, 1-(Double.parseDouble(sentiwordnet[2])+Double.parseDouble(sentiwordnet[3])));
				}
			}
			
			
			sentiword=brsenti.readLine();
		}
		
		brsenti.close();
		return senti;
	}





	private int calcAIFNN(String tweet) throws IOException {
		FileReader frafinn=new FileReader(sentiAnalysis.DIR.concat("afinn.txt"));
		BufferedReader afinn=new BufferedReader(frafinn);
		String line=afinn.readLine();
		HashMap<String, Integer> afinnwords=new HashMap<String, Integer>();
		while(line!=null){
			
			String[] afinnword=line.split("\t");
			afinnwords.put(afinnword[0], Integer.parseInt(afinnword[1]));
			line=afinn.readLine();
		}
		
		tweet=tweet.replaceAll("\\[|\\]", "");
		String[] temp=tweet.split(",");
		int score=0;
		
		for(String word : temp){
			if(afinnwords.containsKey(word)){
				score=score+afinnwords.get(word);
			}
		}
		return score;
	}

	
	
	
	private double[] calcProb(String tweet, DocumentProperties dp) {
		
		double pos=0.5;
		double neg=0.5;
		double neu=0.5;
		
		int sum_pos=0;
		for(String iter : dp.freq_pos.keySet()){
			sum_pos=sum_pos+dp.freq_pos.get(iter);
		}	
		int sum_neg=0;
		for(String iter : dp.freq_neg.keySet()){
			sum_neg=sum_neg+dp.freq_neg.get(iter);
		}	
		
		int sum_neu=0;
		for(String iter : dp.freq_neu.keySet()){
			sum_neu=sum_neu+dp.freq_neu.get(iter);
		}	
	
		
		tweet=tweet.replaceAll("\\[|\\]", "");
		String[] temp=tweet.split(",");
		//DecimalFormat df = new DecimalFormat("#.#####");
		
		for (String word : temp){
			word=word.trim();
			if(dp.freq_pos.containsKey(word)){
				pos=pos*dp.freq_pos.get(word)/sum_pos;
			}
			if(dp.freq_neg.containsKey(word)){
				neg=neg*dp.freq_neg.get(word)/sum_neg;
			}
			if(dp.freq_neu.containsKey(word)){
				neu=neu*dp.freq_neu.get(word)/sum_neu;
			}
		}
		
		double[] ret={pos, neg, neu};
		
		return ret;
	}
	
}
