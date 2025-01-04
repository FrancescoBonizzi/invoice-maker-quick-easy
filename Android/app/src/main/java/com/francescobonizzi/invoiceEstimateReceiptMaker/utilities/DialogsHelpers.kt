package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.app.Activity
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.google.android.material.snackbar.Snackbar

/** Una serie di metodi di comodo per inviare all'utente una notifica interna all'applicazione */
class DialogsHelpers
{
    companion object
    {
        /** Mostra un messaggio d'errore */
        fun showError(activity: Activity, message: String)
        {
            val messageDialog = Snackbar.make(
                activity.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT)
            messageDialog.setBackgroundTint(activity.resources.getColor(R.color.colorError, null))
            messageDialog.show()
        }

        /** Mostra un messaggio di conferma */
        fun showConfirmation(activity: Activity, message: String)
        {
            val messageDialog = Snackbar.make(
                activity.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT)
            messageDialog.setBackgroundTint(activity.resources.getColor(R.color.colorNiceThing, null))
            messageDialog.show()
        }
    }

}