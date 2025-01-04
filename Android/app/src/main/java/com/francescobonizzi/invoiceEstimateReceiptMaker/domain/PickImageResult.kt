package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

import android.graphics.Bitmap

data class PickImageResult(
    val imagePath: String,
    val image: Bitmap,
    val selectedImageType: Int
)