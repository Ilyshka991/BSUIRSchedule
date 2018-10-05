package com.pechuro.bsuirschedule.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "all_employees")
data class Employee(

        @PrimaryKey()
        @ColumnInfo(name = "employee_id")
        @SerializedName("id")
        var employeeId: String,

        @ColumnInfo(name = "first_name")
        var firstName: String,

        @ColumnInfo(name = "last_name")
        var lastName: String,

        @ColumnInfo(name = "middle_name")
        var middleName: String,

        var fio: String,

        @ColumnInfo(name = "photo_link")
        var photoLink: String?,

        @ColumnInfo(name = "rank")
        var rank: String?
)