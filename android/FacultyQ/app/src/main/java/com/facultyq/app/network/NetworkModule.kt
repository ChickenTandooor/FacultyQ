
package com.facultyq.app.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Socket

object NetworkModule {

    private val baseUrls = listOf(
        "http://192.168.0.247:8000/",
        "http://172.27.177.183:8000/",
        "http://10.235.180.59:8000/",
        "http://10.50.124.0:8000/"
    )

    @Volatile
    private var selectedBaseUrl: String? = null

    @Volatile
    private var apiInstance: FacultyQApi? = null

    private fun isServerReachable(baseUrl: String): Boolean {
        return try {
            val address = baseUrl
                .removePrefix("http://")
                .substringBefore(":")

            Socket().use { socket ->
                socket.connect(
                    InetSocketAddress(address, 8000),
                    1200
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun initialize(): Boolean =
        withContext(Dispatchers.IO) {
            val reachableUrl = baseUrls.firstOrNull {
                isServerReachable(it)
            }

            synchronized(this@NetworkModule) {
                selectedBaseUrl = reachableUrl
                apiInstance = reachableUrl?.let { url ->
                    Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(
                            GsonConverterFactory.create()
                        )
                        .build()
                        .create(FacultyQApi::class.java)
                }
            }

            reachableUrl != null
        }

    val api: FacultyQApi
        get() = apiInstance
            ?: throw IllegalStateException(
                "Backend not found. Call NetworkModule.initialize() first."
            )
}
