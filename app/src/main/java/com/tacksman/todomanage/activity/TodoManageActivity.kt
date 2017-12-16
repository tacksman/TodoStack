package com.tacksman.todomanage.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tacksman.todomanage.R
import kotlinx.android.synthetic.main.activity_todo_activity.*

class TodoManageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_activity)

        fab.setOnClickListener { view ->

        }
    }
}
