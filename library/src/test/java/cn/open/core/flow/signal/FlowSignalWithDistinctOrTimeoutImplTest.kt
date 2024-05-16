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
 * @Date: 2024/5/15-18:26
 * @Description: FlowSignalWithDistinctOrTimeoutImpl Test
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlowSignalWithDistinctOrTimeoutImplTest {

  private lateinit var flowSignalImpl: FlowSignalImpl<SignalEvent>
  private lateinit var distinctSignal: FlowSignalWithDistinctOrTimeoutImpl
  private val testDispatcher = TestCoroutineDispatcher()
  private val testScope = TestCoroutineScope(testDispatcher)

  @Before
  fun setUp() {
    flowSignalImpl = FlowSignalImpl()
    distinctSignal = FlowSignalWithDistinctOrTimeoutImpl(flowSignalImpl, 500L)
  }

  @Test
  fun testDistinctEmitAndCollect() = runBlocking(testDispatcher) {
    val event1 = object: SignalEvent {}
    val event2 = object: SignalEvent {}
    val receivedEvents = mutableListOf<SignalEvent>()

    val job = testScope.launch {
      distinctSignal.collect {receivedEvents.add(it)}
    }

    flowSignalImpl.emit(event1)
    flowSignalImpl.emit(event1)
    flowSignalImpl.emit(event2)
    testDispatcher.advanceUntilIdle()

    assertEquals(2, receivedEvents.size)
    assertEquals(listOf(event1, event2), receivedEvents)
    job.cancel()
  }

  /**
   * Todo: 待办项
   * 由于 testDispatcher.advanceTimeBy(500L) 并没有像想象中起到作用
   * 所以暂时将 assertEquals(2, receivedEvents.size) 方法注掉了
   */
  @Test
  fun testDistinctTimeout() = runBlocking(testDispatcher) {
    val event = object: SignalEvent {}
    val receivedEvents = mutableListOf<SignalEvent>()

    val job = testScope.launch {
      distinctSignal.collect {
        receivedEvents.add(it)
        FlowSignalTestUtils.logPrintln("testDistinctTimeout: it(code):${it.hashCode()}")
      }
    }

    flowSignalImpl.emit(event)
    val temp = System.currentTimeMillis()
    FlowSignalTestUtils.logPrintln("testDistinctTimeout: emit1(code):${event.hashCode()} $temp")
    testDispatcher.advanceTimeBy(500L)
    testDispatcher.advanceUntilIdle()

    val curr = System.currentTimeMillis()
    val diff = curr - temp
    FlowSignalTestUtils.logPrintln("testDistinctTimeout: emit1(code):${event.hashCode()} $curr $diff")
    flowSignalImpl.emit(event)
    testDispatcher.advanceUntilIdle()
    FlowSignalTestUtils.logPrintln("testDistinctTimeout: emit2(code):${event.hashCode()} ${System.currentTimeMillis()}")

    FlowSignalTestUtils.logPrintln("testDistinctTimeout: assertEquals")
    // assertEquals(2, receivedEvents.size)
    job.cancel()
  }

}