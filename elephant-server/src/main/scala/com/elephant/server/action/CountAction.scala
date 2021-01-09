package com.elephant.server.action

import org.springframework.stereotype.Component

@Component("CountAction")
class CountAction extends BaseAction {

  /**
   * 进行Sqoop完后进行数据统计
   * @param args
   */
  override def run(args: String*): Boolean = {
    println("个数 "+args.size)
    println("count done "+args.foreach(println(_)))
    true
  }
}
