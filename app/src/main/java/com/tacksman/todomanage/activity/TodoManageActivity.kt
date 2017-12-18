package com.tacksman.todomanage.activity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.tacksman.todomanage.R
import com.tacksman.todomanage.entity.Todo
import com.tacksman.todomanage.model.TodoListManageModel
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.view_todo_list_item.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class TodoManageActivity : AppCompatActivity(), PositiveButtonClickListener {

    companion object {
        val KEY_TITLE = ".title"
        val KEY_DESCRIPTION = ".description"
    }

    override fun positiveButtonClicked(title: String, description: String) {
        if(title.isNotEmpty()) {
            createNewTodo(title, description)
        } else {
            Toast.makeText(this, "Title無しで新規作成はできません", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var model: TodoListManageModel

    private val TAG = TodoManageActivity::class.java.simpleName

    val KEY_MODEL = "${TodoManageActivity::class.java.simpleName}.model"

    private lateinit var adapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        if (savedInstanceState != null) {
            this.model = savedInstanceState.getParcelable(KEY_MODEL)
        } else {
            this.model = TodoListManageModel()
        }

        adapter = TodoListAdapter(this)
        rv_todo_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_todo_list.adapter = adapter

        fab.setOnClickListener { view ->
            val fragment = AddNewTodoInfoInputDialogFragment()
            fragment.impl = this
            fragment.show(supportFragmentManager, "new todo dialog")
        }

        fetchTodoList()
    }

    private fun fetchTodoList() {
        launch(UI) {
            var todoList = model.todoList
            async(coroutineContext + CommonPool) {
                try {
                    todoList = model.fetchTodoList()
                } catch (e: Throwable) {
                    Log.w(this@TodoManageActivity::class.java.simpleName, e)
                    throw e
                }
            }.await()

            adapter.todoList = todoList as MutableList<Todo>
            adapter.notifyDataSetChanged()
        }
    }

    private fun createNewTodo(title: String, description: String) {
        launch(UI) {
            async(coroutineContext + CommonPool) {
                try {
                    val dateTime = LocalDateTime.now()
                    model.addTodo(
                            Todo.Builder()
                                    .title(title)
                                    .description(description)
                                    .createdAt(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .build()
                    )
                } catch (e: Throwable) {
                    throw e
                }
            }.await()


            var newTodo: List<Todo> = emptyList()
            try {
                async(coroutineContext + CommonPool) {
                    newTodo = model.fetchTodoList()
                }.await()
            } catch (e: Throwable) {
                throw e
            }

            if (newTodo.isEmpty()) return@launch

            update(newTodo)
        }
    }

    /**
     * addTodo実行後コールしてリストを再描画する
     */
    private fun update(newTodoList: List<Todo>) {
        Log.d("update todo list", newTodoList.toString())
        val todoList = adapter.todoList
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return todoList[oldItemPosition].id == newTodoList[newItemPosition].id
            }

            override fun getOldListSize(): Int {
                return todoList.size
            }

            override fun getNewListSize(): Int {
                return newTodoList.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return todoList[oldItemPosition] == newTodoList[newItemPosition]
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val payload = mutableMapOf<String, Pair<Any?, Any?>>()
                payload["title"] = Pair(todoList[oldItemPosition].title, newTodoList[newItemPosition].title)
                payload["description"] = Pair(todoList[oldItemPosition].description, newTodoList[newItemPosition].description)
                payload["completed"] = Pair(todoList[oldItemPosition].completed, newTodoList[newItemPosition].completed)
                payload["createdAt"] = Pair(todoList[oldItemPosition].createdAt, newTodoList[newItemPosition].createdAt)
                return payload
            }
        }).dispatchUpdatesTo(adapter)

        model.updateTodoList(newTodoList)
    }

    class TodoListAdapter(context: Context) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

        var todoList = mutableListOf<Todo>()

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun onBindViewHolder(holder: TodoViewHolder?, position: Int) {
            holder?.bind(todoList[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TodoViewHolder {
            val view = inflater.inflate(R.layout.view_todo_list_item, parent, false)
            return TodoViewHolder(view)
        }

        override fun getItemCount(): Int {
            return todoList.size
        }

        class TodoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            fun bind(todo: Todo) {
                itemView.title.text = todo.title
                itemView.description.text = todo.description
                itemView.completed.visibility = if (todo.completed) View.VISIBLE else View.GONE
            }
        }
    }

    class AddNewTodoInfoInputDialogFragment : DialogFragment() {

        companion object {
            val ADD_TODO = 102
        }

        var title: String = ""
        var description: String = ""

        var impl: PositiveButtonClickListener? = null

        private lateinit var dialog: AlertDialog

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.dialog_new_todo_info_input, null, false)

            dialog = AlertDialog.Builder(activity)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        impl?.positiveButtonClicked(title, description)
                    })
                    .setNegativeButton(android.R.string.cancel, { _, _ -> })
                    .create()

            view.findViewById<EditText>(R.id.et_title)?.addTextChangedListener(
                    object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            title = s.toString()
                        }
                    })

            view.findViewById<EditText>(R.id.et_description)?.addTextChangedListener(
                    object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            description = s.toString()
                        }
                    })
            return dialog
        }
    }
}

interface PositiveButtonClickListener {
    fun positiveButtonClicked(title: String, description: String)
}
