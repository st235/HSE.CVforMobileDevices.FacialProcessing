package github.com.st235.facialprocessing.data

import github.com.st235.facialprocessing.data.db.FaceDAO
import github.com.st235.facialprocessing.data.db.FaceEntity
import github.com.st235.facialprocessing.data.db.FaceScannerDatabase
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.data.db.MediaFileEntity

class FacesRepository(
    private val faceScannerDatabase: FaceScannerDatabase
) {

    private val faceDao = faceScannerDatabase.getFaceDao()
    private val mediaFilesDao = faceScannerDatabase.getMediaFilesDao()

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

    fun getAllFaces(): List<FaceWithMediaFileEntity> {
        return faceDao.getAll()
    }

}