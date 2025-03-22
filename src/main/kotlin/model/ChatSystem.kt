package com.rwadada.model

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable

@Rpc
interface ChatSystem : RemoteService {
    suspend fun sendMessage(clientId: String, text: String)
    suspend fun getAllMessage(): List<Message>
    suspend fun observeMessage(): Flow<Message>
}

@Serializable
data class Message(val clientId: String, val messageText: String)