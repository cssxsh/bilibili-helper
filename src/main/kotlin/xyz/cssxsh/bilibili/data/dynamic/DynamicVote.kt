package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import java.time.*

@Serializable
data class DynamicVote(
    @SerialName("button")
    val button: DynamicButton? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("choice_cnt")
    val choices: Int,
    @SerialName("default_share")
    @Serializable(NumberToBooleanSerializer::class)
    val share: Boolean = false,
    @SerialName("desc")
    val description: String = "",
    @SerialName("end_time")
    @Serializable(InstantSerializer::class)
    val end: Instant,
    @SerialName("join_num")
    val joined: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("type")
    val type: Int? = null,
    @SerialName("uid")
    val uid: Long = 0,
    @SerialName("vote_id")
    val voteId: Long
)