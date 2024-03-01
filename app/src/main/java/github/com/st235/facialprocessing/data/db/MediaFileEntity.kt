package github.com.st235.facialprocessing.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_file")
data class MediaFileEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "media_id") val mediaId: Long,
    @ColumnInfo(name = "media_uri") val mediaUri: String,
)
