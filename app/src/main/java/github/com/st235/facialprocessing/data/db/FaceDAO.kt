package github.com.st235.facialprocessing.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FaceDAO {

    @Insert
    fun insert(vararg face: FaceEntity)

    @Query("SELECT * FROM face")
    fun getAll(): List<FaceEntity>

    @Query("SELECT media_id, media_url FROM face GROUP BY media_id, media_url")
    fun getProcessedMediaFiles(): List<MediaFileEntity>

}
