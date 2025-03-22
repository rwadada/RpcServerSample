package com.rwadada

import com.rwadada.model.ChatService
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

fun main(args: Array<String>) = runBlocking {
    val message = args.getOrNull(0) ?: return@runBlocking
    val ktorClient = HttpClient {
        installKrpc {
            waitForServices = true
        }
    }

    val client: KtorRpcClient = ktorClient.rpc {
        url {
            host = "localhost"
            port = 8080
            encodedPath = "message"
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }

    val chatService: ChatService = client.withService<ChatService>()
    chatService.sendMessage("Client", message)

    ktorClient.close()
}