package com.sherryyuan.wordy.utils

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * Converts a [Spanned] into a styled [AnnotatedString].
 * Currently supports bold, italic, bold-italic, underline, foreground color, and background color.
 * Based on https://proandroiddev.com/how-to-display-styled-strings-in-jetpack-compose-decd6b705746
 */
fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    // Append raw text
    append(this@toAnnotatedString.toString())
    // Iterate through and add spans
    getSpans(0, length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        val spanStyle: SpanStyle? = when (span) {
            is StyleSpan -> {
                when (span.style) {
                    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                    Typeface.BOLD_ITALIC -> SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                    )

                    else -> null
                }
            }

            is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
            is ForegroundColorSpan -> SpanStyle(color = Color(span.foregroundColor))
            is BackgroundColorSpan -> SpanStyle(background = Color(span.backgroundColor))
            else -> null
        }
        spanStyle?.let { addStyle(it, start, end) }
    }
}
