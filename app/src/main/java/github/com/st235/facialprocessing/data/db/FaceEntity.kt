package github.com.st235.facialprocessing.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "face",
    foreignKeys = [
        ForeignKey(
            entity = MediaFileEntity::class,
            parentColumns = [ "media_id" ],
            childColumns = [ "media_id" ],
        )
    ],
    indices = [Index(value = ["media_id"])],
)
data class FaceEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "region_left") val left: Float,
    @ColumnInfo(name = "region_top") val top: Float,
    @ColumnInfo(name = "region_width") val width: Float,
    @ColumnInfo(name = "region_height") val height: Float,
    @ColumnInfo(name = "media_id") val mediaId: Long,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "gender") val gender: Int,
    @ColumnInfo(name = "emotion") val emotion: Int,
    @ColumnInfo(name = "has_beard") val hasBeard: Boolean,
    @ColumnInfo(name = "has_glasses") val hasGlasses: Boolean,
    @ColumnInfo(name = "has_mustache") val hasMustache: Boolean,
    @ColumnInfo(name = "is_smiling") val isSmiling: Boolean,
    @ColumnInfo(name = "embeddings") val embeddings: List<Float>,
)
