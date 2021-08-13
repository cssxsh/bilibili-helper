package xyz.cssxsh.mirai.plugin

import io.ktor.client.features.cookies.*
import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.serialization.*
import kotlin.properties.*

@Serializable
data class EditThisCookie(
    @SerialName("domain")
    val domain: String,
    @SerialName("expirationDate")
    val expirationDate: Double? = null,
    @SerialName("hostOnly")
    val hostOnly: Boolean = false,
    @SerialName("httpOnly")
    val httpOnly: Boolean,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String,
    @SerialName("sameSite")
    val sameSite: String = "unspecified",
    @SerialName("secure")
    val secure: Boolean,
    @SerialName("session")
    val session: Boolean = false,
    @SerialName("storeId")
    val storeId: String = "0",
    @SerialName("value")
    val value: String
)

fun EditThisCookie.toCookie() = Cookie(
    name = name,
    value = value,
    encoding = CookieEncoding.URI_ENCODING,
    expires = expirationDate?.run { GMTDate(times(1000).toLong()) },
    domain = domain,
    path = path,
    secure = secure,
    httpOnly = httpOnly
)

fun Cookie.toEditThisCookie(id: Int = 0) = EditThisCookie(
    name = name,
    value = value,
    expirationDate = expires?.timestamp?.div(1000)?.toDouble(),
    domain = domain.orEmpty(),
    path = path.orEmpty(),
    secure = secure,
    httpOnly = httpOnly,
    id = id
)

private inline fun <reified T : Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
    thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
}

internal val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()