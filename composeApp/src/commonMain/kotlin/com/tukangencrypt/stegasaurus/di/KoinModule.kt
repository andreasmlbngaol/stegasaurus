package com.tukangencrypt.stegasaurus.di

import com.tukangencrypt.stegasaurus.data.repository.CryptoRepositoryImpl
import com.tukangencrypt.stegasaurus.data.repository.ImageRepositoryImpl
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import com.tukangencrypt.stegasaurus.domain.repository.ImageRepository
import com.tukangencrypt.stegasaurus.domain.use_case.EmbedUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.EncryptAndEmbedUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.ExtractAndDecryptUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.ExtractUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.GenerateKeyPairUseCase
import com.tukangencrypt.stegasaurus.presentation.navigation.navigationModule
import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

val mainModules = module {
    single<ImageRepository> { ImageRepositoryImpl() }
    single<CryptoRepository> { CryptoRepositoryImpl() }

    single<EmbedUseCase> { EmbedUseCase(get()) }
    single<ExtractUseCase> { ExtractUseCase(get()) }

    factory { EncryptAndEmbedUseCase(get(), get()) }
    factory { ExtractAndDecryptUseCase(get(), get()) }
    factory { GenerateKeyPairUseCase(get()) }

    factory { HomeViewModel(get(), get(), get()) }
}

fun initKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            mainModules,
            navigationModule
        )
    }
}