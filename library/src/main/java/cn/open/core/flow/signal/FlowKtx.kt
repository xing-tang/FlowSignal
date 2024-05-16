package cn.open.core.flow.signal

import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-16:19
 * @Description: Flow 的扩展类
 */

/**
 * 收集 Flow
 * @receiver ComponentActivity
 * @param flow Flow<T>
 * @param action Function1<T, Unit>
 * @param state State
 */
fun <T> ComponentActivity.collect(flow: Flow<T>, action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      flow.collect(action)
    }
  }
}

/**
 * 收集 Flow，并防抖动
 * @receiver ComponentActivity
 * @param flow Flow<T>
 * @param action Function1<T, Unit>
 * @param timeoutMillis Long
 * @param state State
 */
@OptIn(FlowPreview::class)
fun <T> ComponentActivity.collectDebounce(flow: Flow<T>, action: (T) -> Unit, timeoutMillis: Long, state: Lifecycle.State = Lifecycle.State.CREATED) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      flow.debounce(timeoutMillis).collect(action)
    }
  }
}

/**
 * 收集 Flow
 * @receiver Fragment
 * @param flow Flow<T>
 * @param action Function1<T, Unit>
 * @param state State
 */
fun <T> Fragment.collect(flow: Flow<T>, action: (T) -> Unit, state: Lifecycle.State = Lifecycle.State.CREATED) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      flow.collect(action)
    }
  }
}

/**
 * 收集 Flow，并防抖动
 * @receiver Fragment
 * @param flow Flow<T>
 * @param action Function1<T, Unit>
 * @param timeoutMillis Long
 * @param state State
 */
@OptIn(FlowPreview::class)
fun <T> Fragment.collectDebounce(flow: Flow<T>, action: (T) -> Unit, timeoutMillis: Long, state: Lifecycle.State = Lifecycle.State.CREATED) {
  lifecycleScope.launch {
    repeatOnLifecycle(state) {
      flow.debounce(timeoutMillis).collect(action)
    }
  }
}