package github.com.st235.facialprocessing.domain

import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.domain.clustering.Distance
import kotlin.math.pow
import kotlin.math.sqrt

sealed class FaceDistanceMetric: Distance<FaceWithMediaFileEntity> {

    data object L2FaceDistanceMetric : FaceDistanceMetric() {
        override fun calculateDistance(
            one: FaceWithMediaFileEntity,
            another: FaceWithMediaFileEntity
        ): Double {
            var distance = 0.0

            val embeddingsSize = one.embeddings.size
            for (i in 0 until embeddingsSize) {
                distance += (another.embeddings[i] - one.embeddings[i]).pow(2)
            }

            return sqrt(distance)
        }
    }

    data object CosineFaceDistanceMetric : FaceDistanceMetric() {
        override fun calculateDistance(
            one: FaceWithMediaFileEntity,
            another: FaceWithMediaFileEntity
        ): Double {
            val x1 = one.embeddings
            val x2 = another.embeddings

            var dotProduct = 0.0
            var normA = 0.0
            var normB = 0.0
            for (i in x1.indices) {
                dotProduct += x1[i] * x2[i]
                normA += x1[i].pow(2)
                normB += x2[i].pow(2)
            }
            return dotProduct / (sqrt(normA) * sqrt(normB))
        }
    }

}