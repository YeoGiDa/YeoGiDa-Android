package com.starters.yeogida.presentation.common

import androidx.lifecycle.Observer

// 데이터가 한 번 소비되면 더 이상 사용되지 않게 하자.
class Event<T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            // 데이터를 소비 (반환)
            content
        }
    }
}

// onEventUnhandledContent 람다 함수 = 아직 핸들링되지 않은 데이터를 다루는 람다 함수.
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {

        // 데이터의 값이 바뀌었을 때, null이 아니면 람다 함수에 아직 처리되지 않은 데이터 content 전달.
        event?.getContentIfNotHandled()?.let { content ->
            onEventUnhandledContent(content)
        }
    }
}