package com.cs398.NoteCloudService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@SpringBootApplication()
class NoteCloudServiceApplication

fun main(args: Array<String>) {
	runApplication<NoteCloudServiceApplication>(*args)
}

@RestController
class NoteResource(val service: NoteService){

	@GetMapping
	fun index(): List <Note> = service.findNotes()

	@PostMapping
	fun post(@RequestBody note: Note){
		service.post(note)
	}
}

@Service
class NoteService(val dao: NoteRepository){
	fun findNotes() = dao.findNotes()
	fun post(note: Note){
		dao.save(note)
	}
}

@Table("Note")
data class Note(
	val userId: Long,
	val id: Long,
	val title: String,
	val content: String,
	val notify: Boolean,
	val folderId: Long?,
	val createdTime: Long,
	val updatedTime: Long
)

@Table("Folder")
data class Folder(
	val userId: Long,
	val id: Long,
	val name: String,
	val parent: Long?,
	val createdTime: Long,
	val updatedTime: Long
)

@Table("Tag")
data class Tag(
	val userId: Long,
	val id: Long,
	val name: String,
	val createdTime: Long,
	val updatedTime: Long
)

@Table("TagNoteCrossRef")
data class TagNoteCrossRef(
	val userId: Long,
	val tagId: Long,
	val noteId: Long,
	val name: String,
	val createdTime: Long,
	val updatedTime: Long
)

interface NoteRepository: CrudRepository<Note, Long>{
	@Query("select * from Note")
	fun findNotes(): List<Note>
}