package github.com.st235.facialprocessing.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class FaceWithMediaFileEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "region_left") val left: Float,
    @ColumnInfo(name = "region_top") val top: Float,
    @ColumnInfo(name = "region_width") val width: Float,
    @ColumnInfo(name = "region_height") val height: Float,
    @ColumnInfo(name = "media_id") val mediaId: Long,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "gender") val gender: Int,
    @ColumnInfo(name = "emotion") val emotion: Int,
    @ColumnInfo(name = "has_beard") val hasBeard: Boolean,
    @ColumnInfo(name = "has_glasses") val hasGlasses: Boolean,
    @ColumnInfo(name = "has_mustache") val hasMustache: Boolean,
    @ColumnInfo(name = "is_smiling") val isSmiling: Boolean,
    @ColumnInfo(name = "embeddings") val embeddings: List<Float>,
)
