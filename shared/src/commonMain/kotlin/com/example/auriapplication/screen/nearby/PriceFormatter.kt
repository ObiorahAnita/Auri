package com.example.auriapplication.screen.nearby

fun formatPriceLevel(priceLevel: String?): String? {
    return when (priceLevel) {
        "PRICE_LEVEL_FREE" -> "Free"
        "PRICE_LEVEL_INEXPENSIVE" -> "€"
        "PRICE_LEVEL_MODERATE" -> "€€"
        "PRICE_LEVEL_EXPENSIVE" -> "€€€"
        "PRICE_LEVEL_VERY_EXPENSIVE" -> "€€€€"
        else -> null
    }
}

fun formatPriceRange(priceRange: PriceRange?, priceLevel: String? = null): String? {
    val start = priceRange?.startPrice
    val end = priceRange?.endPrice

    if (start == null && end == null) return formatPriceLevel(priceLevel)

    val currencyCode = start?.currencyCode ?: end?.currencyCode
    val currency = when (currencyCode) {
        "EUR" -> "€"
        "USD" -> "$"
        "GBP" -> "£"
        null -> ""
        else -> currencyCode
    }

    val startVal = start?.units ?: 0
    val endVal = end?.units ?: 0

    return when {
        startVal > 0 && endVal > 0 -> "$currency$startVal-$endVal"
        startVal > 0 -> "From $currency$startVal"
        endVal > 0 -> "Up to $currency$endVal"
        else -> formatPriceLevel(priceLevel)
    }
}
