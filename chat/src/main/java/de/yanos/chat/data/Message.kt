package de.yanos.chat.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = false) val id: String = "",
    val chatId: String = "",
    val creatorId: String = "",
    val text: String? = null,
    @Embedded(prefix = "media_") val media: Media? = null,
    val refMsgId: String? = null,
    val state: Map<String, MessageState> = mapOf(),
    val reactions: Map<String, List<String>> = mapOf(),
    val ts: Long = 0L,
)

data class Media(
    val id: String,
    val name: String,
    val mimeType: String,
    val path: String,
    val size: Long
)

enum class MessageState {
    SENT, DELIVERED, READ, DELETED
}