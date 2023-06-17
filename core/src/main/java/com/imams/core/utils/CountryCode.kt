package com.imams.core.utils

fun getCountries(): Map<String, String> {
    return hashMapOf(
        "Indonesia" to "id",
        "United States" to "us",
        "Australia" to "id",
        "Brazil" to "br",
        "Canada" to "us",
        "France" to "fr",
        "Germany" to "de",
        "Japan" to "jp",
        "Malaysia" to "my",
        "New Zealand" to "nz",
        "Saudi Arabia" to "sa",
        "Singapore" to "sg",
        "South Korea" to "kr",
        "United Kingdom" to "uk",
    )
}

fun countryLabels() = getCountries().map { it.key }.sorted()

fun String.getCountryCode(): String = getCountries()[this].orEmpty()