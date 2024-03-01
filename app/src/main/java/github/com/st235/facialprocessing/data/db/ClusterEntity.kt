package github.com.st235.facialprocessing.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "clusters",
    foreignKeys = [
        ForeignKey(
            entity = FaceEntity::class,
            parentColumns = [ "id" ],
            childColumns = [ "face_id" ],
        )
    ],
)
data class ClusterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo("cluster_id") val clusterId: Int,
    @ColumnInfo("face_id") val faceId: Int,
)
