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
	fun post(notes: List<Note>){
	}
	fun delete(notes: List<Note>){ noteDao.deleteAll(notes) }

	fun findSince(): MutableList<Note> = noteDao.findSince(1, 0)
//	fun testSaveAllLatest(notes: List<Note>){noteDao.saveAllLatest(notes)}

	fun syncAppData(lastSync: Long, syncData: AppDataBundle): AppDataBundle{

		syncData.notes?.let{ notes ->
			notes.forEach{
				val oldNote = noteDao.findById(it.getPk())
				if(!oldNote.isPresent || (oldNote.isPresent && oldNote.get().updated_at < it.updated_at)){
					noteDao.save(it)
				}
			}
		}
		syncData.folders?.let{  folders ->
			folders.forEach{
				val oldFolder = folderDao.findById(it.getPk())
				if(!oldFolder.isPresent || (oldFolder.isPresent && oldFolder.get().updated_at < it.updated_at)){
					folderDao.save(it)
				}
			}
		}
		syncData.tags?.let{ tags ->
			tags.forEach{
				val oldTag = tagDao.findById(it.getPk())
				if(!oldTag.isPresent || (oldTag.isPresent && oldTag.get().updated_at < it.updated_at)){
					tagDao.save(it)
				}
			}
		}
		syncData.refs?.let{  refs ->
			refs.forEach{
				val oldRef = refDao.findById(it.getPk())
				if(!oldRef.isPresent || (oldRef.isPresent && oldRef.get().updated_at < it.updated_at)){
					refDao.save(it)
				}
			}
		}

		val updatedNotes: MutableList<Note> = noteDao.findSince(syncData.user_id, lastSync)
		val updatedFolders: MutableList<Folder> = folderDao.findSince(syncData.user_id, lastSync)
		val updatedTags: MutableList<Tag> = tagDao.findSince(syncData.user_id, lastSync)
		val updatedRefs: MutableList<TagNoteCrossRef> = refDao.findSince(syncData.user_id, lastSync)

		return AppDataBundle(syncData.user_id, updatedNotes, updatedFolders, updatedTags, updatedRefs)
	}
}

interface NoteRepository: AppRepository<Note, UserSpecificPK>
interface TagRepository: AppRepository<Tag, UserSpecificPK>
interface FolderRepository: AppRepository<Folder, UserSpecificPK>
interface RefRepository: AppRepository<TagNoteCrossRef, UserSpecificCrossRefPK>

@NoRepositoryBean
interface AppRepository<T: BaseTable,ID>: JpaRepository<T,ID> {
	@Query("select t from #{#entityName} t where t.user_id = ?1 and t.updated_at > ?2")
	fun findSince(user_id: Long, timestamp: Long): MutableList<T>
}

data class AppDataBundle(
	val user_id: Long,
	val notes: List<Note>?,
	val folders: List<Folder>?,
	val tags: List<Tag>?,
	val refs: List<TagNoteCrossRef>?
)