package com.github.jetbrains.timetracker.androidapp.data.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.github.jetbrains.timetracker.androidapp.auth.AuthenticationProvider
import com.github.jetbrains.timetracker.androidapp.data.SlicesRepository
import com.github.jetbrains.timetracker.androidapp.model.*
import com.github.jetbrains.timetracker.androidapp.util.snapshots
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.inject
import java.util.*

class FirestoreSlicesRepository : SlicesRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val authenticationProvider by inject<AuthenticationProvider>()

    // TODO Store timestamps and reduce the number of operations
    // https://medium.com/firebase-tips-tricks/how-to-drastically-reduce-the-number-of-reads-when-no-documents-are-changed-in-firestore-8760e2f25e9e

    override fun observeFinishedSlices(): Flow<List<WorkSlice>> {
        return withFinishedSlices {
            snapshots.map { querySnapshot ->
                querySnapshot
                    .documents
                    .mapNotNull { it.toObject<FirestoreWorkSlice>()?.toWorkSlice() }
            }
        } ?: emptyFlow()
    }

    override fun observeWorkActivitiesSuggestions(): Flow<WorkActivitySuggestions> {
        return observeFinishedSlices().map { it.buildActivitySuggestions() }
    }

    override suspend fun getFinishedSlice(id: UUID): Result<WorkSlice?> =
        Result.success(withFinishedSlices {
            document(id.toString()).get().await().toObject<FirestoreWorkSlice>()?.toWorkSlice()
        })

    override suspend fun updateFinishedSlice(id: UUID, sliceChanges: SliceChanges) {
        withFinishedSlices {
            document(id.toString()).update(sliceChanges.buildDetailsUpdatesMap()).await()
        }
    }

    override fun observeRunningSlice(): Flow<RunningSlice?> = withRunningSlice {
        snapshots.map { snapshot ->
            snapshot
                .takeIf { it.exists() }
                ?.toObject<FirestoreRunningSlice>()
                ?.toRunningSlice()
        }
    } ?: emptyFlow()

    override suspend fun startRunningSlice(description: Description, task: Task?, project: Project?) {
        val runningSlice = RunningSlice(
            activity = WorkActivity(
                project = project,
                task = task,
                description = description,
            )
        )
        updateRunningSlice(runningSlice)
    }

    private inline fun <T> withRunningSlice(action: DocumentReference.() -> T): T? =
        runCatching {
            val currentUser = authenticationProvider.currentUser ?: return null
            val runningSliceDocument = db
                .collection("slices")
                .document(currentUser.uid)
            runningSliceDocument.action()
        }.onFailure { throwable ->
            Log.w(TAG, "Error while working with running slice data", throwable)
        }.getOrNull()

    private inline fun <T> withFinishedSlices(action: CollectionReference.() -> T): T? =
        runCatching {
            val currentUser = authenticationProvider.currentUser ?: return null
            val finishedSlicesCollection = db
                .collection("slices")
                .document(currentUser.uid)
                .collection("finished")
            finishedSlicesCollection.action()
        }.onFailure { throwable ->
            Log.w(TAG, "Error while working with finished slices data", throwable)
        }.getOrNull()

    private suspend fun updateRunningSlice(runningSlice: RunningSlice) = withRunningSlice {
        set(runningSlice.toFirestoreRunningSlice()).await()
    }

    private suspend fun updateRunningSlice(changesMap: Map<String, Any>) = withRunningSlice {
        update(changesMap).await()
    }

    override suspend fun updateRunningSlice(sliceChanges: SliceChanges) {
        updateRunningSlice(sliceChanges.buildDetailsUpdatesMap())
    }

    override suspend fun changeRunningSliceState(state: WorkSlice.State) {
        if (state == WorkSlice.State.FINISHED) return finishRunningSlice()

        withRunningSlice {
            val runningSlice = this.get()
                .await()
                ?.toObject<FirestoreRunningSlice>()
                ?.toRunningSlice()
                ?: return
            val newSlice = if (state == WorkSlice.State.PAUSED)
                runningSlice.pause()
            else
                runningSlice.resume()
            updateRunningSlice(newSlice.buildStateUpdatesMap())
        }
    }

    override suspend fun finishRunningSlice() {
        withRunningSlice {
            val runningSlice = get()
                .await()
                ?.toObject<FirestoreRunningSlice>()
                ?.toRunningSlice()
                ?: return
            coroutineScope {
                launch {
                    withRunningSlice {
                        delete().await()
                    }
                }
                val workSlice = runningSlice.finish().toFirestoreWorkSlice()
                withFinishedSlices {
                    document(workSlice.id!!)
                        .set(workSlice)
                        .await()
                }
            }
        }
    }
}