package com.elephant.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WebSpringStart {
}

object WebSpringStart extends App{
  println("xxxxx")
  SpringApplication.run(classOf[WebSpringStart])
  println("xxxxx-bb")

}

/*object WebSpringStartXXX {
  def main(args: Array[String]): Unit = {
    println("web")
    val x=new WebSpringStart()
    x.main(null)
    //SpringApplication.run(classOf[WebSpringStart])
  }
}*/
