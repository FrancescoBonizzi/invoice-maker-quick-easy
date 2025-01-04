package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.francescobonizzi.invoiceEstimateReceiptMaker.BuildConfig
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.google.android.play.core.review.ReviewManagerFactory

class AppReviewHelpers
{
    companion object
    {
        const val MinInvoicesIdentityToAskForReview: Int = 3

        /** Opens the application into the play store */
        fun promptForReviewOnTheStore(activity: Activity)
        {
            val appPackageName: String = BuildConfig.APPLICATION_ID

            try
            {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")))
            }
            catch (exception: ActivityNotFoundException) // It happens if the PlayStore is not installed
            {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }

        /** Prompts - if has enough quotas - the review inside the application */
        fun promptForInAppReview(activity: Activity, logger: ILogger)
        {
            val manager = ReviewManagerFactory.create(activity)

            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { r ->
                if (r.isSuccessful)
                {
                    // We got the ReviewInfo object
                    val reviewInfo = r.result

                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                }
                else
                {
                    // There was some problem, continue regardless of the result.
                    logger.error(r.exception.toString())
                }
            }
        }

    }
}