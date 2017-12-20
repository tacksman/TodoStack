package com.tacksman.todomanage.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tacksman.todomanage.R
import com.tacksman.todomanage.entity.Todo
import kotlinx.android.synthetic.main.activity_todo_detail.*

class TodoDetailActivity: AppCompatActivity() {

    val KEY_TODO = "${TodoDetailActivity::class.java}.todo"

    fun createIntent(context: Context, todo: Todo): Intent {
        val intent = Intent(context, TodoDetailActivity::class.java)
        intent.putExtra(KEY_TODO, todo)
        return intent
    }

    lateinit var todo: Todo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_todo_detail)

        if (savedInstanceState == null) {
            if (intent.hasExtra(KEY_TODO)) {
                todo = intent.getParcelableExtra(KEY_TODO)
            }
        } else {
            todo = savedInstanceState.getParcelable(KEY_TODO)
        }

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

    }

    override fun onResume() {
        super.onResume()

        if (this::todo.isInitialized) {
            tv_title.text = todo.title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false

        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}