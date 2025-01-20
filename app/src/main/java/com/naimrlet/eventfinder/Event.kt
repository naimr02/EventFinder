data class Event(
    val username: String = "",
    val facultyName: String = "",
    val eventName: String = "",
    val locationTime: String = "",
    val description: String = "",
    var id: String? = null, // Firebase key for deletion purposes
)
