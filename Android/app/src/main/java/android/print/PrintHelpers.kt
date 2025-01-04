package android.print

import android.app.Activity
import android.content.Context
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.webkit.WebView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import java.io.File

/** Metodi di comodo per gestire la stampante, e in particolare la stampa di PDF */
class PrintHelpers
{
    companion object
    {
        /** Prints the content of a WebView to a printer (lol) */
        fun printWebViewContent(
            context: Context,
            webView: WebView,
            onOperationFinished: () -> Unit)
        {
            try
            {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager

                if (printManager == null)
                {
                    onOperationFinished()
                    throw Exception("Print Manager null")
                }

                printManager.let {

                    val jobName = "Print PDF"
                    val printAdapter = webView.createPrintDocumentAdapter(jobName)

                    val printAttributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()

                    it.print(
                        jobName,
                        printAdapter,
                        printAttributes)
                }
            }
            finally
            {
                // Non è proprio quando l'operazione è terminata, però ci siamo vicini
                onOperationFinished()
            }
        }

        /** Prints the content of a WebView to a file */
        fun printPdfToFile(
            activity: Activity,
            context: Context,
            webView: WebView,
            filePath: String,
            onSuccess: () -> Unit,
            onOperationFinished: () -> Unit,
            onError: (message: String) -> Unit)
        {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager

            if (printManager == null)
            {
                onOperationFinished()
                throw Exception("Print Manager null")
            }

            val jobName = "Print PDF to save it"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)

            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()

            printAdapter.onLayout(
                null,
                printAttributes,
                null,
                object : LayoutResultCallback()
                {
                    override fun onLayoutCancelled()
                    {
                        super.onLayoutCancelled()
                        onError("Layout cancelled")
                        onOperationFinished()
                    }

                    override fun onLayoutFailed(error: CharSequence?)
                    {
                        super.onLayoutFailed(error)
                        onError("Layout failed." + error.toString())
                        onOperationFinished()
                    }

                    override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean)
                    {
                        try
                        {
                            val file = File(filePath)
                            file.createNewFile()

                            val parcelFileDescriptor = ParcelFileDescriptor.open(
                                file,
                                ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE)

                            printAdapter.onWrite(
                                arrayOf(PageRange.ALL_PAGES),
                                parcelFileDescriptor,
                                CancellationSignal(),
                                object : WriteResultCallback()
                                {
                                    override fun onWriteCancelled()
                                    {
                                        super.onWriteCancelled()
                                        onOperationFinished()
                                        onError("Write cancelled.")
                                        parcelFileDescriptor.close()
                                    }

                                    override fun onWriteFinished(pages: Array<out PageRange>?)
                                    {
                                        super.onWriteFinished(pages)
                                        onSuccess()
                                        onOperationFinished()
                                        parcelFileDescriptor.close()
                                    }

                                    override fun onWriteFailed(error: CharSequence?)
                                    {
                                        super.onWriteFailed(error)
                                        onError("Write failed." + error.toString())
                                        onOperationFinished()
                                        DialogsHelpers.showError(
                                            activity,
                                            activity.getString(R.string.generic_error_rendering))
                                        parcelFileDescriptor.close()
                                    }
                                })
                        }
                        catch (exception: Exception)
                        {
                            onOperationFinished()
                            throw exception
                        }
                    }
                }, null)
        }
    }
}
