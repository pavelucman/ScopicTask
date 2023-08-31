package com.pavel.scopictask.data.repositories

import com.pavel.scopictask.data.network.model.ListItem
import com.pavel.scopictask.data.network.model.Response
import com.pavel.scopictask.data.network.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.pavel.scopictask.util.Constants.Companion.ITEMS
import com.pavel.scopictask.util.Constants.Companion.USERS
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListItemRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val defaultDispatcher: CoroutineDispatcher
) {

    fun getItems() = callbackFlow {
        val userId = auth.currentUser!!.uid
        val userItems = db.collection(ITEMS).whereEqualTo("createdBy.userId", userId)
        val snapshotListener = userItems.addSnapshotListener { snapshot, e ->
            val itemResponse = if (snapshot != null) {
                val items = snapshot.toObjects(ListItem::class.java)
                Response.Success(items)
            } else {
                Response.Error(e)
            }
            trySend(itemResponse)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    suspend fun insertItem(itemName: String) {
        val userId = auth.currentUser!!.uid
        val itemId = db.collection(ITEMS).document().id
        val user = db.collection(USERS).document(userId)
        val itemDocument = db.collection(ITEMS).document(itemId)

        withContext(defaultDispatcher) {
            val currentUser = user.get().await().toObject<User>()
            val item = ListItem(itemId, itemName, currentUser!!)
            itemDocument.set(item).await()
        }
    }

    suspend fun updateListItem(item: ListItem) {
        val itemDocument = db.collection(ITEMS).document(item.id)
        withContext(defaultDispatcher) {
            itemDocument.update(
                mapOf(
                    "name" to item.name,
                    "done" to item.done
                )
            ).await()
        }
    }

    suspend fun deleteItem(itemId: String) {
        val itemDocument = db.collection(ITEMS).document(itemId)
        withContext(defaultDispatcher) {
            itemDocument.delete().await()
        }
    }
}