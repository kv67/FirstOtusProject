package kve.ru.firstproject.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "film_name") var name: String?,
    @ColumnInfo(name = "notification_dt") var date: String?,
    @ColumnInfo(name = "dsc") var dsc: String?
)