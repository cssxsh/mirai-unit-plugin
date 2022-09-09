package xyz.cssxsh.mirai.unit.data

import net.mamoe.mirai.*
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.mapKeys
import net.mamoe.mirai.contact.*

public object UnitBotService : AutoSavePluginConfig("UnitBotService") {
    @ValueName("service")
    @ValueDescription("Unit Bot service")
    public val service: MutableMap<Contact, String> by value<MutableMap<String, String>>()
        .mapKeys(::deserialize, ::serialize)

    internal fun serialize(contact: Contact): String {
        return when (contact) {
            is Group -> "${contact.bot.id}.${contact.id}"
            is Friend -> "${contact.bot.id}.${contact.id}"
            is Member -> "${contact.bot.id}.${contact.group.id}.${contact.id}"
            is Stranger -> "${contact.bot.id}.${contact.id}"
            else -> "${contact.bot.id}.${contact.id}"
        }
    }

    internal fun deserialize(uid: String): Contact {
        val ids = uid.split('.').map { it.toLong() }
        val bot = Bot.getInstance(qq = ids[0])
        return if (ids.size == 3) {
            val (_, group, member) = ids
            bot.getGroupOrFail(id = group).getOrFail(id = member)
        } else {
            val (_, id) = ids
            bot.getGroup(id = id) ?: bot.getFriend(id = id) ?: bot.getStranger(id = id)
            ?: throw NoSuchElementException(uid)
        }
    }
}