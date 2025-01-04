package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

class InvoiceCompany(
    var showCompanyDetails: Boolean = false,
    var showCompanyLogo: Boolean = false,
    var showSignature: Boolean = false,

    var name: String? = null,
    var holderName: String? = null,
    var vatNumber: String? = null,
    var address1: String? = null,
    var phone1: String? = null,
    var phone2: String? = null,
    var email1: String? = null,
    var webSite: String? = null
)