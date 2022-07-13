package com.uns.mvvm.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.Call
import okhttp3.EventListener

class AppData {

    companion object {

        private const val TAG = "AppData"

        var PROTOCOL = "https"

        private var isDebugMode = true

        fun debug(tag: String?, msg: String?) { if(isDebugMode) Log.d(tag, msg ?: "msg is null") }
        fun error(tag: String?, msg: String?) { if(isDebugMode) Log.e(tag, msg ?: "msg is null") }
        fun error(tag: String?, msg: String?, ex:Exception?) { if(isDebugMode) Log.e(tag, msg ?: "msg is null", ex) }

        fun Context?.showToast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


//        val BASE_URL_PATH = "/smc-darwin-tab/v1/"
//        val SERVER_HOST = BuildConfig.OUTSIDE_TEST_SERVER_HOST
//        val SERVER_PORT = BuildConfig.OUTSIDE_TEST_SERVER_PORT
//        val BASE_SERVER = "${PROTOCOL}://${SERVER_HOST}:${SERVER_PORT}"
//        val BASE_URL = "${BASE_SERVER}${BASE_URL_PATH}"

        /** retrofit/okHttp 로 요청보낼때 이벤트가 발생하는 단계에서 호출되는 리스너 */
        val httpEventListener: EventListener = object: EventListener() {
            override fun callStart(call: Call) {
                super.callStart(call)
//                resetLogoutTimer()
            }

            override fun callEnd(call: Call) {
                super.callEnd(call)
            }
        }

    }

}