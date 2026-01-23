package com.campusbussbuddy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.campusbussbuddy.domain.model.User
import com.campusbussbuddy.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val role: String
)

fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        email = email,
        name = name,
        role = UserRole.valueOf(role)
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        name = name,
        role = role.name
    )
}