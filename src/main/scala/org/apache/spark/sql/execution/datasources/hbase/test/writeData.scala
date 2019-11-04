package org.apache.spark.sql.execution.datasources.hbase.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog

object writeData {

  case class Person(key:String,nom:String,prenom:String,age:Int)


  val catalog =     s"""{
                          |"table":{"namespace":"dco_edma", "name":"Site"},
                          |"rowkey":"key",
                          |"columns":{
                          |"key":{"cf":"rowkey", "col":"key", "type":"string"},
                          |"nom":{"cf":"d", "col":"nom", "type":"string"},
                          |"prenom":{"cf":"d", "col":"prenom", "type":"string"},
                          |"age":{"cf":"d", "col":"age", "type":"string"}
                          |}
                          |}""".stripMargin


  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("HBaseSourceExample")
      .getOrCreate()

    import spark.implicits._


    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext


    val data = List(
      Person("1","Dadi","Souhaib",29),
      Person("2","Dadi","Achraf",26),
      Person("3","Dadi","Walid",18),
      Person("4","Dadi","Zoubida",30)
    )



    sc.parallelize(data).toDF.write.options(
      Map(HBaseTableCatalog.tableCatalog -> catalog))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save()

  }

}
