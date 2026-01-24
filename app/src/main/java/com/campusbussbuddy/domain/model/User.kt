package com.campusbussbuddy.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val collegeId: String,
    val role: UserRole
)

enum class UserRole {
    STUDENT,
    DRIVER
}