package com.speakeng.app.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookStorageModule {

    @Provides
    @Singleton
    fun provideUploadedBooksDir(@ApplicationContext context: Context): File =
        File(context.filesDir, "books").apply { mkdirs() }
}
