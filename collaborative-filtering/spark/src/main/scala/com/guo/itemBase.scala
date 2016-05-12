import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating, MatrixFactorizationModel}
import java.util.Calendar

object itemBase {
    def main(args: Array[String]) {	

        // set up environment
        val conf = new SparkConf()
          .setAppName("itemBase")
          .set("spark.executor.memory", "21000m")
        val sc = new SparkContext(conf)

        print("start")
        print(Calendar.getInstance().getTime())

        val file =sc.textFile(args(0))
        val rating=file.map{
        	line=>val fields=line.split("::")
        	(fields(0)toInt, (fields(1).toInt,fields(2).toFloat))
        }

        val movie =file.map{
         line=>val fields=line.split("::")
          (fields(1)toInt, (fields(0).toInt,fields(2).toFloat))
        } 

        //get user preference matrix
        val user=rating.groupByKey()
        // get coocurrence matrix
        val coo=user.flatMap{
          u => val l = u._2
          l.flatMap{
            l2 => l.map(l3=>((l2._1,l3._1),1))
          }
        }.reduceByKey(_+_)
        val coocurrence=coo.map(x=>(x._1._1,(x._1._2,x._2))).groupByKey()
        
        //get matrix multiply
        val coorAndPref = movie.join(coocurrence)
        val score = coorAndPref.flatMap{
          case(movie, (pref,coo)) => {
            coo.map(x=> ((pref._1,movie),x._2*pref._2))
          }
        }
        val allScore=score.reduceByKey(_+_).map(x=>(x._1._1,(x._1._2,x._2))).groupByKey()
        //get top 2 recommendation
        allScore.map(x=>(x._1,x._2.toArray.sortWith(_._2>_._2).slice(0,2).mkString(" "))).saveAsTextFile("hdfs:///output")
    
        print("end")
        print(Calendar.getInstance().getTime())
    }
}