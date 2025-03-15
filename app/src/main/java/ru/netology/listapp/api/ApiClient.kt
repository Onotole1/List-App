package ru.netology.listapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import ru.netology.listapp.BuildConfig

val ApiClient: HttpClient = HttpClient(OkHttp) {
    expectSuccess = true
    addDefaultResponseValidation()

    if (BuildConfig.DEBUG) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
}
