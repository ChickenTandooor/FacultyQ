package com.facultyq.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PATCH


interface FacultyQApi {

    @GET("authorities")
    suspend fun getAuthorities(): AuthorityResponse

    @GET("authorities/{authority_id}")
    suspend fun getAuthority(
        @Path("authority_id") authorityId: String
    ): AuthorityDto

    @GET("students/{enrollment_number}")
    suspend fun getStudent(
        @Path("enrollment_number") enrollmentNumber: String
    ): StudentDto

    @GET("students/{enrollment_number}/queue")
    suspend fun getStudentQueueHistory(
        @Path("enrollment_number") enrollmentNumber: String
    ): StudentQueueHistoryResponse

    @POST("students")
    suspend fun addStudent(
        @Body request: AddStudentRequest
    ): StudentResponse

    @GET("authorities/{authority_id}/queue")
    suspend fun getAuthorityQueue(
        @Path("authority_id") authorityId: String
    ): AuthorityQueueResponse


    @GET("authorities/{authority_id}/history")
    suspend fun getAuthorityHistory(
        @Path("authority_id") authorityId: String
    ): AuthorityHistoryResponse


    @POST("authorities/{authority_id}/queue")
    suspend fun joinAuthorityQueue(
        @Path("authority_id") authorityId: String,
        @Body request: JoinAuthorityQueueRequest
    ): JoinAuthorityQueueResponse

    @POST("queue/{queue_id}/leave")
    suspend fun leaveQueue(
        @Path("queue_id") queueId: String
    ): QueueActionResponse

    @POST("queue/{queue_id}/serve")
    suspend fun serveQueue(
        @Path("queue_id") queueId: String
    ): QueueActionResponse

    @POST("queue/{queue_id}/complete")
    suspend fun completeQueue(
        @Path("queue_id") queueId: String
    ): QueueActionResponse


    @PATCH("authorities/{authority_id}/status")
    suspend fun updateAuthorityStatus(
        @Path("authority_id") authorityId: String,
        @Body request: AuthorityStatusUpdateRequest
    ): AuthorityDto

}