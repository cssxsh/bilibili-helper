package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleAuthorPendant(
    @SerialName("expire")
    val expire: Long,
    @SerialName("image")
    val image: String,
    @SerialName("image_enhance")
    val enhance: String,
    @SerialName("image_enhance_frame")
    val frame: String,
    @SerialName("name")
    val name: String,
    @SerialName("pid")
    val pid: Long,
    @SerialName("n_pid")
    val nPid: Long = 0
)