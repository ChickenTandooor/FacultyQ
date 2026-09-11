package com.facultyq.app.data

data class Authority(
    val id: String,
    val name: String,
    val role: AuthorityRole,
    val department: String,
    val cabin: String,
    val status: FacultyStatus = FacultyStatus.AVAILABLE,
    val queueCapacity: Int = 5,
    val isAvailableToday: Boolean = true
)

enum class AuthorityRole {
    DEAN,
    HOD
}