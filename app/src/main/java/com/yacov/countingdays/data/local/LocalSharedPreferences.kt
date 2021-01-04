package com.yacov.countingdays.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE

class LocalSharedPreferences(
    private val context: Context
) {

    fun setSharedPreferences(key: String, value: String) {
        context.getSharedPreferences("sp", MODE_PRIVATE).edit().putString(key, value).apply()
    }

    fun getSharedPreferences(key: String): String? =
        context.getSharedPreferences("sp", MODE_PRIVATE).getString(key, "")
}
