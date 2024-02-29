package github.com.st235.facialprocessing.domain.clustering

import github.com.st235.facialprocessing.domain.clustering.dbscan.DbscanClusterer

interface Clusterer<V> {

    enum class Algorithm {
        DBSCAN,
    }

    fun cluster(points: List<V>): List<Set<V>>

    companion object {
        fun <T> create(
            metric: Distance<T>,
            algorithm: Algorithm = Algorithm.DBSCAN,
        ): Clusterer<T> {
            return when(algorithm) {
                Algorithm.DBSCAN -> DbscanClusterer(
                    minimumNumberOfClusterMembers = 5,
                    maxDistanceBetweenElementsInACluster = 0.5,
                    metric = metric
                )
            }
        }
    }
}