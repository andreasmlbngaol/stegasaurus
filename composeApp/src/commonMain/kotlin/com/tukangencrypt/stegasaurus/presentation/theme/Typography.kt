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
        Font(Res.font.Arimo_Regular, FontWeight.Normal),
        Font(Res.font.Arimo_Italic, FontWeight.Normal, FontStyle.Italic),

        Font(Res.font.Arimo_Medium, FontWeight.Medium),
        Font(Res.font.Arimo_MediumItalic, FontWeight.Medium, FontStyle.Italic),

        Font(Res.font.Arimo_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Arimo_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),

        Font(Res.font.Arimo_Bold, FontWeight.Bold),
        Font(Res.font.Arimo_BoldItalic, FontWeight.Bold, FontStyle.Italic),

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
