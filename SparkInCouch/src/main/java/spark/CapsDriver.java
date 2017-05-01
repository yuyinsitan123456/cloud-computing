package spark;

import java.util.Map;
import scala.Tuple2;

public class CapsDriver {

    public static void main(String[] args) {
        Map<Tuple2<Long, Integer>, Integer> result;
        result = new CapsTest().doSomething();
        System.out.println(result.keySet().size());
    }
}
