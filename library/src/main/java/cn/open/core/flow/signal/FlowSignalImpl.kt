package cn.open.core.flow.signal

import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-10:58
 * @Description: 事件信号的实现类，支持收集和发送事件信号，且绑定协程域生命周期
 * @param T 事件信号的类型
 */
class FlowSignalImpl<T>: FlowSignalCollect<T>, FlowSignalEmit<T> {

  // 使用 MutableSharedFlow 来处理事件信号的流
  private val sharedFlow = MutableSharedFlow<T>(extraBufferCapacity = Int.MAX_VALUE)

  // 用于存储最后一个缓存的事件信号
  @Volatile
  private var _lastCache: T? = null

  /**
   * 收集事件信号
   * @param action 处理事件信号的函数
   */
  override suspend fun collect(action: suspend (value: T) -> Unit) {
    sharedFlow.collect(action)
  }

  /**
   * 收集粘性事件信号
   * @param action 处理事件信号的函数
   */
  override suspend fun collectSticky(action: suspend (value: T) -> Unit) {
    _lastCache?.let {action(it)} // 如果存在缓存的事件，先处理它
    collect(action) // 继续收集新的事件
  }

  /**
   * 获取最后缓存的事件信号
   * @return 返回最后缓存的事件信号，如果没有则返回 null
   */
  override fun lastCache(): T? {
    return _lastCache
  }

  /**
   * 发送事件信号
   * @param value 要发送的事件信号
   */
  override suspend fun emit(value: T) {
    this._lastCache = value
    sharedFlow.emit(value)
  }

  /**
   * 获取 FlowSignalCollect 接口的实现
   * @return FlowSignalCollect<T>
   */
  fun asCollect(): FlowSignalCollect<T> = this

  /**
   * 获取 FlowSignalEmit 接口的实现
   * @return FlowSignalEmit<T>
   */
  fun asEmit(): FlowSignalEmit<T> = this

}