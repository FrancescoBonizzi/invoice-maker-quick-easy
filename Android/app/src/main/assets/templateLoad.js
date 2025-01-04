function loadTemplate(rawJson, rawJsonTotals) {

    // Parse e check validità
    var parseResult = parseData(rawJson, rawJsonTotals);
    var parsedData = parseResult[0];
    var parsedTotals = parseResult[1];
    assertParsedDataValidity(rawJson, parsedData, parsedTotals);
    assertImagesValidity();

    // Header
    showIf('logo', parsedData.company.showCompanyLogo);
    showIf('signature', parsedData.company.showSignature);
    showIf('watermark', parsedData.shouldShowWatermark)

    setOrHideIfEmptyAnd(parsedData.code, 'invoiceCode', 'invoiceCodeField', parsedData.showInvoiceCode);
    setOrHideIfEmptyAnd(parsedData.creationDate, 'creationDate', 'creationDateField', parsedData.showInvoiceCreationDate);
    setOrHideIfEmptyAnd(parsedData.expirationDate, 'expirationDate', 'expirationDateField', parsedData.showInvoiceExpirationDate);

    // BilledTo
    setOrHideIfEmptyAnd(parsedData.customer.name, 'customerName', 'customerName', parsedData.customer.showCustomerName);
    showIf('billedToBlock', parsedData.customer.name && parsedData.customer.showCustomerName);
    setOrHideIfEmpty(parsedData.customer.address, 'customerAddress', 'customerAddress');
    setOrHideIfEmptyWithLink(parsedData.customer.email, 'customerEmail1', 'customerEmail1Field', `mailto:${parsedData.customer.email}`);
    setOrHideIfEmpty(parsedData.customer.phone, 'customerPhone1', 'customerPhone1Field');

    // BilledBy
    if (parsedData.company.showCompanyDetails) {

        setOrHideIfEmpty(parsedData.company.name, 'companyName', 'companyName');
        setOrHideIfEmpty(parsedData.company.holderName, 'holderName', 'holderName');
        setOrHideIfEmpty(parsedData.company.address1, 'address1', 'address1');

        setOrHideIfEmpty(parsedData.company.vatNumber, 'businessNumber', 'businessNumberField');
        setOrHideIfEmptyWithLink(parsedData.company.email1, 'email1', 'email1Field', `mailto:${parsedData.company.email1}`);
        setOrHideIfEmpty(parsedData.company.phone1, 'phone1', 'phone1Field');
        setOrHideIfEmpty(parsedData.company.phone2, 'phone2', 'phone2Field');
        setOrHideIfEmptyWithLink(parsedData.company.webSite, 'webSite', 'webSiteField', parsedData.company.webSite);

    } else {
        document.getElementById('billedByBlock').style = 'display: none';
    }

    // Dati banca
    if (parsedData.payment.showBankTransferDetails
        && parsedData.payment.checkHolder
        && parsedData.payment.bankDetails) {

        setOrHideIfEmpty(parsedData.payment.checkHolder, 'checkHolder', 'checkHolderField');
        setOrHideIfEmpty(parsedData.payment.bankDetails, 'bankDetails', 'bankDetailsField');

    } else {
        document.getElementById('bankDetailsBlock').style = 'display: none';
    }

    // PayPal
    setOrHideIfEmptyAndWithLink(parsedData.payment.payPalEmail, 'paypalEmail', 'paypalBlock', parsedData.payment.showPayPalEmail, parsedData.payment.payPalEmail);

    // Pagamenti
    showIf('cashPaymentField', parsedData.payment.showCash);
    showIf('creditCardPaymentField', parsedData.payment.showCreditCard);
    showIf('debitCardPaymentField', parsedData.payment.showDebitCard);

    // Note pagamento
    setOrHideIfEmptyAnd(parsedData.payment.paymentNotes, 'paymentNotes', 'paymentNotes', parsedData.payment.showPaymentNotes);

    // Note footer
    setOrHideIfEmptyAnd(parsedData.footerNotes, 'footerNotes', 'footerNotes', parsedData.showFooterNotes);

    // Articoli - Reverse perché insertArticle li mette uno dopo l'altro
    parsedData.articles.reverse().forEach(a => insertArticle(a));

    // Totali
    setOrHideIfEmptyWithProjectionValue(parsedTotals.formattedPartialAmount, `\u00A0${parsedTotals.formattedPartialAmount}`, 'partialValue', 'partialField');

    showIf('taxesField', parsedData.payment.applyTaxes);
    if (parsedData.payment.applyTaxes) {
        setOrHideIfEmptyWithProjectionValue(parsedTotals.formattedTaxesAmount, `\u00A0${parsedTotals.formattedTaxesAmount}`, 'taxesValue', 'taxesField');

        var taxLabel = "";
        if (parsedData.payment.vatLabel && parsedData.payment.vatPercentage) {
            taxLabel = ` (${parsedData.payment.vatLabel} ${parsedData.payment.vatPercentage}%)`
            appendValue('taxesLabel', taxLabel);
        } else if (parsedData.payment.vatPercentage) {
            taxLabel = ` (${parsedData.payment.vatPercentage}%)`
        } else if (parsedData.payment.vatLabel) {
            appendValue('taxesLabel', taxLabel);
        }

    }

    showIf('discountField', parsedData.payment.applyDiscount);
    if (parsedData.payment.applyDiscount) {
        setOrHideIfEmpty(parsedTotals.formattedDiscountAmount, 'discountValue', 'discountField');

        if (parsedData.payment.discountPercentage) {
            appendValue('discountLabel', ` (${parsedData.payment.discountPercentage + '%'})`);
        }
    }

    setOrHideIfEmptyWithProjectionValue(parsedTotals.formattedTotalAmount, `\u00A0${parsedTotals.formattedTotalAmount}`, 'totalValue', 'totalField');

    // Se ho nascosto tasse e discount, nascondo anche partial, basta il total in quel caso.
    var shouldShowPartialField = !isHidden('taxesField') || !isHidden('discountField');
    showIf('partialField', shouldShowPartialField);

    // Se ho solo il total, gli do un po' di margine perché altrimenti la label si attacca alla cifra
    if (!shouldShowPartialField)
    {
        document.getElementById('totalValue').style.textAlign = 'right';
        document.getElementById('totalValue').style.paddingRight = '10px';
    }

    // Tutti i metodi di pagamento
    showIf(
        'allOtherPaymentsBlock',
        parsedData.payment.showCash || parsedData.payment.showCreditCard
        || parsedData.payment.showDebitCard || parsedData.payment.showPayPalEmail);

    // Colori
    setColors(parsedData.template);
}

function isHidden(domObjectId) {
    var element = document.getElementById(domObjectId);
    var style = window.getComputedStyle(element);
    return (style.display === 'none');
}

function setOrHideIfEmpty(value, propertyForSet, propertyForHide) {

    if (value) {
        document.getElementById(propertyForSet).innerText = value;
    } else {
        document.getElementById(propertyForHide).style = 'display: none';
    }

}

function setOrHideIfEmptyWithProjectionValue(value, toSetValue, propertyForSet, propertyForHide) {

    if (value) {
        document.getElementById(propertyForSet).innerText = toSetValue;
    } else {
        document.getElementById(propertyForHide).style = 'display: none';
    }

}

function setOrHideIfEmptyWithLink(value, propertyForSet, propertyForHide, link) {

    if (value) {
        document.getElementById(propertyForSet).innerText = value;
        document.getElementById(propertyForSet).href = link;
    } else {
        document.getElementById(propertyForHide).style = 'display: none';
    }

}

function setOrHideIfEmptyAnd(value, propertyForSet, propertyForHide, andValue) {

    if (andValue) {
        setOrHideIfEmpty(value, propertyForSet, propertyForHide);
    } else {
        document.getElementById(propertyForHide).style = 'display: none';
    }

}

function setOrHideIfEmptyAndWithLink(value, propertyForSet, propertyForHide, andValue, link) {

    if (andValue) {
        setOrHideIfEmptyWithLink(value, propertyForSet, propertyForHide, link);
    } else {
        document.getElementById(propertyForHide).style = 'display: none';
    }

}

function showIf(property, condition) {
    if (!condition) {
        var domObject = document.getElementById(property);
        if (domObject) {
            domObject.style = 'display: none';
        }
    }
}

function setColors(template) {
    document.documentElement.style.setProperty('--primary-color', template.primaryColor);
    document.documentElement.style.setProperty('--primary-color-lighter', template.primaryColorLighter);
    document.documentElement.style.setProperty('--text-color-over-primary-color', template.textColorOverPrimaryColor);
    document.documentElement.style.setProperty('--key-value-label-color', template.keyValueLabelColor);
    document.documentElement.style.setProperty('--common-text-color', template.commonTextColor);
}

function insertArticle(article) {

    var table = document.getElementById("articles-table");

    // 1: after table header row
    var row = table.tBodies[0].insertRow(0);

    var name = row.insertCell(0);
    var quantity = row.insertCell(1);
    var singleItemPrice = row.insertCell(2);
    var finalAmount = row.insertCell(3);

    name.classList.add('article-item-td');
    name.innerHTML = article.name;

    quantity.classList.add('articles-td');
    quantity.innerHTML = article.quantity;

    singleItemPrice.classList.add('articles-td');
    singleItemPrice.innerHTML = article.formattedSingleItemPrice;

    finalAmount.classList.add('articles-td');
    finalAmount.innerHTML = article.formattedFinalAmount;

}

function appendValue(property, valueToAppend) {
    var control = document.getElementById(property);
    control.innerHTML = control.innerHTML + valueToAppend;
}

function parseData(rawJson, rawJsonTotals) {

    if (rawJson.includes('#EASTER_EGG')) {
        throw '#EASTER_EGG_JSON# non sostituito!';
    }

    if (rawJsonTotals.includes('#EASTER_EGG')) {
        throw '#EASTER_EGG_JSON_TOTALS# non sostituito!';
    }

    var parsedData;
    var parsedTotals;

    try {
        parsedData = JSON.parse(rawJson);
    } catch (error) {
        throw 'rawJson parse error: ' + error + '<br /><br />' + rawJson;
    }

    try {
        var parsedTotals = JSON.parse(rawJsonTotals);
    } catch (error) {
        throw 'rawJsonTotals parse error: ' + error + '<br /><br />' + rawJson;
    }

    return [parsedData, parsedTotals];
}

function assertParsedDataValidity(rawJson, parsedData, parsedTotals) {

    if (!parsedData) {
        throw 'rawJson si è deserializzato a null o vuoto'
    }

    if (!parsedData.customer) {
        throw "Invoice: non è presente l'oggetto Customer: " + rawJson;
    }

    if (!parsedData.company) {
        throw "Invoice: non è presente l'oggetto Company: " + rawJson;
    }

    if (!parsedData.articles || parsedData.articles.length === 0) {
        throw "Invoice: non ci sono articoli: " + rawJson;
    }

    if (!parsedData.payment) {
        throw "Invoice: non è presente l'oggetto Payment: " + rawJson;
    }

    if (!parsedData.template) {
        throw "Invoice: non è presente l'oggetto Template: " + rawJson;
    }

    if (!parsedTotals) {
        throw 'rawJsonTotals si è deserializzato a null o vuoto';
    }
}

function assertImagesValidity() {

    if (document.getElementById('logo').src.includes('#EASTER_EGG')) {
        throw 'Logo image non è stato sostituito. Se non hai immagini, metti una stringa vuota'
    }

    if (document.getElementById('signature').src.includes('#EASTER_EGG')) {
        throw 'Signature image non è stato sostituito. Se non hai immagini, metti una stringa vuota'
    }

}

function loadDebugMode() {

    var rawJson = '{"code":"INV-9","customer":{"showCustomerName":true,"name":"Name","address":"Sample Customer Address Address Address Addressv Address Address Address","email":"samplemail@mail.com","phone":"+1 202 555 0156"},"paymentDate":null,"creationDate":"15/09/2020 ","expirationDate":"15/09/2020 ","company":{"showCompanyDetails":true,"showCompanyLogo":true,"showSignature":true,"name":"SampleCompany S.p.a.","holderName":"Sample Holder","vatNumber":"123456789","address1":"Sample Address For This Company Address Address Address Address Address","phone1":"+1 202 555 0156","phone2":"","email1":"samplemail@mail.com","webSite":"https://imaginesoftware.it","logoImagePath":"logo-test.jpg","signatureImagePath":"signature-test.png"},"regionalSettings":{"iso4217currencyCode":"EUR","dateFormat":"dd/MM/yyyy"},"articles":[{"name":"Sample Red Article","quantity":5,"singleItemPrice":0.99,"formattedSingleItemPrice":"E 0.99","formattedFinalAmount":"E 4.95"},{"name":"Sample Yello Article","quantity":1,"singleItemPrice":0.99,"formattedSingleItemPrice":"E 0.99","formattedFinalAmount":"E 0.99"},{"name":"Sample Black Article","quantity":7,"singleItemPrice":3.99,"formattedSingleItemPrice":"E 3.99","formattedFinalAmount":"E 27.93"}],"template":{"id":"Template1","primaryColor":"#ff6d12","primaryColorLighter":"#fff7f2","textColorOverPrimaryColor":"#ffffff","keyValueLabelColor":"gray","commonTextColor":"black"},"payment":{"applyTaxes":true,"applyDiscount":true,"showPayPalEmail":true,"showCash":true,"showDebitCard":true,"showCreditCard":true,"showBankTransferDetails":true,"showPaymentNotes":true,"payPalEmail":"samplemail@mail.com","bankDetails":"A Very Nice Sample Bank","checkHolder":"Sample Holder","paymentNotes":"Lorem ipsum dolor sit amet, consectetur adipiscing elit.","vatPercentage":22,"vatLabel":"IVA","discountPercentage":8},"showInvoiceCode":true,"showInvoiceCreationDate":true,"showInvoiceExpirationDate":true,"showFooterNotes":true,"footerNotes":"Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "shouldShowWatermark":true}';
    var rawJsonTotals = '{"formattedPartialAmount":"E 100","formattedDiscountAmount":"-E 5","formattedTaxesAmount":"E 20.9","formattedTotalAmount":"E 115.9"}';

    document.getElementById('logo').src = "..\\..\\..\\..\\..\\Assets\\TemplateTests\\logo-test.png";
    document.getElementById('signature').src = "..\\..\\..\\..\\..\\Assets\\TemplateTests\\signature-test.png";

    loadTemplate(rawJson, rawJsonTotals);
}
