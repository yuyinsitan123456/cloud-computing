package pp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.apache.spark.api.java.function.Function;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class GetlabeledTweet1{
	 List<JsonObject> objs;
	 String[] positivehashtags = {"mentalhealth","recovery","courage","courageous","empowerment","empower",
			 "empoweryourself","motivationalquotes","positivethinking","positivequotes","positivity","positivevibrations",
			 "onelove","onelife","oneway","onelive","bless","likeforlike","support","proud","positive","blessed",
			 "peace","beauty","finest","fashion","motivation","happy","lovelife","passion","love","mentalhealthawareness",
			 "hardworkingkids","youthpower","promotinghealth","helpothers","healthylife","hello","hi","goodday",
			 "shouldbegood","firstmeet","smile","lovemyfriends","beautiful","homesweethome","great","maybeparty",
			 "newme","betterme","kiss","believe","positivemind","positivethought","positivepeople","blondes","mylove",
			 "happiness","findyoursoul","findyoursmile","spiritual","positivevibetribe","affirmation","harmony",
			 "mygym","motivate","strength","focus","focused","beastmode","gym","selfmotivated","positiveattitude",
			 "training","trainingpartner","miracles","grow","weighttraining","gettingitdone","nice","indipendent",
			 "journey","stayhealthy","getstrong","active","goodhabits","goodjob","excercise","excellent","inspiration",
			 "optimism","cleanse","relax","inspire","spirit","healing","energies"};
	 String[] negativehagstags = {"negative","police","drugtest","trueface","followforfollow","negativfilm",
			 "negativity","eccentric","restarted","chemistry","people","nope","sentiment","latergram","drawing","fuck",
			 "fucking","fucked","shit","damn","monday","entrepreneurs","entrepreneurship","keeplearning","limitless",
			 "loyaltyiseverything","selfish","determinacao","sleep","wakeup","depression","suicide","anxiety",
			 "wanttodie","selfharm","sadness","unhappy","selfinjury","worthless","goals","lifegoals",
			 "luggage","pay","unnecessary","money","cost","extra","shouldnt","mediumformat",
			 "mediumformatfilm","geometric","minimalzine","learnminimalism","minimalism","minimalmood"};
	 int totaltweets = 0;
	 int positivetweets = 0;
	 int negativetweets = 0;
	 String polarity = "none";
	 public String readStream(String tweet)throws FileNotFoundException, IOException {
		 StopWords sw = new StopWords();
		 RemoveNoise rn = new RemoveNoise();
		 StringBuffer sb;
		 String labeledTweet = "";
		// String a ="";
		 JsonReader reader =  new JsonReader(new StringReader(tweet));
		/* InputStream fis = new FileInputStream(fileName2);	 
	     JsonReader reader = new JsonReader(new InputStreamReader(fis, "UTF-8"));*/
	    // WriteFiles wf = new WriteFiles(fileName3);
	    
	     reader.setLenient(true);
	 	   
        	reader.beginObject();
	     
	       		 	String texttemp = "";
	       		 	int signal = 0;      		
	        		while(reader.hasNext()){
	        			
	        			String s22 = reader.nextName();
	        			if(s22.equals("text")){
	        				
	        				texttemp = reader.nextString().toLowerCase();
	        				
	        				if(texttemp.contains("🐼")||texttemp.contains("😄")||texttemp.contains("😊")||
	        						texttemp.contains("😍")||texttemp.contains("😝")||texttemp.contains("😃")||
	        						texttemp.contains("🤣")||texttemp.contains("🙂")){
	        					
	        					sb = new StringBuffer();
	        					String[] temp = texttemp.split("\\s+");
	        					temp = rn.removeNoise(temp);
	        					for(int i = 0; i < temp.length; i++){
	        						 sb. append(temp[i]);
	        						 sb. append(" ");
	        					}
	        					texttemp = sb.toString();
	        					
	        					this.polarity = "positive";
	        					//wf.writeFile(texttemp,"#positive");	
	        					labeledTweet = texttemp+"  #"+this.getPolarity();
	        					this.positivetweets = this.positivetweets +1;
   		 						signal = 1;
	        				}else if(texttemp.contains("😡")||texttemp.contains("😫")||texttemp.contains("😣")||
	        						texttemp.contains("😤")||texttemp.contains("☹️")||texttemp.contains("😒")){
	        					sb = new StringBuffer();
	        				
	        					String[] temp = texttemp.split("\\s+");
	        					temp = rn.removeNoise(temp);
	        					for(int i = 0; i < temp.length; i++){
	        						 sb. append(temp[i]);
	        						 sb. append(" ");
	        					}
	        					texttemp = sb.toString();
	        					this.polarity = "negative";
	        					//wf.writeFile(texttemp,"#negative");	
	        					labeledTweet = texttemp+"  #"+this.getPolarity();
	        					this.negativetweets = this.negativetweets +1;
   		 						signal = 1;
	        				}
	        			}else if(!s22.equals("entities")&&!s22.equals("text")){
	        				reader.skipValue();
	        			}else if(s22.equals("entities")){
	        				JsonToken check2 = reader.peek();
    		        		if (check2 == JsonToken.NULL) {   		        	
    		       			 	reader.skipValue();   		       			 
    		       		 	}else{	        				
	        				reader.beginObject();
	        				while(reader.hasNext()){
	        					String s222 = reader.nextName();
	        					if(!s222.equals("hashtags")){
	        						reader.skipValue();
	        					}else if(s222.equals("hashtags")){
	        						JsonToken check3 = reader.peek();
	        		        		if (check3 == JsonToken.NULL) {	        		        	
	        		       			 	reader.skipValue();	        		       			 	
	        		       		 	}else{
	        		       		 		reader.beginArray();
	        		       		 		int totalscore = 0;
	        		       		 		while(reader.hasNext()){
	        		       		 			reader.beginObject();	 
	        		       		 			while(reader.hasNext()){
	        		       		 				String s2222 = reader.nextName();
	        		       		 				if(!s2222.equals("text")){
	        		       		 					reader.skipValue();	 
	        		       		 				}else if(s2222.equals("text")){
	        		       		 					String hashtagss = reader.nextString();
	        		       		 					for(int i=0;i<positivehashtags.length;i++){
	        		       		 						if(positivehashtags[i].equals(hashtagss)){
	        		       		 							totalscore = totalscore+1;
	        		       		 						}
	        		       		 					}
	        		       		 					for(int i=0;i<negativehagstags.length;i++){
	        		       		 						if(negativehagstags[i].equals(hashtagss)){
	        		       		 							totalscore = totalscore-1;
	        		       		 						}
	        		       		 					}
	        		       		 					
	        		       		 				}
	        		       		 			}
	        		       		 			reader.endObject();
	        		       		 		}
	        		       		 		reader.endArray();
	        		       		 		if(signal == 0){
	        		       		 			sb = new StringBuffer();
	        		       		 			String[] temp = texttemp.split("\\s+");
	        		       		 			temp = rn.removeNoise(temp);
	        		       		 			for(int i = 0; i < temp.length; i++){
	        		       		 				sb. append(temp[i]);
	        		       		 				sb. append(" ");
	        		       		 			}
	        		       		 			texttemp = sb.toString();
	        		       		 			if(totalscore>0&&!texttemp.trim().equals("")){	        		       		 					
	        		       		 				//wf.writeFile(texttemp,"#positive");
	        		       		 				this.polarity = "positive";
	        		       		 				labeledTweet = texttemp+"  #"+this.getPolarity();
	        		       		 				this.positivetweets = this.positivetweets +1;
	        		       		 			}else if(totalscore<0&&!texttemp.trim().equals("")){
	        		       		 				//wf.writeFile(texttemp,"#negative");
	        		       		 				this.polarity = "negative";
	        		       		 				labeledTweet = texttemp+"  #"+this.getPolarity();
	        		       		 				this.negativetweets = this.negativetweets + 1;
	        		       		 			}
	        		       		 		}
	        		       		 	}
	        					}
	        				}
	        				reader.endObject();
    		       		 }
	        		}
	        	}
	        	reader.endObject();
	       
    	return labeledTweet;
	     //wf.closeWrite();
	    // fis.close();
	 }
	 public int getTotalTweets(){
		 return this.totaltweets;
	 }
	 public int getPositiveTweet(){
		 return this.positivetweets;
	 }
	 public int getNegativeTweet(){
		 return this.negativetweets;
	 }
	 public String getPolarity(){
		 return this.polarity;
	 }
	/* public static void main(String[] args) throws FileNotFoundException, IOException{
		 String file2 = "C:/JAVA/mywork/workspace/cloud/src/smallTwitter.json";
		 String file3 = "C:/Users/ChangLong/Desktop/cloud/test.txt";
		 readStream(file2,file3);
		 System.out.println("totaltweets: "+totaltweets);
		 System.out.println("positivetweets: "+positivetweets);
		 System.out.println("negativetweets: "+negativetweets);
		 StopWords sw = new StopWords();
		 RemoveNoise rn = new RemoveNoise();
		 StringBuffer sb;
	
		 String[] ww = {"a","ccc","http://t.co/yaABu2reuI","http://t.co/yaABu2reuI1","#DFSDFSDF","#makeup"};
		 sb = new StringBuffer();
 			String[] temp = sw.removeStopWords(ww);
 			temp = rn.removeNoise(temp);
 			for(int i = 0; i < temp.length; i++){
 				sb. append(temp[i]);
 				sb. append(" ");
 			}
 			System.out.println(sb.toString());
	 }*/
}
