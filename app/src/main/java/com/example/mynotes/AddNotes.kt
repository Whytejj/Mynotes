package com.example.mynotes

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_notes.*

class AddNotes : AppCompatActivity() {
    var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        var dbManager = DbManager(this)

        //need to put this in try catch bc an intent will not always be provided
        try {
            var bundle:Bundle = intent.extras!!
            id = bundle.getInt(dbManager.colID, 0)
            if(id != 0) {
                id = bundle.getInt(dbManager.colID)
                etTitle.setText(bundle.getString(dbManager.colTitle))
                etContent.setText(bundle.getString(dbManager.colContent))
            }
        }catch (ex:Exception){}

        /*0 is the default value for if no ID was found...meaning when id!= 0, this activity
          was launched by pressing buEdit button and the user wants to update a preexisting note*/

    }

    fun buAdd(view:View){
        var dbManager = DbManager(this)
        var values = ContentValues()

        if(id == 0) {//not updating data
            values.put(dbManager.colTitle, etTitle.text.toString())
            values.put(dbManager.colContent, etContent.text.toString())

            //insert data
            var insertData = dbManager.Insert(values)


            if (insertData > 0) {
                Toast.makeText(this, "note added", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "note not added", Toast.LENGTH_LONG).show()
            }
        }else{//updating data
            var selectionArgs = arrayOf(id.toString())
            values.put(dbManager.colTitle, etTitle.text.toString())
            values.put(dbManager.colContent, etContent.text.toString())
            val insertData = dbManager.update(values, "${dbManager.colID}=?", selectionArgs)
        }
        finish()
    }
}