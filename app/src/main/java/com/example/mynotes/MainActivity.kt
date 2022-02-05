package com.example.mynotes

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.view.*

class MainActivity : AppCompatActivity() {
    var listOfNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //add dummy data
        listOfNotes.add(Note(1, "Meet professor", "Meet professor tommorow at 8 for the assignment"))
        listOfNotes.add(Note(2, "Meet professor", "Meet professor tommorow at 8 for the assignment"))
        listOfNotes.add(Note(3, "Meet professor", "Meet professor tommorow at 8 for the assignment"))

        //load from database; "%" is for all titles
        loadQuery("%")
    }

    override fun onResume() {
        super.onResume()

        loadQuery("%")
    }

    fun loadQuery(title:String){
        var dbManager = DbManager(this)
        val projection = arrayOf(dbManager.colID, dbManager.colTitle, dbManager.colContent)
        val selectionArgs = arrayOf(title)
        //need all columns so projection *; select "Title like [selectArgs... meaning <title>]"; sort by "Title"
        //ex: val selectionArgs = arrayOf(title, content); cursor = dbManager.Query(projection, selection: title like ? and content like ?, sortOrder: "Title")
        //Select projection Where Title like % Sort By Title
        val cursor = dbManager.Query(projection, "${dbManager.colTitle} like ?", selectionArgs, dbManager.colTitle)
        listOfNotes.clear()
        if(cursor.moveToFirst()){
            do {
                val ID = cursor.getInt(cursor.getColumnIndex(dbManager.colID))
                val Title = cursor.getString(cursor.getColumnIndex(dbManager.colTitle))
                val Content = cursor.getString(cursor.getColumnIndex(dbManager.colContent))

                listOfNotes.add(Note(ID, Title, Content))

            }while (cursor.moveToNext())
        }

        var myNotesAdapter = MyNotesAdapter(this, listOfNotes)
        lvNotes.adapter = myNotesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        val sv = menu!!.findItem(R.id.search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Toast.makeText(applicationContext, p0, Toast.LENGTH_LONG).show()
                //load notes that has [any text in front] p0 [any text behind]
                loadQuery("%$p0%")
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //load notes that has [any text in front] p0 [any text behind]
                loadQuery("%$p0%")
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addNote->{
                //go to add note page
                var addNote_intent = Intent(this,AddNotes::class.java)
                startActivity(addNote_intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter:BaseAdapter{
        var listOfNotes_Adapter = ArrayList<Note>()
        var context:Context? = null
        constructor(context: Context,listOfNote:ArrayList<Note>):super(){
            this.listOfNotes_Adapter = listOfNotes
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.ticket,null)
            var myNote = listOfNotes_Adapter[p0]
            myView.tvTitle.text = myNote.noteTitle
            myView.tvContent.text = myNote.noteContent

            myView.buDelete.setOnClickListener(View.OnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArg = arrayOf(myNote.noteId.toString())
                dbManager.Delete("${dbManager.colID}=?",selectionArg)
                loadQuery("%")
            })
            myView.buEdit.setOnClickListener(View.OnClickListener {
                goToUpdate(myNote)
            })

            return myView
        }

        override fun getItem(p0: Int): Any {
            return listOfNotes_Adapter[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listOfNotes_Adapter.size
        }


    }

    fun goToUpdate(note:Note){
        var dbManager = DbManager(this)
        var editNote_intent = Intent(this,AddNotes::class.java)
        editNote_intent.putExtra(dbManager.colID, note.noteId)
        editNote_intent.putExtra(dbManager.colTitle, note.noteTitle)
        editNote_intent.putExtra(dbManager.colContent, note.noteContent)
        startActivity(editNote_intent)
    }
}