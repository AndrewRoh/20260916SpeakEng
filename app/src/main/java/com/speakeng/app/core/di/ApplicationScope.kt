package com.speakeng.app.core.di

import javax.inject.Qualifier

/**
 * A [kotlinx.coroutines.CoroutineScope] that outlives any single screen. Use it for fire-and-forget
 * writes (e.g. persisting a completion toggle) that must not be lost if the user navigates away or
 * the hosting ViewModel is cleared before the write finishes — `viewModelScope` gets cancelled at
 * that point, which can silently drop an in-flight Room write.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
