package com.tacksman.todomanage.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Todo(
        @SerializedName("id") val id: String?,
        @SerializedName("title") var title: String?,
        @SerializedName("description") var description: String?,
        @SerializedName("completed") var completed: Boolean,
        @SerializedName("created_at") val createdAt: String?
): Parcelable {



    class Builder {

        lateinit var id: String
        lateinit var title: String
        lateinit var description: String
        var completed: Boolean = false
        lateinit var createdAt: String

        fun id(id: String): Builder {
            this.id = id
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun createdAt(createdAt: String): Builder {
            this.createdAt = createdAt
            return this
        }

        fun build(): Todo {

            return Todo(id, title, description, completed, createdAt)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString()) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Todo

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (completed != other.completed) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + completed.hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Todo(id=$id, title=$title, description=$description, completed=$completed, createdAt=$createdAt)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeByte(if (completed) 1 else 0)
        parcel.writeString(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Todo> {
        override fun createFromParcel(parcel: Parcel): Todo {
            return Todo(parcel)
        }

        override fun newArray(size: Int): Array<Todo?> {
            return arrayOfNulls(size)
        }
    }

}