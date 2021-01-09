package com.oozie.pipineTest

import java.util.Properties

import org.apache.oozie.client.{OozieClient, WorkflowJob}
import org.junit.Test
import scala.collection.JavaConversions._

class PipineTestApp {
  val oozieUrl="http://cdh-10-21-17-94:11000/oozie/"
  val conf = new Properties
  val userName = System.getProperty("user.name")
  conf.setProperty("user.name", "hive")
  conf.setProperty("nameNode", "hdfs://cdhtest")
  conf.setProperty("jobTracker", "yarnRM")
  conf.setProperty("launcher_queue", "root.jwth.etl")
  conf.setProperty("mapreduce_queue","root.jwth.etl")
  conf.setProperty("mapreduce.job.user.name","jwth")
  conf.setProperty("oozie.use.system.libpath", "true")
  conf.setProperty("oozie.wf.rerun.failnodes", "true")
  conf.setProperty("security_enabled", false+"")
  //hive的database
  conf.setProperty("catalogDatabase","seewo_cdm_dev")
  conf.setProperty("catalogTable","test_1")
  conf.setProperty("hiveMetaStoreUri","thrift://cdh-10-21-17-95:9083")
  //hcatlog的hdfs地址
  conf.setProperty("catalogHome","/opt/cloudera/parcels/CDH/lib/hive-hcatalog")
  conf.setProperty("dryrun","false")
  conf.setProperty("security_enabled","false")
  //mysql的配置
  conf.setProperty("mysqlUrl","jdbc:mysql://sr-dev-mysql-master-1.gz.cvte.cn:3306/dw_data_steam_beta?characterEncoding=UTF-8&useSSL=false")
  conf.setProperty("n","seewo")
  conf.setProperty("p","seewo@cvte")


  /**
   * mysql到hive
   */
  @Test
  def MysqlToHive()={
    println("step 1 starting use sqoop import data into hive from mysql")
    //表信息
    conf.setProperty("mysqlUrl","jdbc:mysql://sr-dev-mysql-master-1.gz.cvte.cn:3306/dw_data_steam_beta?characterEncoding=UTF-8&useSSL=false")
    conf.setProperty("mysqlDb","dw_data_steam_beta")
    conf.setProperty("mysqlTable","table_id")
    conf.setProperty("hiveDb","seewo_cdm_dev")
    conf.setProperty("hiveTable","table_id_mm")
    conf.setProperty("extra","--verbose")
    conf.setProperty("map","1")
    //workflow.xml的地址
    conf.setProperty("oozie.wf.application.path","hdfs://cdhtest/user/mengxp/mysqlToHive/")
    //存入hive的哪张表
    val client=new OozieClient(oozieUrl)
    val oozieJobId=client.run(conf)
    println(conf)
    while (client.getJobInfo(oozieJobId).getStatus.equals(WorkflowJob.Status.RUNNING)){
      println("Workflow-sqoop-import job running ...")
      Thread.sleep(10 * 1000)
    }
    println("Workflow-sqoop-import job completed ...")
    println(client.getJobInfo(oozieJobId))

    println(s"step 1 end use sqoop import data into hive from mysql status =${client.getJobInfo(oozieJobId).getStatus}")

    if(client.getJobInfo(oozieJobId).getStatus.equals(WorkflowJob.Status.SUCCEEDED)){
      println("step 2 starting use oozie etl hive table")

      //workflow.xml的地址
      conf.setProperty("oozie.wf.application.path","hdfs://cdhtest/user/mengxp/oozieHive/")
      conf.setProperty("hiveServer2Url","jdbc:hive2://psd-hadoop038:10000/default")
      conf.setProperty("hiveDb","seewo_cmd_dev")
      conf.setProperty("hivePassword","123456")
      //这个参数是Java的lib包的路径
      //存入hive的哪张表
      val client=new OozieClient(oozieUrl)
      val oozieJobId=client.run(conf)
      println(conf)
      while (client.getJobInfo(oozieJobId).getStatus.equals(WorkflowJob.Status.RUNNING)){
        println("Workflow-hive-etl job running ...")
        Thread.sleep(10 * 1000)
      }
      println("Workflow-hive-etl job completed ...")
      println(client.getJobInfo(oozieJobId))
      println("step 2 end  use oozie etl hive table")
    }
  }



  /**
   * 1.docker-compose exec mysql mysql -uroot -p123456
   * 2.retry-max="2" retry-interval="1" 进行验证
   * 验证OozieSqoopRetry
   * 在xml中增加参数  【retry-max="2" retry-interval="1"】
   */
  @Test
  def validationOozieSqoopRetry()= {
    println("step 1 starting use sqoop import data into hive from mysql")
    //表信息
    conf.setProperty("mysqlUrl", "jdbc:mysql://172.20.211.54:3306")
    conf.setProperty("mysqlDb", "mydb")
    conf.setProperty("mysqlTable", "orders")
    conf.setProperty("n","root")
    conf.setProperty("p","123456")
    conf.setProperty("hiveDb", "seewo_cdm_dev")
    conf.setProperty("hiveTable", "orders")
    conf.setProperty("extra", "--verbose")
    conf.setProperty("map", "1")
    //workflow.xml的地址
    conf.setProperty("oozie.wf.application.path", "hdfs://cdhtest/user/mengxp/mysqlToHive/")
    //本地文件不支持
    //conf.setProperty("oozie.wf.application.path", "file:///Users/mengxp/Desktop/code/OozieProject/doc/test")
    //存入hive的哪张表
    val client = new OozieClient(oozieUrl)
    val oozieJobId = client.run(conf)
    val workflow=client.getJobInfo(oozieJobId)
    workflow.getActions.foreach(
      x=>{
        println(x.getName)
      }
    )
   println(client.getJobInfo(oozieJobId).getStatus)
    println(conf)
    while (client.getJobInfo(oozieJobId).getStatus.equals(WorkflowJob.Status.RUNNING)) {
      val action=workflow.getActions.get(0)
      //println(action)
      //println(action.getErrorMessage+"zzz"+action.getId)
      val workflowActionInfo=client.getWorkflowActionInfo(action.getId)
      println(workflowActionInfo)
      println(workflowActionInfo.getExternalId)
      if(workflowActionInfo.getExternalId!=null) {
        //println(client.getJobLog(workflowActionInfo.getExternalId))
        //println("Workflow-sqoop-import job running ...")
        Thread.sleep(10 * 1000)
      }
    }
    println("Workflow-sqoop-import job completed ...")
    println(client.getJobInfo(oozieJobId))
    println(s"step 1 end use sqoop import data into hive from mysql status =${client.getJobInfo(oozieJobId).getStatus}")
  }


  /**
   * runCoodinate-elephant-data
   */
  @Test
  def runHiveByCoodinate()={
    //coodinate的配置
    conf.setProperty("oozie.coord.application.path","hdfs:///user/mengxp/elephant-core")
    conf.setProperty("start_date","2021-01-09T11:55+0800")
    conf.setProperty("end_date","2021-01-25T11:30+0800")
    conf.setProperty("cron","49 * * * *")
    conf.setProperty("app_name","elephant-data")


    conf.setProperty("hivePassword","123456")
    //workflow的配置
    conf.setProperty("wf_application_path","hdfs:///user/mengxp/elephant-workflowxml")
    conf.setProperty("hiveServer2Url","jdbc:hive2://psd-hadoop038:10000/default")
    conf.setProperty("hiveDb","seewo_cmd_dev")
    conf.setProperty("hivePassword","123456")
    //这个参数是Java的lib包的路径
    conf.setProperty("oozie.libpath","hdfs://cdhtest/user/mengxp/elephant-lib2/")
    val client=new OozieClient(oozieUrl)
    val oozieJobId=client.run(conf)
    println(conf)
    while (client.getJobInfo(oozieJobId).getStatus.equals(WorkflowJob.Status.RUNNING)){
      println("Workflow job running ...")
      Thread.sleep(10 * 1000)
    }
    println("Workflow job completed ...")
    println(client.getJobInfo(oozieJobId))
  }
}
