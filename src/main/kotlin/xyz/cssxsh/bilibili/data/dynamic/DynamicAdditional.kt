package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
sealed class DynamicAdditional {
    abstract val content: Any

    @Serializable
    @SerialName("ADDITIONAL_TYPE_NONE")
    data class ADDITIONAL_TYPE_NONE(
        @SerialName("none")
        override val content: String
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_PGC")
    data class ADDITIONAL_TYPE_PGC(
        @SerialName("pgc")
        override val content: String,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_GOODS")
    data class Goods(
        @SerialName("goods")
        override val content: DynamicGoods,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_VOTE")
    data class Vote(
        @SerialName("vote")
        override val content: DynamicVote,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_COMMON")
    data class Common(
        @SerialName("common")
        override val content: DynamicCommon,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_MATCH")
    data class ADDITIONAL_TYPE_MATCH(
        @SerialName("match")
        override val content: String,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_UP_RCMD")
    data class ADDITIONAL_TYPE_UP_RCMD(
        @SerialName("rcmd")
        override val content: String,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_UGC")
    data class Season(
        @SerialName("ugc")
        override val content: DynamicSeason,
    ) : DynamicAdditional()

    @Serializable
    @SerialName("ADDITIONAL_TYPE_RESERVE")
    data class Reserve(
        @SerialName("reserve")
        override val content: DynamicReserve,
    ) : DynamicAdditional()
}