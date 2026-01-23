package com.campusbussbuddy.domain.model

data class User(
    val id: String,
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.STUDENT
)

enum class UserRole {
    STUDENT,
    DRIVER
}