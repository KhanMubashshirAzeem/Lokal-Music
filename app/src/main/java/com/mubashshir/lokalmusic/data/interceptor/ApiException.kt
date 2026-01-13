package com.mubashshir.lokalmusic.data.interceptor

import java.io.IOException

class ApiException(
    val code: Int,
    override val message: String
) : IOException(message)
