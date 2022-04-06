package com.github.jetbrains.timetracker.androidapp.util

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

val DocumentReference.snapshots: Flow<DocumentSnapshot>
    get() = callbackFlow {
        val listener = addSnapshotListener { snapshot, exception ->
            if (snapshot != null) trySend(snapshot)
            if (exception != null) close(exception)
        }
        awaitClose { listener.remove() }
    }

val Query.snapshots: Flow<QuerySnapshot>
    get() = callbackFlow {
    val listener = addSnapshotListener { snapshot, exception ->
        snapshot?.let { trySend(snapshot) }
        exception?.let { close(exception) }
    }
    awaitClose { listener.remove() }
}