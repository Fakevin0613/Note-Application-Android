package com.cs398.note_cloud_service

import javax.persistence.*

@Entity
@IdClass(UserSpecificPK::class)
data class Note(
    @Id
    @Column(nullable = false)
    override val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val local_id: Long? = null,
    val title: String = "",
    val content: String = "",
    val notify: Boolean = false,
    val notify_at: Long? = null,
    val folder_id: Long? = null,
    override val created_at: Long = 0,
    override val updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificPK(user_id!!, local_id!!)
}

@Entity
@IdClass(UserSpecificPK::class)
data class Folder(
    @Id
    @Column(nullable = false)
    override val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val local_id: Long? = null,
    val name: String = "",
    val parent: Long? = null,
    override val created_at: Long = 0,
    override val updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificPK(user_id!!, local_id!!)
}

@Entity
@IdClass(UserSpecificPK::class)
data class Tag(
    @Id
    @Column(nullable = false)
    override val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val local_id: Long? = null,
    val name: String = "",
    override val created_at: Long = 0,
    override val updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificPK(user_id!!, local_id!!)
}

@Entity
@IdClass(UserSpecificCrossRefPK::class)
data class TagNoteCrossRef(
    @Id
    @Column(nullable = false)
    override val user_id: Long? = null,
    @Id
    @Column(nullable = false)
    val tag_id: Long? = null,
    val note_id: Long? = null,
    val name: String = "",
    override val created_at: Long = 0,
    override val updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificCrossRefPK(user_id!!, tag_id!!, note_id!!)
}

abstract class BaseTable(
    open val user_id: Long?,
    open val created_at: Long,
    open val updated_at: Long
)

class UserSpecificPK(
    override val user_id: Long = 0,
    val local_id: Long = 0
): BasePk(user_id), java.io.Serializable

class UserSpecificCrossRefPK(
    override val user_id: Long = 0,
    val tag_id: Long = 0,
    val note_id: Long = 0
): BasePk(user_id), java.io.Serializable

abstract class BasePk(open val user_id: Long)