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
	val id: Long?,
	val title: String,
	val content: String,
	val notify: Boolean,
	val

)

//`title` TEXT NOT NULL,
//`content` TEXT NOT NULL,
//`notify` INTEGER NOT NULL,
//`folderId` INTEGER,
//`createdTime` DATE NOT NULL,
//`updatedTime` DATE NOT NULL,
//`id` INTEGER PRIMARY KEY NOT NULL,
//FOREIGN KEY(`folderId`) REFERENCES `Folder`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL

//`Folder`
//`name` TEXT NOT NULL,
//`parent` INTEGER,
//`id` INTEGER PRIMARY KEY NOT NULL,
//`createdTime` DATE NOT NULL,
//`updatedTime` DATE NOT NULL,
//FOREIGN KEY(`parent`) REFERENCES `Folder`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE

//tag
//`name` TEXT NOT NULL,
//`createdTime` DATE NOT NULL,
//`updatedTime` DATE NOT NULL,
//`id` INTEGER PRIMARY KEY NOT NULL


//CREATE TABLE IF NOT EXISTS `TagNoteCrossRef`
//(
//`tagId` INTEGER NOT NULL,
//`noteId` INTEGER NOT NULL,
//`createdTime` DATE NOT NULL,
//`updatedTime` DATE NOT NULL,

interface NoteRepository: CrudRepository<Note, String>{
	@Query("select id, title from Note")
	fun findNotes(): List<Note>
}