package github.com.st235.facialprocessing.interactors.utils

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.rescale

@WorkerThread
fun extractClusters(
    facesRepository: FacesRepository,
    localUriLoader: LocalUriLoader,
    n: Int? = null,
): List<FaceCluster> {
    val faceClusters = mutableListOf<FaceCluster>()
    val clusters = facesRepository.getClusterToFacesLookup()

    for (cluster in clusters.entries.sortedBy { it.key }) {
        val clusterId = cluster.key
        val randomFaceId = cluster.value.min()
        val randomFaceEntity = facesRepository.getFaceById(randomFaceId)

        val mediaFile = localUriLoader.load(Uri.parse(randomFaceEntity.mediaUrl))

        if (mediaFile == null) {
            throw IllegalStateException("Media file should never be null.")
        }

        val faceBitmap = Bitmap.createBitmap(
            mediaFile,
            (randomFaceEntity.left * mediaFile.width).toInt(), (randomFaceEntity.top * mediaFile.height).toInt(),
            (randomFaceEntity.width * mediaFile.width).toInt(), (randomFaceEntity.height * mediaFile.height).toInt(),
        ).rescale(maxWidth = 128, maxHeight = 128)

        faceClusters.add(
            FaceCluster(
                clusterId = clusterId,
                sampleFace = faceBitmap
            )
        )

        if (n != null && faceClusters.size >= n) {
            break
        }
    }

    return faceClusters
}

@WorkerThread
fun extractCluster(
    clusterId: Int,
    facesRepository: FacesRepository,
    localUriLoader: LocalUriLoader,
): FaceCluster {
    val clusters = facesRepository.getClusterToFacesLookup()

    val cluster = clusters.getValue(clusterId)
    val randomFaceId = cluster.min()
    val randomFaceEntity = facesRepository.getFaceById(randomFaceId)

    val mediaFile = localUriLoader.load(Uri.parse(randomFaceEntity.mediaUrl))

    if (mediaFile == null) {
        throw IllegalStateException("Media file should never be null.")
    }

    val faceBitmap = Bitmap.createBitmap(
        mediaFile,
        (randomFaceEntity.left * mediaFile.width).toInt(), (randomFaceEntity.top * mediaFile.height).toInt(),
        (randomFaceEntity.width * mediaFile.width).toInt(), (randomFaceEntity.height * mediaFile.height).toInt(),
    ).rescale(maxWidth = 64, maxHeight = 64)

    return FaceCluster(
        clusterId = clusterId,
        sampleFace = faceBitmap
    )
}

