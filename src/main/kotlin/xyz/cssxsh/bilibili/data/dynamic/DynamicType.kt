package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable(with = DynamicType.Serializer::class)
enum class DynamicType(val id: Int) {
    NONE(id = 0),
    REPLY(id = 1),
    PICTURE(id = 2),
    TEXT(id = 4),
    VIDEO(id = 8),
    ARTICLE(id = 64),
    MUSIC(id = 256),
    EPISODE(id = 512),
    DELETE(id = 1024),
    SKETCH(id = 2048),
    LIVE(id = 4200),
    LIVE_END(id = 4308);

    companion object Serializer : KSerializer<DynamicType> {
        override val descriptor: SerialDescriptor
            get() = buildSerialDescriptor("BiliCardTypeSerializer", SerialKind.ENUM)

        override fun serialize(encoder: Encoder, value: DynamicType) = encoder.encodeInt(value.id)

        override fun deserialize(decoder: Decoder): DynamicType = decoder.decodeInt().let { index ->
            requireNotNull(values().find { it.id == index }) { "$index not in ${values().toList()}" }
        }
    }
}