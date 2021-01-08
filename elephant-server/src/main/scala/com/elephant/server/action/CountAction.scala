package com.elephant.server.action

import org.springframework.stereotype.Component

@Component("CountAction")
class CountAction extends BaseAction {

  /**
   * 进行Sqoop完后进行数据统计
   * @param args
   */
  override def runTask(args: String*): Unit = {
    println("count done "+args.foreach(println(_)))
  }
}
