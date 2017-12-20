package com.tacksman.todomanage.infrastructure

import com.tacksman.todomanage.entity.Todo
import retrofit2.Call
import retrofit2.http.*

interface TodoManageService {

    @GET("/todos")
    fun fetchTodoList(): Call<List<Todo>>

    @GET("/todos/{id}")
    fun fetchTodo(@Query("id") id: String): Call<Todo>

    @POST("/todos")
    fun addTodo(
            @Body todo: Todo
    ): Call<String>

    @PUT("/todos/{id}")
    fun update(
            @Path("id") id: String,
            @Body todo: Todo
    ): Call<String>

    @DELETE("/todos/{id}")
    fun delete(id: String): Call<Boolean>

}