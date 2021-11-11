package com.example.mobileattester.ui.util

// Removes https & other excess from the url
// baseUrl: http://192.168.0.1:4050/
// result: 192.168.0.1:4050
fun parseBaseUrl(baseUrl : String) : String
{
    var nUrl = baseUrl.dropWhile { c -> c != '/' && !c.isDigit() }
    if(!nUrl.first().isDigit())
        nUrl = nUrl.dropWhile { c -> c == '/' }
    return nUrl.takeWhile { c -> c != '/' }
}