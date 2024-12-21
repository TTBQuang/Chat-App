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

        // Bước 1: Truy vấn các chatrooms có userUID trong members
        val chatRoomsQuery = firestore.collection("ChatRoom")
            .whereArrayContains("members", userUID)
            .get()
            .await()

        // Bước 2: Lọc chatroom có cả userUID và partnerUID
        val existingChatRoomDoc = chatRoomsQuery.documents.find { document ->
            val members = document.get("members") as? List<*>
            members?.containsAll(listOf(userUID, partnerUID)) ?: false
        }

        // Nếu chatroom đã tồn tại
        if (existingChatRoomDoc != null) {
            val chatRoomId = existingChatRoomDoc.id

            // Lắng nghe sự thay đổi trong sub-collection messages
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

            // Lắng nghe kết thúc
            awaitClose { listenerRegistration.remove() }
        } else {
            // Nếu không tìm thấy, tạo chatroom mới
            val newChatRoomRef = firestore.collection("ChatRoom").document()
            val newChatRoom = ChatRoom(id = newChatRoomRef.id, messages = emptyList())

            // Thêm document mới vào Firestore
            newChatRoomRef.set(
                mapOf("members" to listOf(userUID, partnerUID))
            ).await()

            // Gửi trả về chatroom mới
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

        // Tạo thông tin message cần gửi, bao gồm cả thời gian gửi (sentAt)
        val messageData = mapOf(
            "content" to message.content,
            "sender" to message.sender, // Nếu bạn lưu trữ thông tin người gửi dưới dạng UID, thì chỉ cần UID
            "sentAt" to FieldValue.serverTimestamp() // Lưu thời gian gửi
        )

        // Thêm message vào sub-collection "messages" của ChatRoom
        firestore.collection("ChatRoom")
            .document(chatRoomId)
            .collection("messages")
            .add(messageData)
            .await() // Đảm bảo thực thi bất đồng bộ và chờ kết quả

        // Bạn có thể làm thêm các xử lý khác như thông báo thành công hay cập nhật trạng thái UI
    }

}
