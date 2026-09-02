package com.facultyq.app.data

data class Faculty(
    val id: String,
    val name: String,
    val cabin: String,
    val status: FacultyStatus = FacultyStatus.AVAILABLE,
    val queueCapacity: Int = 5,
    val isAvailableToday: Boolean = true
)

enum class FacultyStatus {
    AVAILABLE,
    BUSY,
    DO_NOT_DISTURB,
    AWAY
}