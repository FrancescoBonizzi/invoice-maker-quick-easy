package com.francescobonizzi.invoiceEstimateReceiptMaker.storage

import android.content.Context
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.AppConfiguration
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

/** File json matenuto sempre aggiornato in memoria che gestisce i settings dell'applicazione */
class JsonAppConfigurationRepository(val context: Context) : IAppConfigurationRepository
{
    private val _fileName: String = context.filesDir.path.toString() + "/configuration.json"
    private val _appConfiguration: AppConfiguration
    private val _jsonSerializer: Gson = GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create()

    // In inizializzazione carico o creo il file
    init
    {
        val file = File(_fileName)

        if (!file.exists())
        {
            _appConfiguration = AppConfiguration.createEmpty(context)
            save()
        }
        else
        {
            val raw = file.readText()
            _appConfiguration = _jsonSerializer.fromJson(raw, AppConfiguration::class.java)
        }
    }

    override fun get(): AppConfiguration
    {
        return _appConfiguration
    }

    override fun save()
    {
        val file = File(_fileName)
        if (!file.exists())
        {
            file.createNewFile()
        }

        val serialized = _jsonSerializer.toJson(_appConfiguration)
        file.writeText(serialized)
    }

}