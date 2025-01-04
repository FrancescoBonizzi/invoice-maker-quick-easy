package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger

/** Gestore delle SharedPreferences singleton perché sembra che in alcuni casi vengano scritti dei valori e letti altri */
class SharedPreferencesHelper
{
    companion object
    {
        private var _sharedPreferences: SharedPreferences? = null
        private lateinit var _logger: ILogger

        fun init(activity: Activity, logger: ILogger)
        {
            _logger = logger

            if (_sharedPreferences == null)
            {
                _sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
            }

            if (_sharedPreferences == null)
            {
                _logger.error("Can't get a SharedPreference!")
            }
        }

        fun getOrPutBool(key: String, defaultValue: Boolean): Boolean
        {
            return _sharedPreferences!!.getBoolean(key, defaultValue)
        }

        fun putBool(key: String, value: Boolean)
        {
            val edit = _sharedPreferences!!.edit()
            edit.putBoolean(key, value)
            edit.apply()
        }
    }
}