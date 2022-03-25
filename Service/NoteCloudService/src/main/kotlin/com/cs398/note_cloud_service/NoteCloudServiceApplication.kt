package com.cs398.note_cloud_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.sql.SQLException
import java.util.*

@SpringBootApplication()
class NoteCloudServiceApplication

fun main(args: Array<String>) {
	runApplication<NoteCloudServiceApplication>(*args)
}

@RestController
class NoteResource(val noteService: NoteService){

	/**
	 * Receives local changes and returns updates since lastSync
	 * @param lastSync 	timestamp of last sync time with local device
	 * @param syncData 	object containing database changes since lastSync
	 */
	@RequestMapping(value = ["/sync"], method = [RequestMethod.POST])
	fun sync(@RequestBody syncData: AppDataBundle): AppDataBundle{
		return noteService.syncAppData(syncData)
	}

	/**
	 * wipes out entire database.
	 */
	@RequestMapping(value = ["/fresh_start"], method = [RequestMethod.DELETE])
	fun clear(){
		return noteService.clearAll()
	}

	@GetMapping
	fun index(): List <Note>{
		return noteService.findNotes()
	}

	@PostMapping
	fun post(@RequestBody notes: List<Note>){
		noteService.post(notes)
	}

	@DeleteMapping
	fun delete(@RequestBody notes: List<Note>){
		noteService.delete(notes)
	}
}

@Service
class NoteService(
	val noteDao: NoteRepository,
	val folderDao: FolderRepository,
	val tagDao: TagRepository,
	val refDao: RefRepository,
	val delDao: DLRepository
){
	fun findNotes(): MutableList<Note> = noteDao.findAll()
	fun post(notes: List<Note>){
		noteDao.saveAll(notes)
	}
	fun delete(notes: List<Note>){ noteDao.deleteAll(notes) }

	fun clearAll(){
		noteDao.deleteAll()
		folderDao.deleteAll()
		tagDao.deleteAll()
		refDao.deleteAll()
		delDao.deleteAll()
	}

	val tables = arrayOf("Note", "Folder", "Tag", "TagNoteCrossRef")
	fun syncAppData(syncData: AppDataBundle): AppDataBundle{
		syncData.folders?.forEach{
			it.user_id = syncData.user_id
			val oldFolder = folderDao.findById(it.getPk())

			// update/insert data
			if(needSave(oldFolder as Optional<BaseTable>, it)){
				// if new note with same pk
				if(needNew(oldFolder as Optional<BaseTable>, it)) {
					// this is a new data. create a new instance and update
					// notes and tag-not refs accordingly
					val newId = (folderDao.getTopId(syncData.user_id)?:0) + 1
					syncData.notes?.forEachIndexed { index, note ->
						if (note.folder_id == it.id) syncData.notes[index].folder_id = newId
					}
					syncData.deletes?.forEachIndexed { index, del ->
						if (del.table_name==tables[1] && del.id_primary == it.id) syncData.deletes[index].id_primary = newId
					}
					it.id = newId
				}

				// it.updated_at = Date().time
				folderDao.save(it)
			}
		}
		syncData.tags?.forEach{
			it.user_id = syncData.user_id
			val oldTag = tagDao.findById(it.getPk())

			// update/insert data
			if(needSave(oldTag as Optional<BaseTable>, it)){
				// if new note with same pk
				if(needNew(oldTag as Optional<BaseTable>, it)) {
					// this is a new data. create a new instance and update
					// notes and tag-not refs accordingly
					val newId = (tagDao.getTopId(syncData.user_id)?:0) + 1
					syncData.refs?.forEachIndexed { index, ref ->
						if (ref.tag_id == it.id) syncData.refs[index].tag_id = newId
					}
					syncData.deletes?.forEachIndexed { index, del ->
						if (del.table_name==tables[2] && del.id_primary == it.id) syncData.deletes[index].id_primary = newId
					}
					it.id = newId
				}

				// it.updated_at = Date().time
				tagDao.save(it)
			}
		}
		syncData.notes?.forEach{
			it.user_id = syncData.user_id
			val oldNote = noteDao.findById(it.getPk())

			// update/insert data
			if(needSave(oldNote as Optional<BaseTable>, it)){
				// if new note with same pk
				if(needNew(oldNote as Optional<BaseTable>, it)) {
					// this is a new data. create a new instance and update
					// notes and tag-not refs accordingly
					val newId = (noteDao.getTopId(syncData.user_id)?:0) + 1
					syncData.refs?.forEachIndexed { index, ref ->
						if (ref.note_id == it.id) syncData.refs[index].note_id = newId
					}
					syncData.deletes?.forEachIndexed { index, del ->
						if (del.table_name==tables[0] && del.id_primary == it.id) syncData.deletes[index].id_primary = newId
					}
					it.id = newId
				}

				// it.updated_at = Date().time
				noteDao.save(it)
			}
		}
		syncData.refs?.let{  refs ->
			refs.forEach{
				it.user_id = syncData.user_id
				// it.updated_at = Date().time
			}
			refDao.saveAll(refs)
		}

		// delete stuff
		syncData.deletes?.forEach{
			it.user_id = syncData.user_id
			it.id = (delDao.getTopId(syncData.user_id)?:0) + 1
			when(it.table_name){
				tables[0] -> {
					val oldItem = noteDao.findById(UserSpecificPK(syncData.user_id, it.id_primary))
					if(oldItem.isPresent && oldItem.get().updated_at < it.deleted_at){
						// it's updated before deleted. so delete anyway
						noteDao.delete(oldItem.get())
						// it.deleted_at = Date().time
						delDao.save(it)
					}
				}
				tables[1] -> {
					val oldItem = folderDao.findById(UserSpecificPK(syncData.user_id, it.id_primary))
					if(oldItem.isPresent && oldItem.get().updated_at < it.deleted_at){
						// it's updated before deleted. so delete anyway
						folderDao.delete(oldItem.get())
						// it.deleted_at = Date().time
						delDao.save(it)
					}
				}
				tables[2] -> {
					val oldItem = tagDao.findById(UserSpecificPK(syncData.user_id, it.id_primary))
					if(oldItem.isPresent && oldItem.get().updated_at < it.deleted_at){
						// it's updated before deleted. so delete anyway
						tagDao.delete(oldItem.get())
						// it.deleted_at = Date().time
						delDao.save(it)
					}
				}
			}
		}

		return AppDataBundle(
			syncData.user_id,
			syncData.last_sync,
			noteDao.findSince(syncData.user_id, syncData.last_sync),
			folderDao.findSince(syncData.user_id, syncData.last_sync),
			tagDao.findSince(syncData.user_id, syncData.last_sync),
			refDao.findSince(syncData.user_id, syncData.last_sync),
			delDao.findSince(syncData.user_id, syncData.last_sync)
		)
	}

	private fun needSave(old: Optional<BaseTable>, new: BaseTable): Boolean =
		(!old.isPresent || new.created_at != old.get().created_at || new.updated_at > old.get().updated_at)
	private fun needNew(old: Optional<BaseTable>, new: BaseTable): Boolean =
		old.isPresent && new.created_at != old.get().created_at
}

interface NoteRepository: AppRepository<Note, UserSpecificPK>
interface TagRepository: AppRepository<Tag, UserSpecificPK>
interface FolderRepository: AppRepository<Folder, UserSpecificPK>
interface RefRepository: AppRepository<TagNoteCrossRef, UserSpecificCrossRefPK>
interface DLRepository: JpaRepository<DeleteLog, UserSpecificCrossRefPK>{
	@Query("select t from #{#entityName} t where t.user_id = ?1 and t.deleted_at > ?2")
	fun findSince(user_id: Long, timestamp: Long): MutableList<DeleteLog>

	@Query("select t.id from #{#entityName} t where t.user_id = ?1 order by id desc limit 1", nativeQuery = true)
	fun getTopId(user_id: Long): Long?
}

@NoRepositoryBean
interface AppRepository<T: BaseTable,ID>: JpaRepository<T,ID> {
	@Query("select t from #{#entityName} t where t.user_id = ?1 and t.updated_at > ?2")
	fun findSince(user_id: Long, timestamp: Long): MutableList<T>

	@Query("select t.id from #{#entityName} t where t.user_id = ?1 order by id desc limit 1", nativeQuery = true)
	fun getTopId(user_id: Long): Long?
}

data class AppDataBundle(
	val user_id: Long,
	val last_sync: Long,
	val notes: List<Note>?,
	val folders: List<Folder>?,
	val tags: List<Tag>?,
	val refs: List<TagNoteCrossRef>?,
	val deletes: List<DeleteLog>?
)