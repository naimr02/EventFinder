package com.naimrlet.eventfinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("events")
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> get() = _events

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventList = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                }
                _events.value = eventList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
