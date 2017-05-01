package spark;

import java.util.Arrays;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;
import scala.Tuple3;

public class CloudantTest {

    public static void main(String[] args) {
        try {
            SparkConf sparkConf = new SparkConf().setAppName("spark cloudant connecter").setMaster("local");
            sparkConf.set("spark.streaming.concurrentJobs", "30");

            JavaSparkContext sc = new JavaSparkContext(sparkConf);

            SQLContext sqlContext = new SQLContext(sc);
            System.out.print("initialization successfully");

            Dataset<Row> st = sqlContext.read().format("com.cloudant.spark")
                    //.option("view", "_design/test/_view/createdAt")
                    .option("cloudant.protocol", "http")
                    .option("cloudant.host", "127.0.0.1:5984")
                    //.option("cloudant.username", "nek")
                    //.option("cloudant.password", "123456")
                    .load("melbourne");

            // System.out.println(st.filter(st.col("id").gt("858583564652130306")).count());
            // st.printSchema();
            
            JavaRDD<Row> rdd = st.toJavaRDD();
            JavaPairRDD<Tuple2<Long, Integer>, Integer> result;
            result = rdd.mapToPair((Row r) -> {
                return new Tuple2<>(new Tuple2<>(Long.valueOf(r.getAs("_id")) % 2, 1), 1);
            }).reduceByKey((x, y) -> x + y);
            result = result.coalesce(1);
            result.saveAsTextFile("file:///Users/nek/Desktop/output");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
