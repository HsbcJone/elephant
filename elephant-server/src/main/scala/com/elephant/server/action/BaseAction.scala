package com.elephant.server.action

/**
 * 这里的Action级别 和 workflow中的sqoop节点是一致的
 */
trait BaseAction {
  def runTask(args:String*)
}
