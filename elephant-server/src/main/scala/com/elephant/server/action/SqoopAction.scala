package com.elephant.server.action

import java.util.Properties

import javax.jdo.annotations.Columns
import org.apache.oozie.client.{OozieClient, WorkflowJob}
import org.springframework.stereotype.Component

@Component("SqoopAction")
class SqoopAction  extends BaseAction {


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
  //



  /**
   * 从Hive到mysql
   */
  def transDataIntoHiveFromMysql()={
    //表信息
    conf.setProperty("tableName","test_table_id")
    conf.setProperty("updateKeys","dt_d,user_id")
    conf.setProperty("updateMode","allowinsert")
    conf.setProperty("partitionKeys","dt_d")
    conf.setProperty("partitionValues","2020-04-14")
    conf.setProperty("extra","--verbose")
    conf.setProperty("map","4")
    //workflow.xml的地址
    conf.setProperty("oozie.wf.application.path","hdfs://cdhtest/user/jwth/etl_ds/reflux/partition/")
    //存入hive的哪张表
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

  /**
   * mysql到hive
   */
  def MysqlToHive()={
    //表信息
    conf.setProperty("mysqlUrl","jdbc:mysql://sr-dev-mysql-master-1.gz.cvte.cn:3306/dw_data_steam_beta?characterEncoding=UTF-8&useSSL=false")
    conf.setProperty("mysqlDb","dw_data_steam_beta")
    conf.setProperty("mysqlTable","table_id")
    conf.setProperty("hiveDb","seewo_cdm_dev")
    conf.setProperty("hiveTable","table_id_mm")
    conf.setProperty("extra","--verbose")
    conf.setProperty("map","4")
    //workflow.xml的地址
    conf.setProperty("oozie.wf.application.path","hdfs://cdhtest/user/mengxp/mysqlToHive/")
    //存入hive的哪张表
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

  override def run(args: String*): Boolean = {

    println("its run into SqoopAction... start")

    MysqlToHive()

    println("its run into SqoopAction... end")

    true

  }
}
