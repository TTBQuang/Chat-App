package com.example.chatapp.data.repository

import com.example.chatapp.data.model.UserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface HomeRepository {
    fun getAllUsers(): Flow<List<UserData>>
    fun findUsersByUsername(query: String): Flow<List<UserData>>
}

class FirebaseHomeRepository @Inject constructor() : HomeRepository {
    override fun getAllUsers(): Flow<List<UserData>> = callbackFlow {
        val db = Firebase.firestore
        val listenerRegistration = db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val usersList = snapshot.documents.mapNotNull { document ->
                        UserData(
                            UID = document.getString("UID"),
                            username = document.getString("username"),
                            profilePictureUrl = document.getString("profilePictureUrl")
                        )
                    }
                    trySend(usersList)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun findUsersByUsername(query: String): Flow<List<UserData>> = callbackFlow {
        val db = Firebase.firestore
        val listenerRegistration = db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val filteredUsers = snapshot.documents.mapNotNull { document ->
                        val userData = UserData(
                            UID = document.getString("UID"),
                            username = document.getString("username"),
                            profilePictureUrl = document.getString("profilePictureUrl")
                        )
                        if (isUsernameContainQuery(userData.username ?: "", query)) {
                            userData
                        } else null
                    }
                    trySend(filteredUsers)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    private fun isUsernameContainQuery(username: String, query: String): Boolean {
        return username.contains(query, ignoreCase = true)
    }
}
