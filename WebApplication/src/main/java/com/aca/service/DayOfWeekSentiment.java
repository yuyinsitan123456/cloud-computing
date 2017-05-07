package com.aca.service;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class DayOfWeekSentiment {
    /*
    @Autowired
    private JavaSparkContext javaSparkContext;

    public Map<Tuple2<Integer, Boolean>, Integer> doMelbourne() {
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        System.out.print("initialization successfully");

        Dataset<Row> st = sqlContext.read().format("com.cloudant.spark")
                .option("view", "_design/test/_view/idCase1")
                .option("cloudant.protocol", "http")
                .option("cloudant.host", "127.0.0.1:5984")
                .load("melbourne");

        JavaRDD<Row> rdd = st.toJavaRDD();
        JavaPairRDD<Tuple2<Integer, Boolean>, Integer> result;
        result = rdd.mapToPair((Row r) -> {
            GenericRowWithSchema valueJson = r.getAs("value");
            boolean sentiment = (boolean) valueJson.getAs("sentiment");
            String time = valueJson.getAs("time");
            int dayOfWeek = whatDayIsItToday(time);
            return new Tuple2<>(new Tuple2<>(dayOfWeek, sentiment), 1);
        }).reduceByKey((x, y) -> x + y);
        result = result.coalesce(1);
        return result.collectAsMap();
    }

    public static int whatDayIsItToday(String time) {
        Date date = new Date(time);
        Calendar c = Calendar.getInstance();
        c.set(1900 + date.getYear(), date.getMonth(), date.getDay() - 1);
        return c.get(Calendar.DAY_OF_WEEK);
    }
    */
}
