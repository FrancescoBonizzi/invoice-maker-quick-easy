package com.francescobonizzi.invoiceEstimateReceiptMaker.storage

import android.content.Context
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customer
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customers
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.ICustomersRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.File

/** File json matenuto sempre aggiornato in memoria che gestisce i clienti */
class JsonCustomersRepository(val context: Context) : ICustomersRepository, KoinComponent
{
    private val _fileName: String = context.filesDir.path.toString() + "/customers.json"
    private val _customers: Customers
    private val _jsonSerializer: Gson = GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create()
    private val _logger: ILogger by inject()

    // In inizializzazione carico o creo il file
    init
    {
        val file = File(_fileName)

        if (!file.exists())
        {
            _customers = Customers(mutableListOf())
            save()
        }
        else
        {
            val raw = file.readText()

            try
            {
                _customers = _jsonSerializer.fromJson(raw, Customers::class.java)
            }
            catch (ex: Exception)
            {
                _logger.error("Errore nel caricamento del file dei clienti. File: $raw", ex)
                throw Exception("Errore nel caricamento del file dei clienti")
            }
        }

    }

    override fun getAll(): MutableList<Customer>
    {
        return _customers.customers
    }

    override fun get(customerId: Int): Customer
    {
        return _customers.customers[customerId]
    }

    override fun add(customer: Customer)
    {
        _customers.customers.add(0, customer)
    }

    override fun delete(customer: Customer)
    {
        _customers.customers.remove(customer)
        save()
    }

    override fun save()
    {
        val file = File(_fileName)
        if (!file.exists())
        {
            file.createNewFile()
        }

        // Non cedere alla tentazione di salvare la lista riordinata,
        // perchè se riassegno la lista poi l'adapter del recyclerview ha il vecchio oggetto!
        val serialized = _jsonSerializer.toJson(_customers)
        file.writeText(serialized)
    }

}