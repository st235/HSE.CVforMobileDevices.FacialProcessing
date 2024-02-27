package github.com.st235.facialprocessing.data.db

import androidx.room.ColumnInfo

data class MediaFileEntity(
    @ColumnInfo(name = "media_id") val mediaId: Long,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
)
