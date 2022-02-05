package com.example.mynotes

open class Note {
    var noteId:Int? = null
    var noteTitle:String? = null
    var noteContent:String? = null

    constructor(noteId:Int, noteTitle:String, noteContent:String){
        this.noteId = noteId
        this.noteTitle = noteTitle
        this.noteContent = noteContent
    }
}