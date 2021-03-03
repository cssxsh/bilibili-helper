package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LongRangeSerializer : KSerializer<LongRange> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor(LongRange::class.qualifiedName!!, StructureKind.LIST)

    override fun deserialize(decoder: Decoder): LongRange =
        decoder.decodeSerializableValue(LongArraySerializer()).let { (first, last) -> first..last }

    override fun serialize(encoder: Encoder, value: LongRange) =
        encoder.encodeSerializableValue(LongArraySerializer(), listOf(value.first, value.last).toLongArray())
}