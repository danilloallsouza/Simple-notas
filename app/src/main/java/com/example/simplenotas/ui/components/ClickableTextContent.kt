package com.example.simplenotas.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

// Tags para identificar os tipos de cliques
private const val URL_TAG = "URL"
private const val PHONE_TAG = "PHONE"

// Expressões regulares para identificar URLs e números de telefone
private val URL_REGEX = "(https?://|www\\.)[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)".toRegex()
private val PHONE_REGEX = "(\\+\\d{1,3}\\s?)?\\(?\\d{2,3}\\)?[\\s.-]?\\d{4,5}[\\s.-]?\\d{4}".toRegex()

/**
 * Componente que exibe texto com links e números de telefone clicáveis
 */
@Composable
fun ClickableTextContent(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
) {
    val context = LocalContext.current
    val annotatedText = processTextWithLinks(text)
    
    val contentColor = LocalContentColor.current

    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = textColor
        ),
        modifier = modifier,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = URL_TAG, start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    openUrl(context, annotation.item)
                }
            
            annotatedText.getStringAnnotations(tag = PHONE_TAG, start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    dialPhoneNumber(context, annotation.item)
                }
        }
    )
}

/**
 * Constrói um AnnotatedString com links e números de telefone clicáveis
 */
@Composable
private fun processTextWithLinks(text: String): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        
        // Estilo para links
        val linkStyle = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
        
        // Encontra e anota URLs
        URL_REGEX.findAll(text).forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last + 1
            addStyle(linkStyle, start, end)
            addStringAnnotation(tag = URL_TAG, annotation = matchResult.value, start, end)
        }
        
        // Encontra e anota números de telefone
        PHONE_REGEX.findAll(text).forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last + 1
            addStyle(linkStyle, start, end)
            addStringAnnotation(tag = PHONE_TAG, annotation = matchResult.value, start, end)
        }
    }
}

/**
 * Abre uma URL no navegador
 */
private fun openUrl(context: Context, url: String) {
    var urlToOpen = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        urlToOpen = "https://$url"
    }
    
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
    context.startActivity(intent)
}

/**
 * Abre o discador com o número de telefone
 */
private fun dialPhoneNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber.replace(Regex("[^0-9+]"), "")}" ))
    context.startActivity(intent)
}