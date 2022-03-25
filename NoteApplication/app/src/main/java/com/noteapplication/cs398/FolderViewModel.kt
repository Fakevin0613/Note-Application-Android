package com.noteapplication.cs398

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.noteapplication.cs398.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.KClass

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    val allFolders: LiveData<List<Folder>>

    var isAddingFolder: Boolean = false
    private val dao: NoteDataAccess
    private val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    init {
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allFolders = dao.getFolders()
    }

    fun deleteFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(folder)
    }

    fun updateFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(folder)

        // need some way to detect deleted tags
//        tags?.let {_ ->
//            tags.forEach {
//                dao.insert(it)
//                dao.insert(TagNoteCrossRef(it.id, note.id))
//            }
//        }
    }

    fun insertFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(folder)
    }

    fun syncData() = viewModelScope.launch(Dispatchers.IO) {
        val queue = Volley.newRequestQueue(getApplication())
        val url = "http://10.0.2.2:8080/sync"
        val lastSync = 1L

        val obj = JSONObject()
        try{
            obj.put("user_id", 1)
            obj.put("last_sync", lastSync)
            obj.put("notes", JSONArray(gson.toJson(dao.getNoteSince(lastSync))))
            obj.put("folders", JSONArray(gson.toJson(dao.getFolderSince(lastSync))))
            obj.put("tags", JSONArray(gson.toJson(dao.getTagSince(lastSync))))
            obj.put("refs", JSONArray(gson.toJson(dao.getRefSince(lastSync))))
            obj.put("deletes", JSONArray(gson.toJson(dao.getDelSince(lastSync))))
        }catch (e: JSONException){
            error(e)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, obj,
            { result ->
                viewModelScope.launch(Dispatchers.IO) {
                    (result["folders"] as JSONArray?)?.let {
                        for (i in (0 until it.length())) {
                            dao.insert(gson.fromJson(it[i].toString(), Folder::class.java))
                        }
                    }
                    (result["tags"] as JSONArray?)?.let {
                        for (i in (0 until it.length())) {
                            dao.insert(gson.fromJson(it[i].toString(), Tag::class.java))
                        }
                    }
                    (result["notes"] as JSONArray?)?.let {
                        for (i in (0 until it.length())) {
                            dao.insert(gson.fromJson(it[i].toString(), Note::class.java))
                        }
                    }
                    (result["refs"] as JSONArray?)?.let {
                        for (i in (0 until it.length())) {
                            dao.insert(gson.fromJson(it[i].toString(), TagNoteCrossRef::class.java))
                        }
                    }
                    (result["deletes"] as JSONArray?)?.let {
                        for (i in (0 until it.length())) {
                            val del = gson.fromJson(it[i].toString(), DeleteLog::class.java)
                            when(del.tableName){
                                "Note" -> dao.deleteNoteById(del.idPrimary)
                                "Folder" -> dao.deleteFolderById(del.idPrimary)
                                "Tag" -> dao.deleteTagById(del.idPrimary)
                                "TagNotCrossRef" -> dao.deleteRefById(del.idPrimary, del.idSecondary!!)
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "Sync complete", Toast.LENGTH_SHORT).show()
                    }
                }
            },{
                viewModelScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "Sync failed: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        withContext(Dispatchers.Main) {
            Toast.makeText(getApplication(), "Sync start", Toast.LENGTH_SHORT).show()
        }
        queue.add(jsonObjectRequest)
    }
}