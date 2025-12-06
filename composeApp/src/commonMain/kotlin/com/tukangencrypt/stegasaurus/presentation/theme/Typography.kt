package com.tukangencrypt.stegasaurus.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import stegasaurus.composeapp.generated.resources.*

private val GoogleSansCode
    @Composable
    get() = FontFamily(
        Font(Res.font.GoogleSansCode_Regular, FontWeight.Normal),
        Font(Res.font.GoogleSansCode_Italic, FontWeight.Normal, FontStyle.Italic),

        Font(Res.font.GoogleSansCode_Light, FontWeight.Light),
        Font(Res.font.GoogleSansCode_LightItalic, FontWeight.Light, FontStyle.Italic),

        Font(Res.font.GoogleSansCode_Medium, FontWeight.Medium),
        Font(Res.font.GoogleSansCode_MediumItalic, FontWeight.Medium, FontStyle.Italic),

        Font(Res.font.GoogleSansCode_SemiBold, FontWeight.SemiBold),
        Font(Res.font.GoogleSansCode_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),

        Font(Res.font.GoogleSansCode_Bold, FontWeight.Bold),
        Font(Res.font.GoogleSansCode_BoldItalic, FontWeight.Bold, FontStyle.Italic),

        Font(Res.font.GoogleSansCode_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.GoogleSansCode_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
    )


@Composable
fun googleSansCodeTypography(): Typography {
    val base = MaterialTheme.typography

    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = GoogleSansCode),
        displayMedium = base.displayMedium.copy(fontFamily = GoogleSansCode),
        displaySmall = base.displaySmall.copy(fontFamily = GoogleSansCode),

        headlineLarge = base.headlineLarge.copy(fontFamily = GoogleSansCode),
        headlineMedium = base.headlineMedium.copy(fontFamily = GoogleSansCode),
        headlineSmall = base.headlineSmall.copy(fontFamily = GoogleSansCode),

        titleLarge = base.titleLarge.copy(fontFamily = GoogleSansCode),
        titleMedium = base.titleMedium.copy(fontFamily = GoogleSansCode),
        titleSmall = base.titleSmall.copy(fontFamily = GoogleSansCode),

        bodyLarge = base.bodyLarge.copy(fontFamily = GoogleSansCode),
        bodyMedium = base.bodyMedium.copy(fontFamily = GoogleSansCode),
        bodySmall = base.bodySmall.copy(fontFamily = GoogleSansCode),

        labelLarge = base.labelLarge.copy(fontFamily = GoogleSansCode),
        labelMedium = base.labelMedium.copy(fontFamily = GoogleSansCode),
        labelSmall = base.labelSmall.copy(fontFamily = GoogleSansCode),
    )
}
