package github.com.st235.facialprocessing.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClusterDAO {

    @Insert
    fun insert(vararg clusterEntity: ClusterEntity)

    @Query("SELECT clusters.cluster_id FROM clusters WHERE clusters.face_id = :faceId")
    fun getClusterIdByFaceId(faceId: Int): Int

    @Query("SELECT * FROM clusters")
    fun getAll(): List<ClusterEntity>

    @Query("SELECT face.id, face.region_left, face.region_top, face.region_width, face.region_height, face.media_id, media_file.media_uri, face.age, face.gender, face.emotion, face.has_beard, face.has_glasses, face.has_mustache, face.is_smiling, face.embeddings FROM clusters INNER JOIN face ON clusters.face_id == face.id INNER JOIN media_file ON face.media_id == media_file.media_id WHERE clusters.cluster_id = :clusterId ORDER BY RANDOM() LIMIT 1")
    fun fetchRandomFaceForCluster(clusterId: Int): FaceWithMediaFileEntity

    @Query("SELECT face.id, face.region_left, face.region_top, face.region_width, face.region_height, face.media_id, media_file.media_uri, face.age, face.gender, face.emotion, face.has_beard, face.has_glasses, face.has_mustache, face.is_smiling, face.embeddings FROM clusters INNER JOIN face ON clusters.face_id == face.id INNER JOIN media_file ON face.media_id == media_file.media_id WHERE clusters.cluster_id = :clusterId")
    fun fetchFacesForCluster(clusterId: Int): List<FaceWithMediaFileEntity>

    @Query("DELETE FROM clusters")
    fun nuke()

}