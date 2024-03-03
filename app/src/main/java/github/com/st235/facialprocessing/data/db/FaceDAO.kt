package github.com.st235.facialprocessing.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FaceDAO {

    @Insert
    fun insert(vararg face: FaceEntity)

    @Query("SELECT * FROM face INNER JOIN media_file ON face.media_id == media_file.media_id WHERE face.id = :id")
    fun getById(id: Int): FaceWithMediaFileEntity

    @Query("SELECT * FROM face INNER JOIN media_file ON face.media_id == media_file.media_id")
    fun getAll(): List<FaceWithMediaFileEntity>

    @Query("SELECT media_file.media_id, media_file.media_uri FROM face INNER JOIN media_file ON face.media_id == media_file.media_id GROUP BY media_file.media_id, media_file.media_uri")
    fun getMediaFilesWithFaces(): List<MediaFileEntity>

    @Query("SELECT * FROM face INNER JOIN media_file ON face.media_id == media_file.media_id WHERE media_file.media_id = :mediaId")
    fun getAllFacesByMediaFile(mediaId: Long): List<FaceWithMediaFileEntity>

}
