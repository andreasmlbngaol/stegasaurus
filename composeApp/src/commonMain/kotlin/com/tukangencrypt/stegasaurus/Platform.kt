package com.tukangencrypt.stegasaurus

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform