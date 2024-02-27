package github.com.st235.facialprocessing.data

import github.com.st235.facialprocessing.data.db.FaceDAO
import github.com.st235.facialprocessing.data.db.FaceEntity
import github.com.st235.facialprocessing.data.db.FaceScannerDatabase
import github.com.st235.facialprocessing.data.db.MediaFileEntity

class FacesRepository(
    private val faceScannerDatabase: FaceScannerDatabase
) {

    private val faceDao: FaceDAO = faceScannerDatabase.getFaceDao()

    fun getProcessedMediaFiles(): List<MediaFileEntity> {
        return faceDao.getProcessedMediaFiles()
    }

    fun insert(faces: List<FaceEntity>) {
        faceDao.insert(*faces.toTypedArray())
    }

    fun getAllFaces(): List<FaceEntity> {
        return faceDao.getAll()
    }

}