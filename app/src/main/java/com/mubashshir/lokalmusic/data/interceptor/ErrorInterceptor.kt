package com.mubashshir.lokalmusic.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            throw mapToApiException(response.code)
        }

        return response
    }

    private fun mapToApiException(code: Int): ApiException {
        return when (code) {

            // 🔵 Client Errors (4xx)
            400 -> ApiException(400, "Bad request. Please try again.")
            401 -> ApiException(401, "Unauthorized. Please login again.")
            403 -> ApiException(403, "Access forbidden.")
            404 -> ApiException(404, "Resource not found.")
            405 -> ApiException(405, "Method not allowed.")
            408 -> ApiException(408, "Request timeout.")

            // 🔵 Conflict & Validation
            409 -> ApiException(409, "Conflict occurred.")
            422 -> ApiException(422, "Validation error.")

            // 🔴 Server Errors (5xx)
            500 -> ApiException(500, "Internal server error.")
            501 -> ApiException(501, "Not implemented.")
            502 -> ApiException(502, "Bad gateway.")
            503 -> ApiException(503, "Service unavailable.")
            504 -> ApiException(504, "Gateway timeout.")

            // 🟠 Unknown
            else -> ApiException(code, "Unexpected error occurred (Code: $code)")
        }
    }
}

