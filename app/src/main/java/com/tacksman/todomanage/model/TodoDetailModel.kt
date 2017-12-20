package com.tacksman.todomanage.model

import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import com.tacksman.todomanage.entity.Todo
import com.tacksman.todomanage.infrastructure.TodoManageRepository

class TodoDetailModel(val todo: Todo): Parcelable {

    var title = todo.title
    var description = todo.description
    var completed = todo.completed

    lateinit var listener: OnStatusUpdateListener

    fun init(impl: OnStatusUpdateListener) {
        listener = impl
    }

    fun editTitle(editable: Editable) {
        title = editable.toString()
        listener.update()
    }

    fun editDescription(editable: Editable) {
        description = editable.toString()
        listener.update()
    }

    suspend fun update() {
        TodoManageRepository().edit(Todo.Builder()
                .init(todo)
                .title(title)
                .description(description)
                .completed(completed)
                .build())
    }

    fun changeCompletedState(completed: Boolean) {
        this.completed = completed
    }

    interface OnStatusUpdateListener {
        fun update()
    }

    constructor(parcel: Parcel) : this(parcel.readParcelable<Todo>(Todo::class.java.classLoader)) {
        title = parcel.readString()
        description = parcel.readString()
        completed = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(todo, flags)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeByte(if (completed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TodoDetailModel> {
        override fun createFromParcel(parcel: Parcel): TodoDetailModel {
            return TodoDetailModel(parcel)
        }

        override fun newArray(size: Int): Array<TodoDetailModel?> {
            return arrayOfNulls(size)
        }
    }
}