package github.com.st235.facialprocessing.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FaceEntity::class, MediaFileEntity::class, ClusterEntity::class],
    version = 1,
)
@TypeConverters(ListOfFloatsTypeConverter::class)
abstract class FaceScannerDatabase: RoomDatabase() {
    abstract fun getFaceDao(): FaceDAO

    abstract fun getMediaFilesDao(): MediaFilesDAO

    abstract fun getClustersDao(): ClusterDAO
}
