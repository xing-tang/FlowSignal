package cn.open.core.flow.signal

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-10:56
 * @Description: 表示一个可以发送事件信号的接口
 * @param T 事件信号的类型
 */
interface FlowSignalEmit<T> {

  /**
   * 发送事件信号
   * @param value 要发送的事件信号
   */
  suspend fun emit(value: T)

}