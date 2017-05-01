package spark;

import java.util.Arrays;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class Sum {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("App").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> distData = sc.parallelize(data);

        JavaRDD<Integer> lineLengths = distData.map((Integer s) -> s);
        int totalLength = lineLengths.reduce((Integer a, Integer b) -> {
            MyFileWriter w = new MyFileWriter("hello.txt");
            w.writeFile(a + b + "haha!");
            w.closeWrite();
            return a * b;
        });
        System.out.println(totalLength);
        sc.stop();
    }
}
