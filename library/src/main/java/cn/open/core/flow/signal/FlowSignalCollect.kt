package cn.open.core.flow.signal

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-10:54
 * @Description: 表示一个可以收集事件信号的接口
 * @param T 事件信号的类型
 */
interface FlowSignalCollect<T> {

  /**
   * 收集事件信号
   * @param action 处理事件信号的函数
   */
  suspend fun collect(action: suspend (value: T) -> Unit)

  /**
   * 收集粘性事件信号
   * @param action 处理事件信号的函数
   */
  suspend fun collectSticky(action: suspend (value: T) -> Unit)

  /**
   * 获取最后缓存的事件信号
   * @return 返回最后缓存的事件信号，如果没有则返回 null
   */
  fun lastCache(): T?

}