package cn.open.core.flow.signal

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-14:01
 * @Description: 事件信号池，管理事件信号的收集和发送
 */
object FlowSignalPool: FlowSignalCollect<SignalEvent>, FlowSignalEmit<SignalEvent> {

  // 事件信号的实现类
  private val flowSignal = FlowSignalImpl<SignalEvent>()

  // 事件信号的执行器
  private val flowSignalExecutor = Executors.newSingleThreadExecutor()

  // 事件信号的调度器
  private val flowSignalDispatcher: CoroutineDispatcher = flowSignalExecutor.asCoroutineDispatcher()

  // 事件信号的作用域
  private val flowSignalScope = CoroutineScope(flowSignalDispatcher + SupervisorJob())

  /**
   * 收集事件信号
   * @param action 处理事件信号的函数
   */
  override suspend fun collect(action: suspend (value: SignalEvent) -> Unit) {
    flowSignal.collect(action)
  }

  /**
   * 收集粘性事件信号
   * @param action 处理事件信号的函数
   */
  override suspend fun collectSticky(action: suspend (value: SignalEvent) -> Unit) {
    flowSignal.collectSticky(action)
  }

  /**
   * 获取最后缓存的事件信号
   * @return 最后缓存的事件信号，可能为 null
   */
  override fun lastCache(): SignalEvent? {
    return flowSignal.lastCache()
  }

  /**
   * 发送一个事件信号
   * @param value 要发送的事件信号
   */
  override suspend fun emit(value: SignalEvent) {
    flowSignal.emit(value)
  }

  /**
   * 获取去重或超时的事件信号收集器
   * @param timeoutMillis 超时时间，默认为 500 毫秒
   * @return 去重或超时的事件信号收集器
   */
  fun distinctUntilChangedOrTimeout(timeoutMillis: Long = 500L): FlowSignalCollect<SignalEvent> {
    return FlowSignalWithDistinctOrTimeoutImpl(flowSignal, timeoutMillis)
  }

  /**
   * 获取并返回事件信号的调度器
   * @return CoroutineDispatcher
   */
  fun flowSignalDispatcher(): CoroutineDispatcher {
    return flowSignalDispatcher
  }

  /**
   * 获取并返回事件信号的作用域
   * @return CoroutineScope
   */
  fun flowSignalScope(): CoroutineScope {
    return flowSignalScope
  }

}