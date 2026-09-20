package com.facultyq.app.network

data class AuthorityResponse(
    val count: Int,
    val authorities: List<AuthorityDto>
)

data class AuthorityDto(
    val id: String,
    val name: String,
    val role: String,
    val department: String,
    val cabin: String,
    val status: String,
    val queue_capacity: Int,
    val is_available_today: Boolean
)