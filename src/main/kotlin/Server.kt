package com.rwadada

import com.rwadada.model.ChatSystem
import com.rwadada.model.Message
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlin.coroutines.CoroutineContext

object DataStore {
    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages = _messages.asStateFlow()

    fun setMessage(message: Message) {
        _messages.update { it + message }
    }
}

class ChatSystemImpl(
    override val coroutineContext: CoroutineContext
) : ChatSystem {
    override suspend fun sendMessage(clientId: String, text: String) {
        DataStore.setMessage(Message(clientId, text))
    }

    override suspend fun viewMessages(): Flow<List<Message>> {
        return DataStore.messages
    }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
        println("Server running")
    }.start(wait = true)
}

fun Application.module() {
    install(Krpc)

    routing {
        rpc("/message") {
            rpcConfig {
                serialization {
                    json()
                }
            }

            registerService<ChatSystem> { ctx -> ChatSystemImpl(ctx) }
        }
    }
}