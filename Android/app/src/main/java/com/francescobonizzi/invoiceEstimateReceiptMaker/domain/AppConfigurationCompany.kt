package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class AppConfigurationCompany(
    var name: String? = null,
    var holderName: String? = null,
    var vatNumber: String? = null,
    var address1: String? = null,
    var phone1: String? = null,
    var phone2: String? = null,
    var email1: String? = null,
    var webSite: String? = null,
    var logoImagePath: String? = null,
    var signatureImagePath: String? = null,
    var showCompanyDetails: Boolean = true,
    var showCompanyLogo: Boolean = false,
    var showSignature: Boolean = false
)