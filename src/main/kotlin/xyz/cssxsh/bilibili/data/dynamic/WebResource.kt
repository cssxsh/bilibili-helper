package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("res_type")
sealed class WebResource {
    abstract val source: WebSource

    @Serializable
    @SerialName("3")
    data class Image(
        @SerialName("res_image")
        val image: ResourceImage
    ) : WebResource() {
        override val source: WebSource get() = image.source
    }

    @Serializable
    data class ResourceImage(
        @SerialName("image_src")
        val source: WebSource
    )

    @Serializable
    @SerialName("4")
    data class Animation(
        @SerialName("res_animation")
        val image: ResourceAnimation
    ) : WebResource() {
        override val source: WebSource get() = image.source
    }

    @Serializable
    data class ResourceAnimation(
        @SerialName("webp_src")
        val source: WebSource
    )
}