package spark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

public class CapsTest {

    public Map<Tuple2<Long, Integer>, Integer> doSomething() {
        SparkConf sparkConf = new SparkConf().setAppName("spark cloudant connecter").setMaster("local[*]");
        sparkConf.set("spark.streaming.concurrentJobs", "30");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        SQLContext sqlContext = new SQLContext(sc);
        System.out.print("initialization successfully");

        String schemaString = "_id text";
        List<StructField> fields = new ArrayList<>();
        for (String fieldName : schemaString.split(" ")) {
            StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
            fields.add(field);
        }
        StructType schema = DataTypes.createStructType(fields);

        Dataset<Row> st = sqlContext.read().format("com.cloudant.spark")
                //.option("view", "_design/test/_view/createdAt")
                .option("cloudant.protocol", "http")
                .option("cloudant.host", "127.0.0.1:5984")
                .schema(schema)
                .option("cloudant.username", "couchdb")
                .option("cloudant.password", "123456")
                .load("melbourne");

        // System.out.println(st.filter(st.col("id").gt("858583564652130306")).count());
        // st.printSchema();
        JavaRDD<Row> rdd = st.toJavaRDD();
        JavaPairRDD<Tuple2<Long, Integer>, Integer> result;
        result = rdd.mapToPair((Row r) -> {
            return new Tuple2<>(new Tuple2<>(Long.valueOf(r.getAs("_id")) % 2, 1), 1);
        }).reduceByKey((x, y) -> x + y);
        result = result.coalesce(1);
        return result.collectAsMap();
    }
}
