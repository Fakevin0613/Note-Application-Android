package com.cs398.note_cloud_service

import javax.persistence.*

@Entity
@IdClass(UserSpecificPK::class)
data class Note(
    @Id
    override var user_id: Long? = null,
    @Id
    @Column(nullable = false)
    var id: Long? = null,
    val title: String = "",
    val content: String = "",
    val notify: Boolean = false,
    val notify_at: Long? = null,
    var folder_id: Long? = null,
    override val created_at: Long = 0,
    override var updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificPK(user_id!!, id!!)
}

@Entity
@IdClass(UserSpecificPK::class)
data class Folder(
    @Id
    override var user_id: Long? = null,
    @Id
    @Column(nullable = false)
    var id: Long? = null,
    val name: String = "",
    val parent: Long? = null,
    override val created_at: Long = 0,
    override var updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificPK(user_id!!, id!!)
}

@Entity
@IdClass(UserSpecificPK::class)
data class Tag(
    @Id
    override var user_id: Long? = null,
    @Id
    @Column(nullable = false)
    var id: Long? = null,
    val name: String = "",
    override val created_at: Long = 0,
    override var updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificPK(user_id!!, id!!)
}

@Entity
@IdClass(UserSpecificCrossRefPK::class)
data class TagNoteCrossRef(
    @Id
    override var user_id: Long? = null,
    @Id
    @Column(nullable = false)
    var tag_id: Long? = null,
    var note_id: Long? = null,
    override val created_at: Long = 0,
    override var updated_at: Long = 0
): BaseTable(user_id, created_at, updated_at){
    fun getPk() = UserSpecificCrossRefPK(user_id!!, tag_id!!, note_id!!)
}

@Entity
@IdClass(UserSpecificPK::class)
data class DeleteLog(
    @Id
    var user_id: Long? = null,
    @Id
    @Column(nullable = false)
    var id: Long? = null,
    val table_name: String = "",
    var id_primary: Long = 0,
    var id_secondary: Long? = null,
    var deleted_at: Long = 0,
)

abstract class BaseTable(
    open var user_id: Long?,
    open val created_at: Long,
    open var updated_at: Long
)

class UserSpecificPK(
    override var user_id: Long = 0,
    var id: Long = 0
): BasePk(user_id), java.io.Serializable

class UserSpecificCrossRefPK(
    override var user_id: Long = 0,
    val tag_id: Long = 0,
    val note_id: Long = 0
): BasePk(user_id), java.io.Serializable

abstract class BasePk(open var user_id: Long)