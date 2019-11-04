package org.apache.spark.sql.execution.datasources.hbase.test

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.spark.sql.execution.datasources.hbase.{HBaseRelation, HBaseTableCatalog}
import org.apache.spark.sql.{DataFrame, SparkSession}

object HBaseSource {

  val catDemande =     s"""{
                          |"table":{"namespace":"dco_edma", "name":"Site"},
                          |"rowkey":"key",
                          |"columns":{
                          |"key":{"cf":"rowkey", "col":"key", "type":"string"},
                          |"nom":{"cf":"d", "col":"nom", "type":"string"},
                          |"prenom":{"cf":"d", "col":"prenom", "type":"string"},
                          |"age":{"cf":"d", "col":"age", "type":"string"}
                          |}
                          |}""".stripMargin

  def main(args: Array[String]) {


    val opt = Map(
      HBaseRelation.RESTRICTIVE -> HBaseRelation.Restrictive.none,
      HBaseRelation.MAX_VERSIONS -> "3"
    )

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("HBaseSourceExample")
      .getOrCreate()

    val sqlContext = spark.sqlContext

    sqlContext.udf.register("filterLink",filterLink)

    def withCatalog(cat: String): DataFrame = {
      sqlContext
        .read
        .options(Map(HBaseTableCatalog.tableCatalog->cat) ++ opt)
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .load()
    }


    val demande = withCatalog(catDemande)

    demande.show(10)


    spark.stop()
  }


  val filterLink = (links:Map[String,String],typLink:String) => {
    var data: Map[String, String] = Map[String,String]()
    for(link <- links) {
      if(typLink.r.pattern.matcher(link._1).matches() && link._2.contains("""true""")){
        data += (link._1.split("_")(1).toString -> link._2.toString)
      }
    }
    if(data.size > 0) data else null
  }
}
