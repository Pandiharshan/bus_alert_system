package com.campusbussbuddy.di

import com.campusbussbuddy.data.remote.FirebaseService
import com.campusbussbuddy.data.repository.FirebaseAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseService(): FirebaseService = FirebaseService()
    
    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(
        firebaseService: FirebaseService
    ): FirebaseAuthRepository = FirebaseAuthRepository(firebaseService)
}