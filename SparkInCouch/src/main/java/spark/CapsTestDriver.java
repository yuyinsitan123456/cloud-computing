package spark;

import java.util.Iterator;
import java.util.Map;
import scala.Tuple2;

public class CapsTestDriver {

    public static void main(String[] args) {
        Map<Tuple2<Integer, Boolean>, Integer> result;
        result = new CapsTest().doSomething();
        for (Tuple2<Integer, Boolean> key : result.keySet()) {
            System.out.println(key.productElement(1));
            System.out.println(key + " : " + result.get(key));
        }
    }
}
