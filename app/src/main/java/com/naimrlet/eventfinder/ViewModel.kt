package com.naimrlet.eventfinder

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventViewModel : ViewModel() {
    private val database =
        FirebaseDatabase.getInstance().getReference("events") // Firebase reference
    private val _events =
        MutableStateFlow<List<Event>>(emptyList()) // StateFlow for events list
    val events: StateFlow<List<Event>> get() = _events

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventList =
                    mutableListOf<Event>() // Parse snapshot into Event objects list.
                for (eventSnapshot in snapshot.children) {
                    val event =
                        eventSnapshot.getValue(Event::class.java) // Deserialize Firebase data.
                    if (event != null) eventList.add(event)
                }
                _events.value =
                    eventList // Update state with the fetched list of events.
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addEvent(event: Event) {
        database.push().setValue(event).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Handle failure (e.g., log error or show message)
            }
        }
    }
}
