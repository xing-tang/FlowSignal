package cn.open.core.flow.signal

import cn.open.core.flow.signal.util.FlowSignalTestUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * @Author: xing.tang
 * @Date: 2024/5/15-17:27
 * @Description: FlowSignalImpl Test
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlowSignalImplTest {

  private lateinit var flowSignalImpl: FlowSignalImpl<SignalEvent>
  private val testDispatcher = TestCoroutineDispatcher()
  private val testScope = TestCoroutineScope(testDispatcher)

  @Before
  fun setUp() {
    flowSignalImpl = FlowSignalImpl()
  }

  @Test
  fun testEmitAndCollect() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    var receivedEvent: SignalEvent? = null

    val job = testScope.launch {
      flowSignalImpl.collect {receivedEvent = it}
    }

    flowSignalImpl.emit(event)
    testDispatcher.advanceUntilIdle()

    assertEquals(event, receivedEvent)
    job.cancel()
  }

  @Test
  fun testCollectSticky() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    flowSignalImpl.emit(event)
    testDispatcher.advanceUntilIdle() // Ensure the event is emitted
    FlowSignalTestUtils.logPrintln("testCollectSticky: emit(code):${event.hashCode()}")

    var receivedEvent: SignalEvent? = null
    val job = testScope.launch {
      flowSignalImpl.collectSticky {
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
    flowSignalImpl.emit(event)
    assertEquals(event, flowSignalImpl.lastCache())
  }

}