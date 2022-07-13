package com.uns.mvvm.util

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.IOException
import java.nio.charset.Charset

class AddParamsInterceptor(getMap: HashMap<String, String>, postMap: HashMap<String, String>) : Interceptor {

    companion object { private const val TAG = "AddParamsInterceptor" }

    private var mGETMap = getMap
    private var mPOSTMap = postMap

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url
        val urlBuilder = originalHttpUrl.newBuilder()
        val requestBuilder = original.newBuilder()

        for ((key, value) in mGETMap.entries) {
            urlBuilder.addQueryParameter(key, value)
        }

        val httpUrl = urlBuilder.build()

        if (original.body != null) {
            val requestBody = original.body!!
            val paramList = mPOSTMap.entries.map { "&" + it.key + "=" + it.value }
            val paramLength = paramList.map { it.length }.sum()

            val newRequestBody = object : RequestBody() {
                override fun contentLength(): Long = requestBody.contentLength() + paramLength
                override fun contentType(): MediaType? = requestBody.contentType()

                @Throws(IOException::class)
                override fun writeTo(sink: BufferedSink) {
                    requestBody.writeTo(sink)
                    for (param in paramList) {
                        sink.writeString(param, Charset.forName("UTF-8"))
                    }
                }
            }

            requestBuilder.post(newRequestBody)
        }

        requestBuilder.url(httpUrl)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    class Builder {
        private val mGETMap = hashMapOf<String, String>()
        private val mPOSTMap = hashMapOf<String, String>()

        /**
         * GET 방식 파라미터 추가. (@Query 파라미터와 동일)
         * @param key
         * @param value
         */
        fun addQueryParameter(key: String, value: String) = this.apply { mGETMap[key] = value }

        /**
         * POST 방식 파라미터 추가. (@Field 파라미터와 동일)
         * @param key
         * @param value
         */
        fun addFieldParameter(key: String, value: String) = this.apply { mPOSTMap[key] = value }

        /**
         * GET, POST 방식 파라미터 추가. (@Query, @Field 파라미터 둘 다 추가)
         * @param key
         * @param value
         */
        fun addParameter(key: String, value: String) = this.apply { addQueryParameter(key, value).addFieldParameter(key, value) }

        /**
         * AddParamsInterceptor build.
         */
        fun build() = AddParamsInterceptor(mGETMap, mPOSTMap)
    }
}