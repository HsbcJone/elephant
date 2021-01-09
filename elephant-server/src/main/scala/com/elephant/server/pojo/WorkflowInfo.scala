package com.elephant.server.pojo

import scala.collection.mutable.ListBuffer

class WorkflowInfo {
  var actionInfos=List[ActionInfo]()
}

case class ActionInfo(
                     var actionName:String,
                     var sourceTableName:String,
                     var sinkHiveTableName:String
                     )