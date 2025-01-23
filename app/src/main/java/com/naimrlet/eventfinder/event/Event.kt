package com.naimrlet.eventfinder.event

data class Event(
    val username: String = "",
    val facultyName: String = "",
    val eventName: String = "",
    val location: String = "",
    val description: String = "",
    var id: String? = null, // Firebase key for deletion purposes
)
