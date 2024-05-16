package cn.open.core.flow.signal.util

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-18:13
 * @Description: 事件信号测试工具类
 */
object FlowSignalTestUtils {

  private val tag: String = "FlowSignalTest"

  fun logPrintln(message: String) {
    // val className = Exception().stackTrace[1].className
    println("$tag $message")
  }

}