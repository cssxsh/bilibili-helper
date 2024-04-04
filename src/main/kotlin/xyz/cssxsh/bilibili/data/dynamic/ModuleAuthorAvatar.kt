package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleAuthorAvatar(
    @SerialName("container_size")
    val size: SizeSpec,
    @SerialName("mid")
    val mid: String,
    @SerialName("fallback_layers")
    val fallback: LayerGroup = LayerGroup.Empty,
    @SerialName("layers")
    val layers: List<LayerGroup> = emptyList()
) {
    @Serializable
    data class LayerGroup(
        @SerialName("is_critical_group")
        val isCriticalGroup: Boolean = false,
        @SerialName("layers")
        val layers: List<Layer>
    ) {
        companion object {
            val Empty = LayerGroup(
                isCriticalGroup = false,
                layers = emptyList()
            )
        }
    }

    @Serializable
    data class Layer(
        @SerialName("general_spec")
        val generalSpec: GeneralSpec,
        @SerialName("layer_config")
        val config: ConfigGroup,
        @SerialName("resource")
        val resource: WebResource,
        @SerialName("visible")
        val visible: Boolean = false
    )

    @Serializable
    data class GeneralSpec(
        @SerialName("pos_spec")
        val pos: PosSpec = PosSpec.Empty,
        @SerialName("render_spec")
        val render: RenderSpec = RenderSpec.Empty,
        @SerialName("size_spec")
        val size: SizeSpec = SizeSpec.Empty
    )

    @Serializable
    data class PosSpec(
        @SerialName("axis_x")
        val axisX: Double = 0.0,
        @SerialName("axis_y")
        val axisY: Double = 0.0,
        @SerialName("coordinate_pos")
        val coordinatePos: Int = 0
    ) {
        companion object {
            val Empty = PosSpec(axisX = 0.0, axisY = 0.0, coordinatePos = 0)
        }
    }

    @Serializable
    data class RenderSpec(
        @SerialName("opacity")
        val opacity: Int = 0
    ) {
        companion object {
            val Empty = RenderSpec(opacity = 0)
        }
    }

    @Serializable
    data class SizeSpec(
        @SerialName("height")
        val height: Double = 0.0,
        @SerialName("width")
        val width: Double = 0.0
    ) {
        companion object {
            val Empty = SizeSpec(height = 0.0, width = 0.0)
        }
    }

    @Serializable
    data class ConfigGroup(
        @SerialName("is_critical")
        val isCritical: Boolean? = null,
        @SerialName("tags")
        val tags: Map<String, WebConfig> = emptyMap()
    )
}