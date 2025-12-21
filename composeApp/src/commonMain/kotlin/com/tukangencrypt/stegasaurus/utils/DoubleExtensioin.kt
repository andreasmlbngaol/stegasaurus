package com.tukangencrypt.stegasaurus.utils

import kotlin.math.pow
import kotlin.math.round

fun Double.format(
    decimals: Int,
    thousandSeparator: Char = ',',
    decimalSeparator: Char = '.'
): String {
    val multiplier = 10.0.pow(decimals)
    val rounded = round(this * multiplier) / multiplier

    val parts = rounded.toString().split('.')
    val integerPart = parts[0]
    val decimalPart = parts.getOrElse(1) { "" }

    val withThousands = integerPart
        .reversed()
        .chunked(3)
        .joinToString(thousandSeparator.toString())
        .reversed()

    val paddedDecimal = decimalPart.padEnd(decimals, '0').take(decimals)

    return if (decimals > 0)
        "$withThousands$decimalSeparator$paddedDecimal"
    else
        withThousands
}
