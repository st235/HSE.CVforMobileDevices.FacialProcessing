package github.com.st235.facialprocessing.domain

import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.domain.clustering.Distance
import kotlin.math.pow
import kotlin.math.sqrt

class FaceDistanceMetric: Distance<FaceWithMediaFileEntity> {

    override fun calculateDistance(one: FaceWithMediaFileEntity, another: FaceWithMediaFileEntity): Double {
        var distance = 0.0

        val embeddingsSize = one.embeddings.size
        for (i in 0 until embeddingsSize) {
            distance += (another.embeddings[i] - one.embeddings[i]).pow(2)
        }

        return sqrt(distance)
    }
}