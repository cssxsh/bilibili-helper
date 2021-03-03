package xyz.cssxsh.bilibili.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Boolean::class)
internal object NumberToBooleanSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor(Boolean::class.qualifiedName!!, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeInt(if (value) 1 else 0)

    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeInt() != 0
}