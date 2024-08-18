package example.com.room

import example.com.data.MessageDataSource
import example.com.data.model.Message
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {

    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        userName: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if(members.containsKey(userName)) {
            throw MemberAlreadyExistsException()
        } else {
            members[userName] = Member(
                userName = userName,
                sessionId = sessionId,
                socket = socket
            )
        }
    }

    suspend fun sendMessage(
        senderUsername: String,
        message: String
    ) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                username = senderUsername,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return  messageDataSource.getAllMessages()
    }

    suspend fun tryDisConnect(userName: String) {
        members[userName]?.socket?.close()
        if(members.containsKey(userName)) {
            members.remove(key = userName)
        }
    }
}