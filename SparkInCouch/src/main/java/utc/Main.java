package utc;

import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        java.util.Date dt1 = new java.util.Date("Thu Apr 20 19:21:42 +0000 2017");
        java.util.Date dt2 = new java.util.Date("Thu Apr 20 19:20:15 +0000 2017");
        System.out.println(dt1.before(dt2));
        
        boolean sameday = dt1.getYear() == dt2.getYear() &&
                  dt1.getMonth() == dt2.getMonth() && dt1.getDay() == dt2.getDay();
        
        System.out.println(sameday);
        
        System.out.println(dt1.toString());
        
        Date dt = new Date();
        String[] s = dt.toString().split(" ");
        String UTC = dt.toString();
        UTC = UTC.replace(s[4], "+0000");
        System.out.println(UTC);
        
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        System.out.println(dt.toString());
    }
}
