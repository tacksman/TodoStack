package com.tacksman.todomanage.infrastructure

import com.tacksman.todomanage.entity.Todo
import kotlinx.coroutines.experimental.async
import ru.gildor.coroutines.retrofit.await

class TodoManageRepository {

    suspend fun fetchList(): List<Todo> = async {
        TodoManageDao(TodoManageApi()).fetchTodoList().await()
    }.await()

}