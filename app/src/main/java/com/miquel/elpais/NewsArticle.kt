package com.miquel.elpais

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

data class NewsItem(
    val title: String,
    val link: String,
    val date: String,
    val content: String,
    val author: String? = null,
    val imageUrl: String? = null
)

suspend fun getNewsList(rssUrl: String): MutableList<NewsItem> {
    val newsItems = mutableListOf<NewsItem>()
    withContext(Dispatchers.IO) { // Mover la operación de red fuera del hilo principal
        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(URL(rssUrl).openStream())
        val items = document.getElementsByTagName("item")
        for (i in 0 until items.length) {
            val item = items.item(i) as Element
            val title = item.getTextContent("title") ?: "Sin título"
            val link = item.getTextContent("link") ?: "Sin enlace"
            val d=item.getTextContent("pubDate")?: "Thu, 05 Dec 2024 13:00:00 GMT"
            val date = formatDate(d)
            val content = item.getTextContent("description") ?: "Sin descripción"
            val author = item.getTextContent("dc:creator") ?: "Autor no disponible"
            val imageUrl = item.getAttributeContent("media:content", "url")

            newsItems.add(
                NewsItem(title,link,date,content,author,
                    imageUrl?: "https://upload.wikimedia.org/wikipedia/commons/0/0a/No-image-available.png"
                )
            )
        }
    }
    return newsItems
}

private fun formatDate(input: String): String {
    return input.substring(5,16)
}
private fun Element.getTextContent(tag: String): String? {
    return this.getElementsByTagName(tag).item(0)?.textContent?.trim()
}

private fun Element.getAttributeContent(tag: String, attribute: String): String? {
    val node = this.getElementsByTagName(tag).item(0) as? Element
    return node?.getAttribute(attribute)?.takeIf { it.isNotBlank() }
}
