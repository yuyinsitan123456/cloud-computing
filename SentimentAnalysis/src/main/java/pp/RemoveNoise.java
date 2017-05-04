package pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveNoise {
	static String regexPattern1 = ".*(https?|ftp)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]*.*\"*\n*.*";
	static Pattern pattern1 = Pattern.compile(regexPattern1);
	String regexPattern2 = ".*#.*";
	Pattern pattern2 = Pattern.compile(regexPattern2);
	String regexPattern3 = ".*@.*";
	Pattern pattern3 = Pattern.compile(regexPattern3);
	String regexPattern4 = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+";
	Pattern pattern4 = Pattern.compile(regexPattern4);
	public String[] removeNoise(String[] words) {
		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(words));
		for (int i = 0; i < tokens.size();) {
			String temp;
			int signal = 0;
			Matcher matcher1 = pattern1.matcher(tokens.get(i));
			Matcher matcher2 = pattern2.matcher(tokens.get(i));
			Matcher matcher3 = pattern3.matcher(tokens.get(i));
			Matcher matcher4 = pattern4.matcher(tokens.get(i));
			if(tokens.get(i).contains("#")){
				if(tokens.get(i).charAt(0)!='#'){
					temp = tokens.get(i).split("#")[0].trim();
					tokens.set(i, temp);
					Matcher matcher44 = pattern4.matcher(temp);
					if(temp.contains("@")&&matcher44.matches()){
						tokens.remove(i);
						signal = 1;
		            }
				}
            }
			if(tokens.get(i).contains("@")&&!matcher4.matches()){
				if(tokens.get(i).charAt(0)!='@'){
					String temp1 = tokens.get(i).split("@")[0];
					tokens.set(i, temp1);
				}
            }
            if (matcher1.matches()||matcher2.matches()||matcher3.matches()||matcher4.matches()) {
                tokens.remove(i);
                signal = 1;
            }
            if(signal == 0){
            	i++;
            }
            
        }
        return (String[]) tokens.toArray( 
            new String[tokens.size()]);
	}
/*	 public static void main(String[] args){
		 Matcher matcher1 = pattern4.matcher("sweetieaglae@gmail.com");
		 Matcher matcher2 = pattern1.matcher("http://t.co/yaABu2reuI1");	
		 System.out.println(matcher1.matches());
		// System.out.println(matcher2.matches());
	 }*/
}
