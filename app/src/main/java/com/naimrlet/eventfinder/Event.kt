package com.naimrlet.eventfinder

data class Event(
    val username: String = "",
    val facultyName: String = "",
    val eventName: String = "",
    val locationTime: String = "",
    val description: String = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "", "")
}