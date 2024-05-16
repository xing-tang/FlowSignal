package cn.open.core.flow.signal

import cn.open.core.flow.signal.util.FlowSignalTestUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-18:59
 * @Description: FlowSignalPool Test
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlowSignalPoolTest {

  private val testDispatcher = TestCoroutineDispatcher()
  private val testScope = TestCoroutineScope(testDispatcher)

  @Test
  fun testEmitAndCollect() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    var receivedEvent: SignalEvent? = null

    val job = testScope.launch {
      FlowSignalPool.collect {receivedEvent = it}
    }

    FlowSignalPool.emit(event)
    testDispatcher.advanceUntilIdle()

    assertEquals(event, receivedEvent)
    job.cancel()
  }

  @Test
  fun testCollectSticky() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    FlowSignalPool.emit(event)
    testDispatcher.advanceUntilIdle() // Ensure the event is emitted
    FlowSignalTestUtils.logPrintln("testCollectSticky: emit(code):${event.hashCode()}")

    var receivedEvent: SignalEvent? = null
    val job = testScope.launch {
      FlowSignalPool.collectSticky {
        receivedEvent = it
        FlowSignalTestUtils.logPrintln("testCollectSticky: collectSticky(code):${it.hashCode()}")
      }
    }
    testDispatcher.advanceUntilIdle() // Ensure the event is collected
    FlowSignalTestUtils.logPrintln("testCollectSticky: assertEquals")

    assertEquals(event, receivedEvent)
    job.cancel()
  }


  @Test
  fun testLastCache() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    FlowSignalPool.emit(event)
    assertEquals(event, FlowSignalPool.lastCache())
  }

  @Test
  fun testDistinctUntilChangedOrTimeout() = runBlocking(testDispatcher) {
    val event1 = object: SignalEvent {}
    val event2 = object: SignalEvent {}
    val receivedEvents = mutableListOf<SignalEvent>()

    val distinctSignal = FlowSignalPool.distinctUntilChangedOrTimeout(500L)

    val job = testScope.launch {
      distinctSignal.collect {receivedEvents.add(it)}
    }

    FlowSignalPool.emit(event1)
    FlowSignalPool.emit(event1)
    FlowSignalPool.emit(event2)
    testDispatcher.advanceUntilIdle()

    assertEquals(2, receivedEvents.size)
    assertEquals(listOf(event1, event2), receivedEvents)
    job.cancel()
  }

}