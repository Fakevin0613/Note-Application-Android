package com.cs398.note_cloud_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
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
	fun findNotes(): MutableList<Note> = dao.findAll()
	fun post(note: Note){
		dao.save(note)
	}
}

interface NoteRepository: JpaRepository<Note, UserSpecificPK> {
//	@Query("select * from Note")
//	fun findNotes(): List<Note>

//	fun findAll(): List<Note>
}