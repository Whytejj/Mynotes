package com.example.mynotes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import kotlin.reflect.KTypeProjection

class DbManager {
    val dbName = "MyNotes"
    val dbTable = "Notes"
    val colID = "ID"
    val  colTitle = "Title"
    val colContent = "Content"
    val dbVersion = 1

    //"CREATE TABLE IF NOT EXISTS MyNotes (ID INTEGER PRIMARY KEY, Title TEXT, Content TEXT);"
    val sqlCreateTable = "CREATE TABLE IF NOT EXISTS $dbTable" +
            "($colID INTEGER PRIMARY KEY, $colTitle TEXT, $colContent TEXT);"

    var sqlDB:SQLiteDatabase? = null

    constructor(context: Context){
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes:SQLiteOpenHelper{
        var context:Context? = null
        constructor(context: Context):super(context,dbName, null, dbVersion){
            this.context = context
        }
        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "db created", Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL("DROP TABLE IF EXISTS $dbTable")
        }

    }

    fun Insert(values:ContentValues):Long{
        val insertData = sqlDB!!.insert(dbTable,"", values)
        return insertData
    }

    //projection is columns, selection is rows, cursor is like a table
    fun Query(projection: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder:String):Cursor{
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable

        val cursor = qb.query(sqlDB, projection, selection, selectionArgs,null, null, sortOrder)

        return cursor
    }

    fun Delete(selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.delete(dbTable, selection, selectionArgs)
        return count
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTable, values, selection, selectionArgs)
        return count
    }


}