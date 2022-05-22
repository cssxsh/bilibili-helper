package xyz.cssxsh.mirai.bilibili.command

import net.mamoe.mirai.console.command.*

sealed interface BiliHelperCommand : Command {

    companion object : Collection<BiliHelperCommand> {
        private val commands by lazy {
            BiliHelperCommand::class.sealedSubclasses.mapNotNull { kClass -> kClass.objectInstance }
        }

        override val size: Int get() = commands.size

        override fun contains(element: BiliHelperCommand): Boolean = commands.contains(element)

        override fun containsAll(elements: Collection<BiliHelperCommand>): Boolean = commands.containsAll(elements)

        override fun isEmpty(): Boolean = commands.isEmpty()

        override fun iterator(): Iterator<BiliHelperCommand> = commands.iterator()

        operator fun get(name: String): BiliHelperCommand = commands.first { it.primaryName.equals(name, true) }
    }
}