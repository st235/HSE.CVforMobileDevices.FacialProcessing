package github.com.st235.facialprocessing.domain.clustering

import github.com.st235.facialprocessing.domain.clustering.dbscan.DbscanClusterer
import org.example.hdbscan.HdbscanClusterer

interface Clusterer<V> {
    enum class Algorithm {
        DBSCAN,
        HDBSCAN,
    }

    fun cluster(points: List<V>): List<Set<V>>

    companion object {

        const val NO_CLUSTER = -1

        fun <T> create(
            metric: Distance<T>,
            algorithm: Algorithm = Algorithm.DBSCAN,
        ): Clusterer<T> {
            return when(algorithm) {
                Algorithm.DBSCAN -> DbscanClusterer(
                    minimumNumberOfClusterMembers = 5,
                    maxDistanceBetweenElementsInACluster = 0.78,
                    metric = metric
                )
                Algorithm.HDBSCAN -> HdbscanClusterer(
                    distanceMetric = metric
                )
            }
        }
    }
}