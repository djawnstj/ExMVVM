package com.uns.mvvm.data.api

import com.uns.mvvm.data.AppData
import com.uns.mvvm.data.model.RepoModel
import com.uns.mvvm.util.AddParamsInterceptor
import io.reactivex.rxjava3.core.Single
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

interface GitApi {
    @GET("/users/{user}/repos")
    fun postRepoList(@Path("user") user: String): Single<List<RepoModel>>
}

class GitClient {
    companion object {

        private const val TAG = "GitClient"

        private var instance : GitApi? = null

        @JvmStatic
        val api: GitApi get() { return getInstance() }

        lateinit var dispatcher: Dispatcher

        @Synchronized
        fun getInstance(): GitApi {
            if (instance == null) instance = create()
            return instance as GitApi
        }

        /**
         * 인스턴스를 새로 생성
         */
        @Synchronized
        @JvmStatic
        fun resetInstance() { instance = create() }

        fun create(): GitApi {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("X-Client-Id", "djawnstj")
                    .addHeader("X-Client-Secret", "djawnstj")
                    .addHeader("X-Client-UserId", "djawnstj")
                    .removeHeader("User-Agent")
                    .build()
                return@Interceptor it.proceed(request)
            }


            val clientBuilder = OkHttpClient.Builder()

            //========== SSL support START ==========//
            if (AppData.PROTOCOL == "https") {

                // X509TrustManager
                val x509TrustManager: X509TrustManager = object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                        AppData.debug(TAG, ": authType: $authType")
                    }

                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                        AppData.debug(TAG, ": authType: $authType")
                    }
                }

                try {
                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, trustAllCerts, SecureRandom())
                    val sslSocketFactory = sslContext.socketFactory
                    clientBuilder.sslSocketFactory(sslSocketFactory, x509TrustManager)
                } catch (e: Exception) {
                    AppData.error(TAG, e.message)
                }

                clientBuilder.hostnameVerifier(RelaxedHostNameVerifier())

            }
            //========== SSL support END ==========//

            /** 모든 요청에 파라미터를 고정시키는 인터셉터 */
            val addParamsInterceptor = AddParamsInterceptor.Builder()
                .addFieldParameter("sessionId", "empty" ?: "")
                .addFieldParameter("sessionKey", "empty" ?: "")
                .build()

            /** 응답이 왔을때 코드를 체크해서 특정 이벤트를 호출하는 인터셉터 */
            val responseInterceptor = Interceptor {
                val request = it.request()

                val response = it.proceed(request)
                AppData.error(TAG, "코드 : ${response}")
                if (response.code == 303) ""

                response
            }

            clientBuilder.addInterceptor(headerInterceptor)
            clientBuilder.addInterceptor(httpLoggingInterceptor)
            clientBuilder.addInterceptor(addParamsInterceptor)
            clientBuilder.addInterceptor(responseInterceptor)
            clientBuilder.connectTimeout(180, TimeUnit.SECONDS)  // 타임아웃 시간 설정 180초
            clientBuilder.eventListener(AppData.httpEventListener)
            val client = clientBuilder.build()

            dispatcher = client.dispatcher

            return Retrofit.Builder()
                .baseUrl("${AppData.PROTOCOL}://api.github.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(GitApi::class.java)
        }


        //========== SSL support START ==========//
        private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        })

        class RelaxedHostNameVerifier : HostnameVerifier {
            override fun verify(hostname: String, session: SSLSession): Boolean {
                return true
            }
        }
        //========== SSL support END ==========//

    }
}