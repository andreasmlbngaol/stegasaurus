package com.tukangencrypt.stegasaurus.di

import com.russhwolf.settings.Settings
import com.tukangencrypt.stegasaurus.data.repository.CryptoRepositoryImpl
import com.tukangencrypt.stegasaurus.data.repository.ImageRepositoryImpl
import com.tukangencrypt.stegasaurus.data.repository.KeyRepositoryImpl
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import com.tukangencrypt.stegasaurus.domain.repository.ImageRepository
import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.use_case.*
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.DecryptViewModel
import com.tukangencrypt.stegasaurus.presentation.screen.encrypt.EncryptViewModel
import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

val mainModules = module {
    single<Settings> { Settings() }

    single<ImageRepository> { ImageRepositoryImpl() }
    single<CryptoRepository> { CryptoRepositoryImpl(get()) }
    single<KeyRepository> { KeyRepositoryImpl(get()) }

    single<EmbedUseCase> { EmbedUseCase(get()) }
    single<ExtractUseCase> { ExtractUseCase(get()) }


    factory { EncryptAndEmbedUseCase(get(), get(), get()) }
    factory { ExtractAndDecryptUseCase(get(), get(), get()) }
    factory { GenerateKeyPairUseCase(get()) }
    factory { KeyPairUseCase(get(), get()) }

    factory { HomeViewModel(get()) }
    factory { EncryptViewModel(get(), get()) }
    factory { DecryptViewModel(get(), get()) }
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