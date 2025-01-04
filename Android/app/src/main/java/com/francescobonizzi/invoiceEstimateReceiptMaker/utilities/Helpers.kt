package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Patterns
import androidx.navigation.NavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.AppConfiguration
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.AppConfigurationRegionalSettings
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Helpers
{
    companion object
    {
        /** Ritorna il nome del fragment precedente nel grafo di navigazione */
        @SuppressLint("RestrictedApi")
        fun getPreviousPageTitle(navigationHost: NavController): CharSequence
        {
            val backStack = navigationHost.currentBackStack.value
            if (backStack.size > 1)
            {
                val prevDest = backStack[backStack.size - 2]
                return prevDest.destination.label.toString()
            }

            return ""
        }

        /** Valida sintatticamente un'email */
        fun isValidEmail(target: CharSequence?): Boolean =
            if (target == null) false else Patterns.EMAIL_ADDRESS.matcher(target).matches()

        /** Carica un bitmap dato il path */
        fun getBitmap(imagePath: String): Bitmap
        {
            return BitmapFactory.decodeFile(imagePath)
        }

        fun getBitmap(inputStream: InputStream): Bitmap
        {
            return BitmapFactory.decodeStream(inputStream)
        }

        /** Carica un bitmap dato l'uri */
        fun getBitmap(context: Context, imageUri: Uri): Bitmap
        {
            context.contentResolver
                .openInputStream(imageUri)
                .use { it.use { stream -> return BitmapFactory.decodeStream(stream) } }
        }

        /** Ritorna il servizio di formattazione delle valute configurato */
        fun getCurrencyFormat(regionalSettings: AppConfigurationRegionalSettings): NumberFormat
        {
            if (regionalSettings.iso4217currencyCode.isNullOrBlank())
            {
                throw Exception("iso4217currencyCode null or empty")
            }

            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance(regionalSettings.iso4217currencyCode)

            return format
        }

        /** Ritorna il servizio di formattazione delle valute configurato */
        fun getCurrencyFormat(appConfiguration: AppConfiguration): NumberFormat
        {
            return getCurrencyFormat(appConfiguration.regionalSettings)
        }

        /** Ritorna il servizio di formattazione delle date configurato */
        @SuppressLint("SimpleDateFormat")
        fun getDateFormatter(appConfiguration: AppConfiguration): SimpleDateFormat
        {
            if (appConfiguration.regionalSettings.dateFormat.isNullOrBlank())
            {
                throw Exception("dateFormat null or empty")
            }

            return SimpleDateFormat(appConfiguration.regionalSettings.dateFormat)
        }

        /** Deletes all files in the root the cache folder (it isn't recursive!) */
        fun clearCacheFiles(context: Context)
        {
            val cacheFolder = context.cacheDir
            val files = cacheFolder.listFiles()

            if (files != null)
            {
                for (file in files)
                {
                    file.delete()
                }
            }

            ensureCacheExistance(context)
        }

        fun convertiStringaInNomeFile(stringa: String): String {
            val regex = "[^A-Za-z0-9._-]".toRegex() // Regex per caratteri non validi nei nomi dei file
            val sostituzione = "_"

            // Rimuove i caratteri non validi sostituendoli con '_'
            val nomeFile = stringa.replace(regex, sostituzione)

            // Limite di lunghezza del nome file (potresti volerlo adattare alle tue esigenze)
            val lunghezzaMassima = 100
            return if (nomeFile.length > lunghezzaMassima) {
                nomeFile.substring(0, lunghezzaMassima)
            } else {
                nomeFile
            }
        }

        fun ensureCacheExistance(context: Context)
        {
            val cacheFolder = context.cacheDir
            if (!cacheFolder.exists())
            {
                cacheFolder.mkdir()
            }
        }

        /** Converts a string to double considering also ',' decimal char. If empty or null, returns 0.0 */
        fun textToDouble(text: String?): Double
        {
            if (text.isNullOrBlank())
            {
                return 0.0
            }

            // To avoid people trying to save "." or something similar
            return try
            {
                text.trim().replace(',', '.').toDouble()
            }
            catch (exception: Exception)
            {
                0.0
            }
        }

        /** Converts a string to double considering also ',' decimal char. If empty or null, returns null. */
        fun textToDoubleOrNull(text: String?): Double?
        {
            if (text.isNullOrBlank())
            {
                return null
            }

            return textToDouble(text)
        }

    }

}