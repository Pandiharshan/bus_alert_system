package com.campusbussbuddy.di

import com.campusbussbuddy.data.repository.AuthRepositoryImpl
import com.campusbussbuddy.data.repository.BusTrackingRepositoryImpl
import com.campusbussbuddy.data.repository.QrCodeRepositoryImpl
import com.campusbussbuddy.domain.usecase.AuthRepository
import com.campusbussbuddy.domain.usecase.BusTrackingRepository
import com.campusbussbuddy.domain.usecase.QrCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindBusTrackingRepository(
        busTrackingRepositoryImpl: BusTrackingRepositoryImpl
    ): BusTrackingRepository
    
    @Binds
    @Singleton
    abstract fun bindQrCodeRepository(
        qrCodeRepositoryImpl: QrCodeRepositoryImpl
    ): QrCodeRepository
}