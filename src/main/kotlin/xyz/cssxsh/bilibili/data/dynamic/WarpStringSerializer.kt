package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@PublishedApi
internal class WarpStringSerializer<T>(private val original: KSerializer<T>) : KSerializer<T> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        val json = (encoder as JsonEncoder).json
        val text = json.encodeToString(serializer = original, value = value)
        encoder.encodeString(value = text)
    }

    override fun deserialize(decoder: Decoder): T {
        val json = (decoder as JsonDecoder).json
        val text = decoder.decodeString()
        return json.decodeFromString(deserializer = original, string = text)
    }
}