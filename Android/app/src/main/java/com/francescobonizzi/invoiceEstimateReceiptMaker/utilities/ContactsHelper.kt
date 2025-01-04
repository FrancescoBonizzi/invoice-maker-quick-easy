package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract

/** Metodi comodi per gestire l'accesso ai contatti del telefono, in particolare a farci le query */
class ContactsHelper
{
    companion object
    {
        /** Ritorna l'id del contatto selezionato */
        fun getContactId(contentResolver: ContentResolver, contactUri: Uri): String?
        {
            val cursor = contentResolver.query(contactUri, null, null, null, null)

            // .use è come lo using di C#
            cursor.use { c ->

                if (c != null && c.moveToFirst())
                {
                    return c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                }

                return null
            }
        }

        /** Ritorna il numero di telefono del contatto selezionato */
        fun getContactPhone(contentResolver: ContentResolver, contactId: String): String
        {
            val phoneQuery = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)

            phoneQuery.use { c ->

                if (c != null && c.moveToFirst())
                {
                    return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                }

                return ""
            }
        }

        /** Ritorna il nome del contatto selezionato */
        fun getContactName(contentResolver: ContentResolver, contactUri: Uri): String
        {
            // Ho dovuto utilizzare una tecnica diversa perché filtrano per Phone ottenevo solo i contatti con numero di telefono
            val cursor: Cursor? = contentResolver.query(contactUri, null, null, null, null)

            cursor.use { c ->

                if (c != null && c.moveToFirst())
                {
                    return c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                }

                return ""
            }
        }

        /** Ritorna l'email del contatto selezionato */
        fun getContactEmail(contentResolver: ContentResolver, contactId: String): String
        {
            val emailQuery = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null)

            emailQuery.use { c ->

                if (c != null && c.moveToFirst())
                {
                    return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                }

                return ""
            }
        }

        /** Ritorna l'indirizzo del contatto selezionato */
        fun getAddress(contentResolver: ContentResolver, contactId: String): String
        {
            val emailQuery = contentResolver.query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId, null, null)

            emailQuery.use { c ->

                if (c != null && c.moveToFirst())
                {
                    return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
                }

                return ""
            }
        }

    }
}