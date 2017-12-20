package com.tacksman.todomanage.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.tacksman.todomanage.R
import com.tacksman.todomanage.databinding.ActivityTodoDetailBinding
import com.tacksman.todomanage.entity.Todo
import com.tacksman.todomanage.model.TodoDetailModel
import kotlinx.android.synthetic.main.activity_todo_detail.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class TodoDetailActivity: AppCompatActivity(), TodoDetailModel.OnStatusUpdateListener {
    private val KEY_TODO = "${TodoDetailActivity::class.java}.todo"

    private val KEY_MODEL = "${TodoDetailActivity::class.java}.model"

    fun createIntent(context: Context, todo: Todo): Intent {
        val intent = Intent(context, TodoDetailActivity::class.java)
        intent.putExtra(KEY_TODO, todo)
        return intent
    }

    private lateinit var todo: Todo
    private lateinit var model: TodoDetailModel
    private lateinit var binding: ActivityTodoDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_detail)

        if (savedInstanceState == null) {
            if (intent.hasExtra(KEY_TODO)) {
                todo = intent.getParcelableExtra(KEY_TODO)
            }

            model = TodoDetailModel(todo)
        } else {
            todo = savedInstanceState.getParcelable(KEY_TODO)
            model = savedInstanceState.getParcelable(KEY_MODEL)
        }

        model.init(this)
        binding.model = model

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

    }

    override fun onResume() {
        super.onResume()

        if (this::todo.isInitialized) {
            et_title.setText(todo.title)

            if (todo.completed) iv_completed.setImageResource(R.drawable.ic_check_circle_green_24dp)
            else iv_completed.setImageResource(R.drawable.ic_brightness_1_grey_48dp)

            et_description.setText(todo.description)

            iv_completed.setOnClickListener {
                if (todo.completed) iv_completed.setImageResource(R.drawable.ic_brightness_1_grey_48dp)
                else iv_completed.setImageResource(R.drawable.ic_check_circle_green_24dp)
                model.changeCompletedState(!todo.completed)
            }

            ll_save.setOnClickListener {
                onClickSave()
            }
        }

        changeEditMode(false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelable(KEY_TODO, todo)
        outState?.putParcelable(KEY_MODEL, model)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.todo_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false

        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_OK)
            finish()
            return true
        } else if (item.itemId == R.id.start_edit) {
            changeEditMode(true)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun update() {
        ll_save.isClickable = !TextUtils.isEmpty(model.title)
    }

    private fun changeEditMode(editable: Boolean) {
        Log.d("CHECK", "${editable}")
        iv_completed.isClickable = editable

        et_title.isFocusable = editable
        et_title.isFocusableInTouchMode = editable
        et_title.isLongClickable = editable

        et_description.isFocusable = editable
        et_description.isFocusableInTouchMode = editable
        et_description.isLongClickable = editable

        if (editable) {
            ll_save.visibility = View.VISIBLE
        } else {
            ll_save.visibility = View.GONE
        }
    }

    fun onClickSave() {
        launch(UI) {
            try {
                async(coroutineContext + CommonPool) {
                    model.update()
                }.await()
            } catch (e: Throwable) {
                Log.w("CHECK", e)
                Toast.makeText(this@TodoDetailActivity, "Failed to edit TODO!", Toast.LENGTH_SHORT).show()
            }
        }
        changeEditMode(false)
    }
}