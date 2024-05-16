package cn.open.core.flow.signal

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-19:00
 * @Description: FlowSignalObservable Test
 */
class FlowSignalObservableTest {

  private lateinit var flowSignalObservable: FlowSignalObservable<SignalEvent>
  private val testDispatcher = TestCoroutineDispatcher()
  private val testScope = TestCoroutineScope(testDispatcher)

  @Before
  fun setUp() {
    flowSignalObservable = FlowSignalObservable()
  }

  @Test
  fun testObserver() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    var receivedEvent: SignalEvent? = null

    testScope.launch {
      flowSignalObservable.observer {receivedEvent = it}
    }
    flowSignalObservable.notify(event)

    assertEquals(event, receivedEvent)
  }

  @Test
  fun testRemoveObserver() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    var receivedEvent: SignalEvent? = null

    val observer: (SignalEvent) -> Unit = {receivedEvent = it}
    testScope.launch {
      flowSignalObservable.observer(observer)
    }
    flowSignalObservable.remove(observer)
    flowSignalObservable.notify(event)

    assertNull(receivedEvent)
  }

  @Test
  fun testClearObservers() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    var receivedEvent: SignalEvent? = null

    testScope.launch {
      flowSignalObservable.observer {receivedEvent = it}
    }
    flowSignalObservable.clear()
    flowSignalObservable.notify(event)

    assertNull(receivedEvent)
  }

}