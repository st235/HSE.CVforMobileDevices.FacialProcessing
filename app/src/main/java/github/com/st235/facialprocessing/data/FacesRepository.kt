package github.com.st235.facialprocessing.data

import github.com.st235.facialprocessing.data.db.ClusterEntity
import github.com.st235.facialprocessing.data.db.FaceEntity
import github.com.st235.facialprocessing.data.db.FaceScannerDatabase
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.data.db.MediaFileEntity

class FacesRepository(
    private val faceScannerDatabase: FaceScannerDatabase
) {

    private val faceDao = faceScannerDatabase.getFaceDao()
    private val mediaFilesDao = faceScannerDatabase.getMediaFilesDao()
    private val clustersDao = faceScannerDatabase.getClustersDao()

    fun getProcessedMediaFiles(): List<MediaFileEntity> {
        return mediaFilesDao.getProcessedMediaFiles()
    }

    fun insert(
        mediaFile: MediaFileEntity,
        faces: List<FaceEntity>
    ) {
        mediaFilesDao.insert(mediaFile)
        if (faces.isNotEmpty()) {
            faceDao.insert(*faces.toTypedArray())
        }
    }

    fun getFaceById(faceId: Int): FaceWithMediaFileEntity {
        return faceDao.getById(faceId)
    }

    fun getMediaFileById(mediaId: Int): MediaFileEntity {
        return mediaFilesDao.getById(mediaId)
    }

    fun getAllFaces(): List<FaceWithMediaFileEntity> {
        return faceDao.getAll()
    }

    fun getMediaFilesWithFaces(): List<MediaFileEntity> {
        return faceDao.getMediaFilesWithFaces()
    }

    fun getAllFacesAtMediaFile(mediaId: Int): List<FaceWithMediaFileEntity> {
        return faceDao.getAllFacesByMediaFile(mediaId)
    }

    fun insertClusters(clusters: Map<Int, Int>) {
        clustersDao.nuke()
        clustersDao.insert(*clusters.map {
            ClusterEntity(
                faceId = it.key,
                clusterId = it.value,
            )
        }.toTypedArray())
    }

    fun getClusters(): Map<Int, Int> {
        return clustersDao.getAll().map { it.faceId to it.clusterId }.toMap()
    }
}