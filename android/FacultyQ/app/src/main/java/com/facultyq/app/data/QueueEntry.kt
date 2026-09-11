package com.facultyq.app.data

data class QueueEntry(
    val id: String,
    val studentEnrollmentNumber: String,

    // ID of the faculty or authority being visited
    val targetId: String,

    // Identifies whether the target is a teaching faculty or higher authority
    val targetType: QueueTargetType,

    val purpose: String,
    val studentCurrentClass: String,
    val position: Int,
    val status: QueueEntryStatus = QueueEntryStatus.WAITING
)

enum class QueueTargetType {
    FACULTY,
    AUTHORITY
}

enum class QueueEntryStatus {
    WAITING,
    SERVING,
    COMPLETED,
    LEFT
}