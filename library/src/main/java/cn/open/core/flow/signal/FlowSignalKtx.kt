package cn.open.core.flow.signal

import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-14:06
 * @Description: FlowSignal 的扩展类
 */

/**
 * 收集特定类型的事件信号
 * @param T 事件信号的类型
 * @param action 处理事件信号的函数
 */
suspend inline fun <reified T: SignalEvent> collectEvent(
  crossinline action: suspend (value: T) -> Unit
) {
  FlowSignalPool.collect {
    if (it is T) {
      action(it)
    }
  }
}

/**
 * 收集特定类型的事件信号
 * @receiver ComponentActivity
 * @param action Function1<T, Unit>
 * @param state State
 */
inline fun <reified T: SignalEvent> ComponentActivity.collectEvent(
  crossinline action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED
) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      FlowSignalPool.collect {
        if (it is T) {
          action(it)
        }
      }
    }
  }
}

/**
 * 收集特定类型的事件信号
 * @receiver Fragment
 * @param action Function1<T, Unit>
 * @param state State
 */
inline fun <reified T: SignalEvent> Fragment.collectEvent(
  crossinline action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED
) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      FlowSignalPool.collect {
        if (it is T) {
          action(it)
        }
      }
    }
  }
}

/**
 * 收集特定类型的粘性事件信号
 * @param T 事件信号的类型
 * @param action 处理事件信号的函数
 */
suspend inline fun <reified T: SignalEvent> collectStickyEvent(
  crossinline action: suspend (value: T) -> Unit
) {
  FlowSignalPool.collectSticky {
    if (it is T) {
      action(it)
    }
  }
}

/**
 * 收集特定类型的粘性事件信号
 * @receiver ComponentActivity
 * @param action Function1<T, Unit>
 * @param state State
 */
inline fun <reified T: SignalEvent> ComponentActivity.collectStickyEvent(
  crossinline action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED
) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      FlowSignalPool.collect {
        if (it is T) {
          action(it)
        }
      }
    }
  }
}

/**
 * 收集特定类型的粘性事件信号
 * @receiver Fragment
 * @param action Function1<T, Unit>
 * @param state State
 */
inline fun <reified T: SignalEvent> Fragment.collectStickyEvent(
  crossinline action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED
) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      FlowSignalPool.collectSticky {
        if (it is T) {
          action(it)
        }
      }
    }
  }
}

/**
 * 收集去重或超时的特定类型的事件信号
 * @param T 事件信号的类型
 * @param timeoutMillis 超时时间，默认为 500 毫秒
 * @param action 处理事件信号的函数
 */
suspend inline fun <reified T: SignalEvent> collectDistinctEvent(
  timeoutMillis: Long = 500L, crossinline action: suspend (value: T) -> Unit
) {
  FlowSignalPool.distinctUntilChangedOrTimeout(timeoutMillis).collect {
    if (it is T) {
      action(it)
    }
  }
}

/**
 * 收集去重或超时的特定类型的事件信号
 * @receiver ComponentActivity
 * @param timeoutMillis Long
 * @param action Function1<T, Unit>
 * @param state State
 */
inline fun <reified T: SignalEvent> ComponentActivity.collectDistinctEvent(
  timeoutMillis: Long = 500L, crossinline action: suspend (value: T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED
) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      FlowSignalPool.distinctUntilChangedOrTimeout(timeoutMillis).collect {
        if (it is T) {
          action(it)
        }
      }
    }
  }
}

/**
 * 收集去重或超时的特定类型的事件信号
 * @receiver Fragment
 * @param timeoutMillis Long
 * @param action Function1<T, Unit>
 * @param state State
 */
inline fun <reified T: SignalEvent> Fragment.collectDistinctEvent(
  timeoutMillis: Long = 500L, crossinline action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED
) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      FlowSignalPool.distinctUntilChangedOrTimeout(timeoutMillis).collect {
        if (it is T) {
          action(it)
        }
      }
    }
  }
}

/**
 * 发送一个事件信号
 * @param event 要发送的事件信号
 */
suspend inline fun emitEvent(event: SignalEvent) {
  FlowSignalPool.emit(event)
}

/**
 * 获取并返回事件信号的调度器
 * 注意：使用的时候需要注意协程的生命周期，避免内存泄漏
 * @return CoroutineDispatcher
 */
fun flowSignalDispatcher(): CoroutineDispatcher {
  return FlowSignalPool.flowSignalDispatcher()
}

/**
 * 获取并返回事件信号的作用域
 * 注意：使用的时候需要注意协程的生命周期，避免内存泄漏
 * @return CoroutineScope
 */
fun flowSignalScope(): CoroutineScope {
  return FlowSignalPool.flowSignalScope()
}

/**
 * 在事件信号的作用域中启动一个协程
 * 注意：使用的时候需要注意协程的生命周期，避免内存泄漏
 * @param context CoroutineContext
 * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
 * @return Job
 */
fun flowSignalScopeLaunch(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> Unit): Job {
  return flowSignalScope().launch(context) {block()}
}

/**
 * 在 application 作用域中启动一个协程
 * 注意：使用的时候需要注意协程的生命周期，避免内存泄漏
 * @param context CoroutineContext
 * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
 */
fun applicationScopeLaunch(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> Unit) {
  flowSignalScope().launch(context) {block()}
}