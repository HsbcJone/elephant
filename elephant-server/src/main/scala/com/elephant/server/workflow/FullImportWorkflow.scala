package com.elephant.server.workflow

import com.elephant.server.common.JobExecutionEngine
import com.elephant.server.pojo.{ActionInfo, WorkflowInfo}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.mutable.ListBuffer

@Component("FullImportWorkflow")
class FullImportWorkflow  extends BaseWorkflow {

  @Autowired
  var jobExecutionEngine:JobExecutionEngine=_

  override def run(args: String*): Boolean= {
     //DB查询 web模块的配置信息
    val workflowInfos=ListBuffer[WorkflowInfo]()
    val wc=new WorkflowInfo
    val actionInfos=List(
      ActionInfo("sqoop1","t_project","t_project_mxp"),
      ActionInfo("sqoop2","t_user","t_user_mxp")
    )
    wc.actionInfos=actionInfos
    workflowInfos.+=(wc)
    jobExecutionEngine.run(workflowInfos.toList)
    true
  }
}
