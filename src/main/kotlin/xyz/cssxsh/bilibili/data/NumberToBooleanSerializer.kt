package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

internal object NumberToBooleanSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeInt(if (value) 1 else 0)

    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeInt() != 0
}