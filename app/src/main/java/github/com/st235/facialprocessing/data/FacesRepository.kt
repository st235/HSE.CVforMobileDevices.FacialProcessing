package github.com.st235.facialprocessing.data

import github.com.st235.facialprocessing.data.db.FaceEntity
import github.com.st235.facialprocessing.data.db.FaceScannerDatabase
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.data.db.MediaFileEntity

class FacesRepository(
    private val faceScannerDatabase: FaceScannerDatabase
) {

    private val faceDao = faceScannerDatabase.getFaceDao()
    private val mediaFilesDao = faceScannerDatabase.getMediaFilesDao()

    // Face id to Cluster id.
    private val clustersLookup = mutableMapOf<Int, Int>()

    fun getProcessedMediaFiles(): List<MediaFileEntity> {
        return mediaFilesDao.getProcessedMediaFiles()
    }

    fun insert(mediaFile: MediaFileEntity,
               faces: List<FaceEntity>) {
        mediaFilesDao.insert(mediaFile)
        if (faces.isNotEmpty()) {
            faceDao.insert(*faces.toTypedArray())
        }
    }

    fun getFaceById(faceId: Int): FaceWithMediaFileEntity {
        return faceDao.getById(faceId)
    }

    fun getAllFaces(): List<FaceWithMediaFileEntity> {
        return faceDao.getAll()
    }

    fun getMediaFilesWithFaces(): List<MediaFileEntity> {
        return faceDao.getMediaFilesWithFaces()
    }

    fun updateCluster(clusters: List<Set<FaceWithMediaFileEntity>>) {
        var clusterId = 0
        clustersLookup.clear()

        for (cluster in clusters) {
            for (face in cluster) {
                clustersLookup[face.id] = clusterId
            }
            clusterId += 1
        }
    }
}