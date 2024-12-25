package com.example.chatapp.data.repository

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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface ChatRepository {
    suspend fun fetchAndCreateChatRoom(userUID: String, partnerUID: String): Flow<ChatRoom>
    suspend fun getPartnerInfo(partnerId: String): UserData
    suspend fun sendMessage(chatRoomId: String, message: Message)
}

class ChatRepositoryImpl @Inject constructor() : ChatRepository {
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
    }

}
