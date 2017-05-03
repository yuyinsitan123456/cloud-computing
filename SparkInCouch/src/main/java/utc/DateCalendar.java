package utc;

import java.util.Calendar;
import java.util.Date;

public class DateCalendar {
    public static void main(String[] args) {
        /* CONVERT STRING TO DATE */
        java.util.Date dt1 = new java.util.Date("Thu Apr 20 19:21:42 +0000 2017");
        java.util.Date dt2 = new java.util.Date("Thu Apr 20 19:20:15 +0000 2017");
        
        /* BEFORE? AFTER? */
        System.out.println(dt1.before(dt2));
        
        /* SAME DAY */
        boolean sameday = dt1.getYear() == dt2.getYear() &&
                  dt1.getMonth() == dt2.getMonth() && dt1.getDay() == dt2.getDay();
        System.out.println(sameday);
        
        /* CONVERT DATE TO STRING */
        System.out.println(dt1.toString());
        
        /* CONVERT JAVA STYLE TO TWITTER STYLE */
        Date dt = new Date();
        String[] s = dt.toString().split(" ");
        String UTC = dt.toString();
        UTC = UTC.replace(s[4], "+0000");
        System.out.println(UTC);
        
        /* ADD ONE DAY BY CALENDAR */
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        System.out.println(dt.toString());

        /* Which day of the week is it? */
        Date date = new Date("Sun Apr 30 17:04:11 +0000 2017");
        c.set(1900 + date.getYear(), date.getMonth(), date.getDay() - 1);
        System.out.println(c.getTime());
        System.out.println(c.get(Calendar.DAY_OF_WEEK));
    }
}
