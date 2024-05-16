package cn.open.core.flow.signal

import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resume

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-11:56
 * @Description: 事件信号观察者类，支持添加、移除观察者，并通知观察者事件信号
 * @param T 事件信号的类型
 */
class FlowSignalObservable<T> {

  // 使用线程安全的 CopyOnWriteArrayList 来存储观察者
  private val observers = CopyOnWriteArrayList<(value: T) -> Unit>()

  /**
   * 添加一个观察者
   * @param action 观察者处理事件信号的函数
   */
  suspend fun observer(action: (value: T) -> Unit) {
    suspendCancellableCoroutine {continuation ->
      val wasAdded = synchronized(observers) {
        if (! observers.contains(action)) {
          observers.add(action)
          true
        } else {
          false
        }
      }
      if (wasAdded) {
        continuation.invokeOnCancellation {observers.remove(action)}
      }
      continuation.resume(Unit) // 恢复协程
    }
  }

  /**
   * 移除一个观察者
   * @param action 要移除的观察者处理事件信号的函数
   */
  fun remove(action: (value: T) -> Unit) {
    observers.remove(action)
  }

  /**
   * 清空所有观察者
   */
  fun clear() {
    observers.clear()
  }

  /**
   * 通知所有观察者一个事件信号
   * @param value 要通知的事件信号
   */
  fun notify(value: T) {
    observers.forEach {observer ->
      observer(value)
    }
  }

}