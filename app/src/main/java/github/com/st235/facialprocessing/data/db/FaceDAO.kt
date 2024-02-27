package github.com.st235.facialprocessing.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FaceDAO {

    @Insert
    fun insert(vararg face: FaceEntity)

    @Query("SELECT * FROM face INNER JOIN media_file ON face.media_id == media_file.media_id")
    fun getAll(): List<FaceWithMediaFileEntity>

}
