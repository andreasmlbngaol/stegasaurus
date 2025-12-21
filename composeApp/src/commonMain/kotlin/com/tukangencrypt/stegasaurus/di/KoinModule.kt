package com.tukangencrypt.stegasaurus.di

import com.russhwolf.settings.Settings
import com.tukangencrypt.stegasaurus.data.repository.CryptoRepositoryImpl
import com.tukangencrypt.stegasaurus.data.repository.ImageRepositoryImpl
import com.tukangencrypt.stegasaurus.data.repository.KeyRepositoryImpl
import com.tukangencrypt.stegasaurus.data.repository.BenchmarkRepositoryImpl
import com.tukangencrypt.stegasaurus.domain.repository.BenchmarkRepository
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import com.tukangencrypt.stegasaurus.domain.repository.ImageRepository
import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.use_case.*
import com.tukangencrypt.stegasaurus.presentation.screen.benchmark.BenchmarkViewModel
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.DecryptViewModel
import com.tukangencrypt.stegasaurus.presentation.screen.encrypt.EncryptViewModel
import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModules = module {
    singleOf(::Settings)
    singleOf(::ImageRepositoryImpl) { bind<ImageRepository>() }
    singleOf(::CryptoRepositoryImpl) { bind<CryptoRepository>() }
    singleOf(::KeyRepositoryImpl) { bind<KeyRepository>() }
    singleOf(::BenchmarkRepositoryImpl) { bind<BenchmarkRepository>() }

    factoryOf(::BenchmarkUseCase)
    factoryOf(::EmbedUseCase)
    factoryOf(::EncryptAndEmbedUseCase)
    factoryOf(::ExtractUseCase)
    factoryOf(::ExtractAndDecryptUseCase)
    factoryOf(::GenerateKeyPairUseCase)
    factoryOf(::KeyPairUseCase)

    viewModelOf(::HomeViewModel)
    viewModelOf(::EncryptViewModel)
    viewModelOf(::DecryptViewModel)
    viewModelOf(::BenchmarkViewModel)
}

fun initKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            mainModules,
            /**
             * Add this if you want to use the nav3 instead which is can't be build as release for desktop
             */
//            navigationModule
        )
    }
}