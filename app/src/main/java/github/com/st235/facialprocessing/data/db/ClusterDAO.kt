package github.com.st235.facialprocessing.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClusterDAO {

    @Insert
    fun insert(vararg clusterEntity: ClusterEntity)

    @Query("SELECT * FROM clusters")
    fun getAll(): List<ClusterEntity>

    @Query("DELETE FROM clusters")
    fun nuke()

}