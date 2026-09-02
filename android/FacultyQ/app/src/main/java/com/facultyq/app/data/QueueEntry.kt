package com.facultyq.app.data

data class QueueEntry(
    val id: String,
    val studentEnrollmentNumber: String,
    val facultyId: String,
    val purpose: String,
    val studentCurrentClass: String,
    val position: Int,
    val status: QueueEntryStatus = QueueEntryStatus.WAITING
)

enum class QueueEntryStatus {
    WAITING,
    SERVING,
    COMPLETED,
    LEFT
}