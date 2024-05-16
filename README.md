[toc]
# FlowSignal 事件流通信
## 1 背景
- 在现代 Android 应用中，跨页面和跨组件通信是常见需求。为了高效管理和分发复杂事件流，我们需要一个基于事件驱动架构的事件流通信库。FlowSignal 是一个基于 Kotlin 的库，利用 Kotlin 协程和 Flow 特性以及 Lifecycle 生命周期感知能力，提供了一个简洁而强大的事件管理和分发解决方案。
- [FlowSignal Github 仓库地址]()

## 2 常用的事件流通信库对比
| 事件流通信库 | 延迟发送 | 有序接收 | 粘性事件 | 生命周期 | 线程分发|
| --- | --- | --- | --- | --- | --- |
| EventBus | N | Y | Y | N |Y|
| RxBus | N | Y | Y | N |Y|
| LiveData | Y | Y | Y | Y |Y|
| Flow | Y | Y | Y | Y |Y|
- 通过对比可以看出，LiveData 和 Flow 更符合需求。那么为什么选择了 Flow 而非 LiveData 呢？
- ***Kotlin 协程的兼容性***：Flow 是 Kotlin 协程的一部分，与现代异步编程紧密集成，使其成为 Kotlin 项目的自然选择，开发者可以利用对协程的现有知识。
- ***函数式编程范式***：与 LiveData 相比，Flow 更贴近函数式编程范式，与 Kotlin 的语言特性和设计原则更加契合，从而实现更清晰、更简洁的代码，易于理解和维护。
- ***灵活性与组合性***：Flow 提供了强大的操作符，用于转换和组合数据流，使开发者能够以简洁和可组合的方式表达复杂的事件处理逻辑。
- ***支持背压***：Flow 支持背压，允许开发者控制数据的发射和消费速率，在需要同步事件生产和消费以防止过载或资源浪费的情况下尤为关键。
- 因此，基于 Kotlin 并利用 Kotlin 协程和 Flow 特性以及 Lifecycle 生命周期感知能力的 FlowSignal 成为最佳选择。

## 3 开始使用
### 3.1 引用
- 在 Project 的 settings.gradle.kts 中加入：
```gradle
allprojects {
  repositories {
    ...
    maven {url = uri("https://jitpack.io")}
  }
}
```
- 在 Module 的 build.gradle.kts 中加入：
```gralde
dependencies {
  implementation("cn.open.android.core:flow-signal:1.0.0")
}
```

### 3.2 Event 事件定义
- 定义一个事件类，继承自 SignalEvent 接口：
```kotlin
// 方式一
data class MessageEvent(val message: String) : SignalEvent

// 方式二
sealed interface MediaServiceEvent: SignalEvent {
  data class LiveAudio(val sourceId: Long, val seq: Int, data: LiveAudioData): MediaServiceEvent
  data class LiveVideo(val sourceId: Long, val seq: Int, data: LiveVideoData): MediaServiceEvent
}
```

### 3.3 事件流发送
```kotlin
val event = MessageEvent("Hello, FlowSignal!")

// Activity | Fragment 中使用
lifecycleScope.launch { emitEvent(event) }

// ViewModel 中使用
viewModelScope.launch { emitEvent(event) }

// 其它场景中使用，自己管理生命周期，防止内存泄漏
val job: Job = flowSignalScopeLaunch {
  emitEvent(event)
}
job.cancel()

// 其它场景中使用，跟随应用 Application 级别生命周期，故无需取消
applicationScopeLaunch { emitEvent(event) }
```

### 3.4 事件流收集
```kotlin
// Activity | Fragment 中使用
lifecycleScope.launch { // 收集普通事件
  collectEvent<MessageEvent> {
    // 处理事件
  }
}
lifecycleScope.launch { // 收集粘性事件
  collectStickyEvent<SignalEvent> {
    when(it) { // 处理事件
      is MessageEvent -> {}
      else -> {}
    }
  }
}
lifecycleScope.launch { // 收集去重或超时事件
  collectDistinctEvent<MessageEvent>(500L) {
    // 处理事件
  }
}

// 基于 Activity | Fragment 中的扩展函数使用
collectEvent(this::onMessageEvent, Lifecycle.State.CREATED) // 收集普通事件
collectStickyEvent(this::onMessageEvent, Lifecycle.State.STARTED) // 收集粘性事件
collectDistinctEvent(this::onMessageEvent, Lifecycle.State.RESUMED) // 收集去重或超时事件
private fun onMessageEvent(event: MessageEvent) { // 处理事件
  
}

// 其它场景中使用，自己管理生命周期，防止内存泄漏
val job: Job = flowSignalScopeLaunch {
  collectEvent<MessageEvent> { // 收集普通事件
    // 处理事件
  }
}
job.cancel()

// 其它场景中使用，跟随应用 Application 级别生命周期，故无需取消
applicationScopeLaunch { 
  collectEvent<MessageEvent> { // 收集普通事件
    // 处理事件
  }
}
```

## 4 总结
- FlowSignal 提供了一种高效、简洁的事件处理方案，利用 Kotlin 的协程和 Flow 特性，使得事件的发送和收集更加直观和高效。通过引入 FlowSignal，你可以更轻松地管理 Android 应用中的复杂事件流，提升应用的性能和可维护性。
- 希望本使用文档能够帮助你快速上手 FlowSignal 并应用到实际项目中。如果你有任何问题或建议，欢迎随时反馈。