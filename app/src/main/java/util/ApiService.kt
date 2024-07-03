package util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {
    companion object {
        private const val BASE_URL = "https://aiko-olhovivo-proxy.aikodigital.io/"
        private const val AUTH_TOKEN = "9de3c03ac84f6304d9626c489cb92695ff87fab38a3132ef01ec786650c80a6d"

        fun create(): OlhoVivoApi {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $AUTH_TOKEN")
                        .build()
                    chain.proceed(request)
                }
                .build()
            val retrofit  = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(OlhoVivoApi::class.java)
        }
    }
}