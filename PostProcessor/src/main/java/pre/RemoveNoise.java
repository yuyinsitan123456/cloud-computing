package pre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveNoise {
    private String regexPattern1 = ".*(https?|ftp)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]*.*\"*\n*.*";
    private Pattern pattern1 = Pattern.compile(regexPattern1);
    private String regexPattern2 = ".*#.*";
    private Pattern pattern2 = Pattern.compile(regexPattern2);
    private String regexPattern3 = ".*@.*";
    private Pattern pattern3 = Pattern.compile(regexPattern3);
    private String regexPattern4 = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+";
    private Pattern pattern4 = Pattern.compile(regexPattern4);

    public String removeNoise(String text) {
        List<String> words = new ArrayList<String>(Arrays.asList(text.split("\\s+")));
        for (int i = 0; i < words.size(); ) {
            String temp;
            int signal = 0;
            Matcher matcher1 = pattern1.matcher(words.get(i));
            Matcher matcher2 = pattern2.matcher(words.get(i));
            Matcher matcher3 = pattern3.matcher(words.get(i));
            Matcher matcher4 = pattern4.matcher(words.get(i));
            if (words.get(i).contains("#")) {
                if (words.get(i).charAt(0) != '#') {
                    temp = words.get(i).split("#")[0].trim();
                    words.set(i, temp);
                    Matcher matcher44 = pattern4.matcher(temp);
                    if (temp.contains("@") && matcher44.matches()) {
                        words.remove(i);
                        signal = 1;
                    }
                }
            }
            if (words.get(i).contains("@") && !matcher4.matches()) {
                if (words.get(i).charAt(0) != '@') {
                    String temp1 = words.get(i).split("@")[0];
                    words.set(i, temp1);
                }
            }
            if (matcher1.matches() || matcher2.matches() || matcher3.matches() || matcher4.matches()) {
                words.remove(i);
                signal = 1;
            }
            if (signal == 0) {
                i++;
            }
        }

        StringBuffer sb = new StringBuffer();
        for (String word : words) {
            sb.append(word);
            sb.append(" ");
        }
        return sb.toString();
    }
}
