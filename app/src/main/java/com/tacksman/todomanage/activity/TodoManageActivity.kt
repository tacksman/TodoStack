package com.tacksman.todomanage.activity

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tacksman.todomanage.R
import com.tacksman.todomanage.databinding.ActivityTodoBinding
import com.tacksman.todomanage.entity.Todo
import com.tacksman.todomanage.model.TodoListManageModel
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_todo_activity.*
import kotlinx.android.synthetic.main.view_todo_list_item.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class TodoManageActivity : AppCompatActivity() {

    private lateinit var model: TodoListManageModel

    private val KEY_MODEL = "${TodoManageActivity::class.java.simpleName}.model"

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
        rv_todo_list.adapter = adapter
        rv_todo_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        fab.setOnClickListener { view ->

        }

        fetchTodoList()
    }

    fun fetchTodoList() {
        launch(UI) {
            async(coroutineContext + CommonPool) {
                val todoList = model.fetchTodoList()
                adapter.update(todoList)
            }
        }
    }

    class TodoListAdapter(context: Context): RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

        val todoList = mutableListOf<Todo>()

        val inflater: LayoutInflater = LayoutInflater.from(context)

        fun update(newTodoList: List<Todo>) {
            DiffUtil.calculateDiff(object: DiffUtil.Callback() {
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
                    return payload
                }
            }).dispatchUpdatesTo(this)
        }

        override fun onBindViewHolder(holder: TodoViewHolder?, position: Int) {
            holder?.bind(todoList[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TodoViewHolder {
            val view = inflater.inflate(R.layout.view_todo_list_item, parent)
            return TodoViewHolder(view)
        }

        override fun getItemCount(): Int {
            return todoList.size
        }

        class TodoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            fun bind(todo: Todo) {
                itemView.title.text = todo.title
                itemView.completed.visibility = if (todo.completed) View.VISIBLE else View.GONE
            }
        }
    }
}
