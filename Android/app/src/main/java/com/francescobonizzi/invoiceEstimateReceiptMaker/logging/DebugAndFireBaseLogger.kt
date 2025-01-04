package com.francescobonizzi.invoiceEstimateReceiptMaker.logging

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.francescobonizzi.invoiceEstimateReceiptMaker.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics


/** Logger che scrive in LogCat e su FirebaseCrashlytics */
class DebugAndFireBaseLogger : ILogger
{
    override fun error(message: String)
    {
        var messageToLog = message
        if (messageToLog.isBlank())
        {
            messageToLog = "Generic exception without any message"
        }

        try
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(getCallerInfo(), messageToLog)
            }

            FirebaseCrashlytics.getInstance().recordException(Exception(messageToLog))
        }
        catch (e: Exception)
        {
            // Il logger non deve rompere l'applicazione, MAI
        }
    }

    override fun error(exception: Exception)
    {
        try
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(getCallerInfo(), "Exception thrown!", exception)
            }

            FirebaseCrashlytics.getInstance().recordException(exception)
        }
        catch (e: Exception)
        {
            // Il logger non deve rompere l'applicazione, MAI
        }
    }

    override fun error(message: String, exception: Exception)
    {
        try
        {
            if (BuildConfig.DEBUG)
            {
                Log.e(getCallerInfo(), message, exception)
            }

            FirebaseCrashlytics.getInstance().recordException(exception)
        }
        catch (e: Exception)
        {
            // Il logger non deve rompere l'applicazione, MAI
        }
    }

    override fun event(context: Context, eventName: String, eventProperties: Bundle)
    {
        FirebaseAnalytics.getInstance(context).logEvent(eventName, eventProperties)
    }

    private fun getCallerInfo(): String
    {
        val caller = Thread.currentThread().stackTrace[4]
        return "${caller.className}.${caller.methodName}"
    }
}