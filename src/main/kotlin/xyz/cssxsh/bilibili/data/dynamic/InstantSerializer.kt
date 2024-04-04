package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.*

@PublishedApi
internal object InstantSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.epochSecond)

    override fun deserialize(decoder: Decoder): Instant = Instant.ofEpochSecond(decoder.decodeLong())
}