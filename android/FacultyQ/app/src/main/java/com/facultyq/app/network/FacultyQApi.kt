package com.facultyq.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @POST("students")
    suspend fun addStudent(
        @Body request: AddStudentRequest
    ): StudentResponse

    @GET("authorities/{authority_id}/queue")
    suspend fun getAuthorityQueue(
        @Path("authority_id") authorityId: String
    ): AuthorityQueueResponse

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
}