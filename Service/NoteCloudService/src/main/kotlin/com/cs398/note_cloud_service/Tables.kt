package com.cs398.note_cloud_service

import javax.persistence.*

@Entity
@IdClass(UserSpecificPK::class)
data class Note(
    @Id
    @Column(nullable = false)
    val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val local_id: Long? = null,
    val title: String = "",
    val content: String = "",
    val notify: Boolean = false,
    val notify_at: Long? = null,
    val folder_id: Long? = null,
    val created_at: Long = 0,
    val updated_at: Long = 0
)

@Entity
@IdClass(UserSpecificPK::class)
data class Folder(
    @Id
    @Column(nullable = false)
    val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val local_id: Long? = null,
    val name: String = "",
    val parent: Long? = null,
    val created_at: Long = 0,
    val updated_at: Long = 0
)

@Entity
@IdClass(UserSpecificPK::class)
data class Tag(
    @Id
    @Column(nullable = false)
    val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val local_id: Long? = null,
    val name: String = "",
    val created_at: Long = 0,
    val updated_at: Long = 0
)

class UserSpecificPK(private val user_id: Long = 0, private val local_id: Long = 0): java.io.Serializable

@Entity
@IdClass(UserSpecificCrossRefPK::class)
data class TagNoteCrossRef(
    @Id
    @Column(nullable = false)
    val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val tag_id: Long? = null,
    val note_id: Long? = null,
    val name: String = "",
    val created_at: Long = 0,
    val updated_at: Long = 0
)

class UserSpecificCrossRefPK(private val user_id: Long = 0, private val tag_id: Long = 0, private val note_id: Long = 0): java.io.Serializable