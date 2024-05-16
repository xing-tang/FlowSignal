package cn.open.core.flow.signal

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-14:18
 * @Description: 事件信号去重或超时的实现类
 * @property sourceStream 事件信号的源流
 * @property timeoutMillis 超时时间
 * @constructor
 */
class FlowSignalWithDistinctOrTimeoutImpl(
  private val sourceStream: FlowSignalImpl<SignalEvent>,
  private val timeoutMillis: Long
): FlowSignalCollect<SignalEvent> {

  /**
   * 检查两个事件信号是否相等
   * @param old 旧的事件信号
   * @param new 新的事件信号
   * @return 如果相等则返回 true，否则返回 false
   */
  private fun areEquivalent(old: SignalEvent?, new: SignalEvent?): Boolean {
    return old == new
  }

  /**
   * 检查是否超时
   * @param oldTimeMillis 旧的时间戳
   * @param newTimeMillis 新的时间戳
   * @param timeoutMillis 超时时间
   * @return 如果超时则返回 true，否则返回 false
   */
  private fun hasTimedOut(oldTimeMillis: Long, newTimeMillis: Long, timeoutMillis: Long): Boolean {
    return oldTimeMillis + timeoutMillis < newTimeMillis
  }

  /**
   * 获取当前时间戳（毫秒）
   * @return 当前时间戳
   */
  private fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  /**
   * 收集事件信号
   * @param action 处理事件信号的函数
   */
  override suspend fun collect(action: suspend (value: SignalEvent) -> Unit) {
    var previousSignal: SignalEvent? = null
    var previousTimeMillis: Long = 0L
    sourceStream.collect {newSignal ->
      val currentTimeMillis = getCurrentTimeMillis()
      if (previousSignal == null ||
        ! areEquivalent(previousSignal, newSignal) ||
        hasTimedOut(previousTimeMillis, currentTimeMillis, timeoutMillis)
      ) {
        previousSignal = newSignal
        previousTimeMillis = currentTimeMillis
        action(newSignal)
      }
    }
  }

  /**
   * 收集粘性事件信号
   * @param action 处理事件信号的函数
   */
  override suspend fun collectSticky(action: suspend (value: SignalEvent) -> Unit) {
    var previousSignal: SignalEvent? = null
    var previousTimeMillis: Long = 0L
    sourceStream.collectSticky {newSignal ->
      val currentTimeMillis = getCurrentTimeMillis()
      if (previousSignal == null ||
        ! areEquivalent(previousSignal, newSignal) ||
        hasTimedOut(previousTimeMillis, currentTimeMillis, timeoutMillis)
      ) {
        previousSignal = newSignal
        previousTimeMillis = currentTimeMillis
        action(newSignal)
      }
    }
  }

  /**
   * 获取最后缓存的事件信号
   * @return 最后缓存的事件信号，可能为 null
   */
  override fun lastCache(): SignalEvent? {
    return this.sourceStream.lastCache()
  }

}