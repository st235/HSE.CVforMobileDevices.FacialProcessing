package github.com.st235.facialprocessing.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FaceEntity::class],
    version = 1,
)
@TypeConverters(ListOfFloatsTypeConverter::class)
abstract class FaceScannerDatabase: RoomDatabase() {
    abstract fun getFaceDao(): FaceDAO
}
