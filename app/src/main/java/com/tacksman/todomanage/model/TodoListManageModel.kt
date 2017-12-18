package com.tacksman.todomanage.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.tacksman.todomanage.entity.Todo
import com.tacksman.todomanage.infrastructure.TodoManageRepository

class TodoListManageModel() : Parcelable {

    private val repository = TodoManageRepository()

    var todoList = emptyList<Todo>()

    constructor(parcel: Parcel) : this() {
        todoList = parcel.createTypedArrayList(Todo)
    }

    fun updateTodoList(newTodoList: List<Todo>) {

    }

    suspend fun fetchTodoList(): List<Todo> {
        val fetchedTodoList = repository.fetchList()
        return fetchedTodoList
    }

    suspend fun addTodo(todo: Todo): String {
        return repository.add(todo)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(todoList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TodoListManageModel> {
        override fun createFromParcel(parcel: Parcel): TodoListManageModel {
            return TodoListManageModel(parcel)
        }

        override fun newArray(size: Int): Array<TodoListManageModel?> {
            return arrayOfNulls(size)
        }
    }

}