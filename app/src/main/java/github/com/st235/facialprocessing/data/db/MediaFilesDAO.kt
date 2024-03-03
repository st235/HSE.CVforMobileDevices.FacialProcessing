package github.com.st235.facialprocessing.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MediaFilesDAO {

    @Insert
    fun insert(vararg mediaFileEntities: MediaFileEntity)

    @Query("SELECT * FROM media_file WHERE media_file.media_id = :id")
    fun getById(id: Long): MediaFileEntity

    @Query("SELECT * FROM media_file")
    fun getProcessedMediaFiles(): List<MediaFileEntity>

}