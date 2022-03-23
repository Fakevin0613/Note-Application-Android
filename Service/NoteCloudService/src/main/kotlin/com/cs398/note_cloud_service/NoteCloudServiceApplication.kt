package com.cs398.note_cloud_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp

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
	fun sync(@RequestParam lastSync: Long, @RequestBody syncData: AppDataBundle): AppDataBundle{
		return noteService.syncAppData(lastSync, syncData)
	}

	@GetMapping
	fun index(): List <Note>{
//		return noteService.findNotes()
		return noteService.findSince()
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
	val refDao: RefRepository
){
	fun findNotes(): MutableList<Note> = noteDao.findAll()
	fun post(notes: List<Note>){ noteDao.saveAll(notes)	}
	fun delete(notes: List<Note>){ noteDao.deleteAll(notes) }

	fun findSince(): MutableList<Note> = noteDao.findSince(1, 1)

	fun syncAppData(lastSync: Long, syncData: AppDataBundle): AppDataBundle{

		val updatedNotes: MutableList<Note> = noteDao.findSince(syncData.userId, lastSync)
		val updatedFolders: MutableList<Folder> = folderDao.findSince(syncData.userId, lastSync)
		val updatedTags: MutableList<Tag> = tagDao.findSince(syncData.userId, lastSync)
		val updatedRefs: MutableList<TagNoteCrossRef> = refDao.findSince(syncData.userId, lastSync)

		syncData.notes?.let{ noteDao.saveAll(it) }
		syncData.folders?.let{ folderDao.saveAll(it) }
		syncData.tags?.let{ tagDao.saveAll(it) }
		syncData.refs?.let{ refDao.saveAll(it) }

		return AppDataBundle(syncData.userId, updatedNotes, updatedFolders, updatedTags, updatedRefs)
	}
}

interface NoteRepository: AppRepository<Note, UserSpecificPK> {}
interface TagRepository: AppRepository<Tag, UserSpecificPK> {}
interface FolderRepository: AppRepository<Folder, UserSpecificPK> {}
interface RefRepository: AppRepository<TagNoteCrossRef, UserSpecificCrossRefPK> {}

@NoRepositoryBean
interface AppRepository<T,ID>: JpaRepository<T,ID>{
	@Query("select t from #{#entityName} t where t.user_id = ?1 and t.updated_at > ?2")
	fun findSince(userId: Long, timestamp: Long): MutableList<T>

	// instead of using this, make a SQL trigger that prevents update with less UPDATED_AT time stam.
//	@Query(
//		""
//	)
//	fun saveLatest()
//
//	fun saveAllLatest(li: List<T>){
//		li.forEach{
//			it.
//		}
//	}
}

data class AppDataBundle(
	val userId: Long,
	val notes: List<Note>?,
	val folders: List<Folder>?,
	val tags: List<Tag>?,
	val refs: List<TagNoteCrossRef>?
)