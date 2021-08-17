package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializer(forClass = Boolean::class)
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
internal object NumberToBooleanSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor(Boolean::class.qualifiedName!!, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeInt(if (value) 1 else 0)

    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeInt() != 0
}