package com.codebrew.clikat.app_utils


import android.util.Log
import com.codebrew.clikat.BuildConfig
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

import timber.log.Timber


class SocketManager(userId:String, secret:String) {
    companion object {
        private const val SOCKET_URL_CLIENT = BuildConfig.BASE_URL

        private const val EVENT_CONNECT = "socketConnected"

        //ORDER SOCKET
        const val EMIT_SEND_MESSAGE = "sendMessage"
        const val ON_RECEIVE_MESSAGE = "receiveMessage"
        const val ON_ERROR_MESSAGE = "parameterError"
        const val ON_LOCATION_UPDATE = "user_update_location"
        const val JOIN_ROOM = "join_room"
        const val SHARE_LOCATION ="user_update_location"

        private var INSTANCE: SocketManager? = null

        fun getInstance(userId:String,secretDBKey: String) = INSTANCE ?: synchronized(SocketManager::class.java) {
            INSTANCE ?: SocketManager(userId,secretDBKey)
                    .also {
                        INSTANCE = it
                    }
        }

        /**
         * Disconnects from current instance and also releases references to it
         * so that a new instance will be created next time.
         * */
        fun destroy() {
            Timber.d("Destroying socket instance")
            INSTANCE?.disconnect()
            INSTANCE = null
        }
    }

    private val options by lazy {
        IO.Options().apply {
            reconnection = true
            forceNew = true
            /*multiplex =false*/
            query = "id=$userId&secretdbkey=$secret&userType=2"
            //"id=test&secretdbkey=test&userType=2"
            Log.e("query",query.toString())
        }
    }

    private val socket by lazy { IO.socket(SOCKET_URL_CLIENT, options) }

    fun connect(errorListener: Emitter.Listener) {
        if (socket.connected()) {
            Timber.d("Socket is already connected")
            return
        }

        if (!socket.hasListeners(EVENT_CONNECT)) {
            socket.on(EVENT_CONNECT) {
                Timber.d("Socket Connect${socket.id()}")
            socket.on(Socket.EVENT_DISCONNECT) {
                Log.e("Socket Conne disconnect", "disconnect")
                Timber.d("Socket Disconnect")
            }

            }
            socket.on(Socket.EVENT_CONNECT_TIMEOUT) { args ->
                Log.e("Socket Connect timeout",args.toString())
                Timber.w("Socket Connect timeout : ${args.firstOrNull()}")
            }
            socket.on(Socket.EVENT_ERROR,errorListener)
            socket.on(Socket.EVENT_CONNECT_ERROR) {
                args ->
                Log.e("Socket Connect error",args.firstOrNull().toString())
                Timber.w("Socket connect error : ${args.firstOrNull()}")
            }
        }

        socket.connect()
    }

    fun disconnect() {
        socket.off(Socket.EVENT_DISCONNECT)
        socket.off(Socket.EVENT_CONNECT_TIMEOUT)
        socket.off(Socket.EVENT_ERROR)
        socket.off(Socket.EVENT_CONNECT_ERROR)
        socket.disconnect()
        Timber.d("Disconnect")
    }

    fun checkConnection():Boolean
    {
        return socket.connected()
    }
    fun on(event: String, listener: Emitter.Listener) {
        socket.on(event, listener)
    }

    fun off(event: String, listener: Emitter.Listener) {
        socket.off(event, listener)
    }

    fun onErrorEvent()
    {
        if (socket != null) {
            socket.on(ON_ERROR_MESSAGE) {
                    args -> println("ERROR_MESSAGE ${args?.firstOrNull()}")
            }
        }
    }

    fun emit(event: String, args: Any, acknowledge: Ack) {
        if (socket.connected()) {
            socket.emit(event, args, acknowledge)
        }
    }
}