package com.tacksman.todomanage.infrastructure

import com.tacksman.todomanage.entity.Todo
import retrofit2.Call

class TodoManageDao(val api: TodoManageApi): TodoManageService {

    override fun fetchTodoList(): Call<List<Todo>> {
        return api.retrofit().create(TodoManageService::class.java).fetchTodoList()
    }

    override fun fetchTodo(id: String): Call<Todo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addTodo(todo: Todo): Call<String> {
        return api.retrofit().create(TodoManageService::class.java).addTodo(todo)
    }

    override fun update(id: String, todo: Todo): Call<String> {
        return api.retrofit().create(TodoManageService::class.java).update(id, todo)
    }

    override fun delete(id: String): Call<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}