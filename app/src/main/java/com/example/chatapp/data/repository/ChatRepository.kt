package com.example.chatapp.data.repository

import android.util.Log
import com.example.chatapp.data.api.NotificationRequest
import com.example.chatapp.data.api.RetrofitInstance
import com.example.chatapp.data.model.ChatRoom
import com.example.chatapp.data.model.Message
import com.example.chatapp.data.model.UserData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface ChatRepository {
    suspend fun fetchAndCreateChatRoom(userUID: String, partnerUID: String): Flow<ChatRoom>
    suspend fun getPartnerInfo(partnerId: String): UserData
    suspend fun sendMessage(chatRoomId: String, message: Message)
}

class ChatRepositoryImpl @Inject constructor() : ChatRepository {
    private val notificationService = RetrofitInstance.notificationService

    override suspend fun fetchAndCreateChatRoom(userUID: String, partnerUID: String): Flow<ChatRoom> = callbackFlow {
        val firestore = FirebaseFirestore.getInstance()

        // Step 1: Query chatrooms that contain userUID
        val chatRoomsQuery = firestore.collection("ChatRoom")
            .whereArrayContains("members", userUID)
            .get()
            .await()

        // Step 2: Find chatroom that contains both userUID and partnerUID
        val existingChatRoomDoc = chatRoomsQuery.documents.find { document ->
            val members = document.get("members") as? List<*>
            members?.containsAll(listOf(userUID, partnerUID)) ?: false
        }

        // Step 3: Listen to messages in chatroom
        if (existingChatRoomDoc != null) {
            val chatRoomId = existingChatRoomDoc.id

            // Listen to messages in chatroom
            val listenerRegistration = firestore.collection("ChatRoom")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("sentAt")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val messages = snapshot.documents.mapNotNull { doc ->
                            doc.toObject<Message>()
                        }
                        trySend(ChatRoom(id = chatRoomId, messages = messages))
                    }
                }

            // Close listener when flow is cancelled
            awaitClose { listenerRegistration.remove() }
        } else {
            // Step 4: Create new chatroom if not found
            val newChatRoomRef = firestore.collection("ChatRoom").document()
            val newChatRoom = ChatRoom(id = newChatRoomRef.id, messages = emptyList())

            // Add chatroom to Firestore
            newChatRoomRef.set(
                mapOf("members" to listOf(userUID, partnerUID))
            ).await()

            // Send new chatroom to flow
            trySend(newChatRoom)
        }
    }

    override suspend fun getPartnerInfo(partnerId: String): UserData = suspendCancellableCoroutine { continuation ->
        val db = Firebase.firestore

        db.collection("users")
            .whereEqualTo("UID", partnerId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot != null && !snapshot.isEmpty) {
                        val userData = snapshot.documents.firstOrNull()?.toObject(UserData::class.java)
                        if (userData != null) {
                            continuation.resume(userData)
                        } else {
                            continuation.resumeWithException(Exception("User data not found"))
                        }
                    } else {
                        continuation.resumeWithException(Exception("User not found"))
                    }
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Error fetching user"))
                }
            }
    }

    override suspend fun sendMessage(chatRoomId: String, message: Message) {
        val firestore = FirebaseFirestore.getInstance()

        // Create message data
        val messageData = mapOf(
            "content" to message.content,
            "sender" to message.sender,
            "sentAt" to FieldValue.serverTimestamp()
        )

        // Add message to chatroom
        firestore.collection("ChatRoom")
            .document(chatRoomId)
            .collection("messages")
            .add(messageData)
            .await()

        val members: List<String> = getMembersInChatRoom(chatRoomId)
        for (member in members) {
            if (member != message.sender) {
                val token = getFCMToken(member)
                if (token != null) {
                    sendNotification(token, "New message from Chat App", message.content)
                }
            }
        }
    }

    private suspend fun getFCMToken(id: String): String? {
        val db = Firebase.firestore

        return suspendCoroutine { continuation ->
            db.collection("users")
                .whereEqualTo("UID", id)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        continuation.resumeWith(Result.success(null))
                    } else {
                        val user = documents.firstOrNull()
                        val fcmToken = user?.getString("FCMToken")

                        continuation.resumeWith(Result.success(fcmToken))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("aaa", e.message.toString())
                    continuation.resumeWith(Result.success(null))
                }
        }
    }

    private suspend fun getMembersInChatRoom(chatRoomId: String): List<String> {
        val db = Firebase.firestore

        return suspendCoroutine { continuation ->
            db.collection("ChatRoom")
                .document(chatRoomId)
                .get()
                .addOnSuccessListener { document ->
                    val members = document.get("members") as? List<*>
                    if (members != null) {
                        val memberList = members.filterIsInstance<String>()
                        continuation.resumeWith(Result.success(memberList))
                    } else {
                        continuation.resumeWith(Result.success(emptyList()))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWith(Result.failure(e))
                }
        }
    }


    private fun sendNotification(token: String, title: String, body: String) {
        val request = NotificationRequest(
            token = token,
            title = title,
            body = body
        )

        notificationService.sendNotification(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("aaa", "Notification sent successfully!")
                } else {
                    Log.d("aaa","Failed to send notification: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("aaa","onFailure: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}
