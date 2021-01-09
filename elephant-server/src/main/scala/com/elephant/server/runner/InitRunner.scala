package com.elephant.server.runner

import com.elephant.server.SpringBootScalaIntegration
import com.elephant.server.action.{Base, BaseAction}
import com.elephant.server.workflow.BaseWorkflow
import org.springframework.boot.CommandLineRunner
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component

/**
 * CommandLineRunner(run方法)+ApplicationContextAware(提供AppContext）
 * 通过oozie的springboot中不同的Action的实现并且进行回收
 */
@Component
class InitRunner extends CommandLineRunner with  ApplicationContextAware  {
  var context:AbstractApplicationContext=null
  override def run(argss: String*): Unit = {
    SpringBootScalaIntegration.args.foreach(println(_))
    var componetName=SpringBootScalaIntegration.args(0)
    var componetType=SpringBootScalaIntegration.args(1)
    var flag=true
    try {
      var base:Base=null
      if("action".equalsIgnoreCase(componetType)){
       base= context.getBean(componetName).asInstanceOf[BaseAction]
      }else{
        base= context.getBean(componetName).asInstanceOf[BaseWorkflow]
      }
       flag=base.run(SpringBootScalaIntegration.args: _*)
    } catch {
          //TODO 自定义Exception
      case e:Exception => throw new RuntimeException(e.getMessage)
    } finally {
        //context.close()
        println("context need be stop....")
      context.stop()
    }
  }

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    context=applicationContext.asInstanceOf[AbstractApplicationContext]
  }
}