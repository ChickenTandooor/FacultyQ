package com.facultyq.app.network

data class AddStudentRequest(
    val enrollment_number: String,
    val name: String,
    val current_class: String
)

data class StudentResponse(
    val message: String,
    val student: StudentDto
)

data class StudentDto(
    val enrollment_number: String,
    val name: String,
    val current_class: String
)

data class JoinAuthorityQueueRequest(
    val student_enrollment_number: String,
    val purpose: String,
    val student_current_class: String
)

data class QueueEntryDto(
    val id: String,
    val student_enrollment_number: String,
    val target_id: String,
    val target_type: String,
    val purpose: String,
    val student_current_class: String,
    val position: Int,
    val status: String
)

data class JoinAuthorityQueueResponse(
    val message: String,
    val queue_entry: QueueEntryDto
)

data class AuthorityQueueResponse(
    val authority_id: String,
    val count: Int,
    val queue: List<QueueEntryDto>
)

data class QueueActionResponse(
    val message: String
)