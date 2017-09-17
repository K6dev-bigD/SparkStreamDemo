import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.Seconds
import org.apache.spark.SparkContext
import org.apache.spark.HashPartitioner
import org.apache.spark.sql.SQLContext

import scala.collection.mutable.ListBuffer

object DemoApp {

  val windowSize = Duration(10000L)
  val slidingInterval = Duration(2000L)
  val checkpointInterval = Duration(50000L)
  val checkpointDir = "checkpoint"

  import scala.collection.mutable.ListBuffer

  def updateStateForKey(values: Seq[Int], state: Option[Int]): Option[Int] = {
    val currentCount = values.sum
    val previousCount = state.getOrElse(0)
    Some(currentCount + previousCount)
  }

  // The custom update function that takes in an iterator of key, new values for the key, previous state for the key and returns a new iterator of key, value
  def updateFunc(iter: Iterator[(String, Seq[Int], Option[Int])]): Iterator[(String, Int)] = {
    val list = ListBuffer[(String, Int)]()
    while (iter.hasNext) {
      val record = iter.next
      val state = updateStateForKey(record._2, record._3)
      val value = state.getOrElse(0)
      if (value != 0) {
        list += ((record._1, value))
      }
    }
    list.toIterator
  }

  def main(arg: Array[String]) {

    val conf = new SparkConf().setMaster("local[3]").setAppName("myStreaming")

    val ssc = new StreamingContext(conf, Seconds(10))

    ssc.checkpoint(checkpointDir)

    val lines = ssc.socketTextStream("localhost", 9090)

    val words = lines.flatMap(_.split(" "))

    words.checkpoint(checkpointInterval)

    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

    val runningCountStream = wordCounts.updateStateByKey[Int](updateFunc _, new HashPartitioner(10), false)

    //  DATAFRAME WITH SPARK STREAMING

    runningCountStream.foreachRDD { rdd =>
      val sqlContext = SQLContext.getOrCreate(SparkContext.getOrCreate())
      sqlContext.createDataFrame(rdd).toDF("word", "count").registerTempTable("batch_word_count")
      sqlContext.sql("select * from batch_word_count ").show()
    }

    //runningCountStream.print()

    ssc.start()

    ssc.awaitTermination()

  }
}