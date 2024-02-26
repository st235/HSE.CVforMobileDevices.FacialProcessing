package github.com.st235.facialprocessing.domain.clustering.dbscan

import github.com.st235.facialprocessing.domain.clustering.Clusterer
import github.com.st235.facialprocessing.domain.clustering.Distance
import java.util.LinkedList
import java.util.Queue

class DbscanClusterer<V>(
    private val minimumNumberOfClusterMembers: Int,
    private val maxDistanceBetweenElementsInACluster: Double,
    private val metric: Distance<V>,
) : Clusterer<V> {

    override fun cluster(points: List<V>): List<Set<V>> {
        val visitedPoints = mutableSetOf<V>()

        val result = mutableListOf<Set<V>>()

        for (potentialClusterPoint in points) {
            if (visitedPoints.contains(potentialClusterPoint)) {
                continue
            }
            visitedPoints.add(potentialClusterPoint)

            val potentialCluster = potentialClusterPoint.findNeighboursIn(points)
            if (potentialCluster.size >= minimumNumberOfClusterMembers) {
                val cluster = mutableSetOf<V>()
                cluster.add(potentialClusterPoint)

                val nextClusterPoints: Queue<V> = LinkedList()
                nextClusterPoints.addAll(potentialCluster)

                while (nextClusterPoints.isNotEmpty()) {
                    val nextClusterPoint = nextClusterPoints.remove()
                    if (visitedPoints.contains(nextClusterPoint)) {
                        continue
                    }
                    visitedPoints.add(nextClusterPoint)
                    cluster.add(nextClusterPoint)

                    val neighboursOfAClusterPoint = nextClusterPoint.findNeighboursIn(points)
                    if (neighboursOfAClusterPoint.size >= minimumNumberOfClusterMembers) {
                        nextClusterPoints.addAll(neighboursOfAClusterPoint)
                    }
                }

                result.add(cluster)
            }
        }
        return result
    }

    private fun V.findNeighboursIn(points: List<V>): List<V> {
        val neighbours = ArrayList<V>()
        for (candidatePoint in points) {
            if (metric.calculateDistance(
                    this,
                    candidatePoint
                ) <= maxDistanceBetweenElementsInACluster
            ) {
                neighbours.add(candidatePoint)
            }
        }
        return neighbours
    }
}
