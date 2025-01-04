package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.PickImageResult
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt


/** Helps with the Intents management */
class IntentHelpers
{
    companion object
    {
        const val IntentPickLogoCode = 1
        const val IntentPickSignatureCode = 2
        const val IntentSelectContactCode = 3
        const val PermissionSelectContacts = 4
        const val IntentSettings = 5
        const val PermissionStorage = 6

        /** Start an intent to choose one local contact */
        fun startIntentToSelectContact(fragment: Fragment)
        {
            if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                fragment.requestPermissions(
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PermissionSelectContacts)
                return
            }

            val intent = Intent(Intent.ACTION_PICK).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
            }

            // TODO: A cosa serve? Non ricordo
            //if (intent.resolveActivity(fragment.requireActivity().packageManager) != null)
            //{
            fragment.startActivityForResult(intent, IntentSelectContactCode)
            //}
        }

        /** Start an intent to share a file specified by a path */
        fun startIntentToShareFile(
            fragment: Fragment,
            filePath: String,
            fileNameForDialogTitle: String)
        {
            val context = fragment.requireContext()
            val fileToShare = File(filePath)
            fileToShare.deleteOnExit()

            if (!fileToShare.exists())
            {
                throw Exception("Attempt to share a non existent file: $filePath")
            }

            val fileUri = FileProvider.getUriForFile(
                context,
                "com.francescobonizzi.invoiceEstimateReceiptMaker.fileprovider",
                fileToShare)

            val shareIntent = ShareCompat.IntentBuilder.from(fragment.requireActivity())
                .setType("application/pdf")
                .setStream(fileUri)
                .setSubject(fileNameForDialogTitle)
                .setChooserTitle(fileNameForDialogTitle)
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val resInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList)
            {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            fragment.startActivity(shareIntent)
        }

        /** Start an intent to select an image from the phone */
        fun startIntentToPickImage(fragment: Fragment, pickActionCode: Int)
        {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent =
                Intent.createChooser(
                    getIntent,
                    fragment.getString(R.string.select_image_intent_message))
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            fragment.startActivityForResult(chooserIntent, pickActionCode)
        }

        /** Manages the startIntentToPickImage result: it copies the selected image into the app internal storage */
        fun getPathOnPickImageIntentResult(
            context: Context,
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ): PickImageResult?
        {
            if (resultCode != Activity.RESULT_OK)
            {
                return null
            }

            val pickedFilePath = data?.data ?: return null
            val selectedImage = Helpers.getBitmap(context, pickedFilePath)

            val imagePath = when (requestCode)
            {
                IntentPickLogoCode ->
                {
                    "${context.filesDir}/company-logo-image.jpg"
                }
                IntentPickSignatureCode ->
                {
                    "${context.filesDir}/signature-image.jpg"
                }
                else ->
                {
                    throw Exception("RequestCode not implemented: $requestCode")
                }
            }

            val logoImageFile = File(imagePath)

            if (logoImageFile.exists())
            {
                logoImageFile.delete()
            }

            val outputStream = FileOutputStream(logoImageFile, false)
            outputStream.use { stream ->
                val imageToSave = selectedImage.resizeByWidth(512)
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            }

            return PickImageResult(imagePath, selectedImage, requestCode)
        }

        private fun Bitmap.resizeByWidth(width: Int): Bitmap
        {
            val ratio: Float = this.width.toFloat() / this.height.toFloat()
            val height: Int = (width / ratio).roundToInt()

            return Bitmap.createScaledBitmap(
                this,
                width,
                height,
                false
            )
        }


    }
}