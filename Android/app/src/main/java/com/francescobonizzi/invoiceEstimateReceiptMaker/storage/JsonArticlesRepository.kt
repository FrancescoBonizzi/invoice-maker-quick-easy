package com.francescobonizzi.invoiceEstimateReceiptMaker.storage

import android.content.Context
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Article
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Articles
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IArticlesRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.File

/** File json matenuto sempre aggiornato in memoria che gestisce gli articoli */
class JsonArticlesRepository(val context: Context) : IArticlesRepository, KoinComponent
{
    private val _fileName: String = context.filesDir.path.toString() + "/articles.json"
    private val _articles: Articles
    private val _jsonSerializer: Gson = GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create()
    private val _logger: ILogger by inject()

    // In inizializzazione carico o creo il file
    init
    {
        val file = File(_fileName)

        if (!file.exists())
        {
            _articles = Articles(mutableListOf())
            save()
        }
        else
        {
            val raw = file.readText()

            try
            {
                _articles = _jsonSerializer.fromJson(raw, Articles::class.java)
            }
            catch (ex: Exception)
            {
                _logger.error("Errore nel caricamento del file degli articoli. File: $raw", ex)
                throw Exception("Errore nel caricamento del file degli articoli")
            }
        }

    }

    override fun getAll(): MutableList<Article>
    {
        return _articles.articles
    }

    override fun get(articleId: Int): Article
    {
        return _articles.articles[articleId]
    }

    override fun add(article: Article)
    {
        _articles.articles.add(0, article)
    }

    override fun delete(article: Article)
    {
        _articles.articles.remove(article)
        save()
    }

    // Serializzo su disco
    override fun save()
    {
        val file = File(_fileName)
        if (!file.exists())
        {
            file.createNewFile()
        }

        val serialized = _jsonSerializer.toJson(_articles)
        file.writeText(serialized)
    }
}