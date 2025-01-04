package com.francescobonizzi.invoiceEstimateReceiptMaker.staticData

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateColor
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateLayout

/** I dati dei template */
class TemplatesData
{
    companion object
    {
        fun getAllColors(): Array<TemplateColor>
        {
            return arrayOf(
                TemplateColor(id = 1, primaryColor = "#ffbb5a", primaryColorLighter = "#fff7f1", textColorOverPrimaryColor = "#ffffff", keyValueLabelColor = "gray", commonTextColor = "black"),
                TemplateColor(id = 2, primaryColor = "#ff8b8c", primaryColorLighter = "#fff0f0", textColorOverPrimaryColor = "#ffffff", keyValueLabelColor = "gray", commonTextColor = "black"),
                TemplateColor(id = 3, primaryColor = "#c4a6fe", primaryColorLighter = "#f8f5ff", textColorOverPrimaryColor = "#ffffff", keyValueLabelColor = "gray", commonTextColor = "black"),
                TemplateColor(id = 4, primaryColor = "#2e9eff", primaryColorLighter = "#eaf5fe", textColorOverPrimaryColor = "#ffffff", keyValueLabelColor = "gray", commonTextColor = "black"),
                TemplateColor(id = 5, primaryColor = "#c9c9c9", primaryColorLighter = "#fafafa", textColorOverPrimaryColor = "black", keyValueLabelColor = "black", commonTextColor = "black")
            )
        }

        fun getAllLayouts(): Array<TemplateLayout>
        {
            return arrayOf(
                TemplateLayout("Layout 1", "template1.png", "template1.html"),
                TemplateLayout("Layout 2", "template2.png", "template2.html"),
                TemplateLayout("Layout 3", "template3.png", "template3.html"),
                TemplateLayout("Layout 4", "template4.png", "template4.html"),
                TemplateLayout("Layout 5", "template5.png", "template5.html")
            )
        }
    }
}