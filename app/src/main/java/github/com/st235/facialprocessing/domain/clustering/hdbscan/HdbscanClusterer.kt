package org.example.hdbscan

import github.com.st235.facialprocessing.domain.clustering.Clusterer
import github.com.st235.facialprocessing.domain.clustering.Distance

class HdbscanClusterer<T>(
    private val distanceMetric: Distance<T>,
    private val filterProbability: Float = 0f
): Clusterer<T> {

    override fun cluster(points: List<T>): List<Set<T>> {
        val distances = HdbscanUtils.getPairwiseDistances(points, distanceMetric)
        val coreDistances = HdbscanUtils.calculateCoreDistances(distances, 5)
        val mst = HdbscanUtils.constructMinimalSpanningTree(distances, coreDistances, true)
        mst.quicksortByEdgeWeight()

        val numPoints = distances.size

        val hierarchyAndClusterTree = HdbscanUtils.computeHierarchyAndClusterTree(
            numPoints,
            mst,
            minClusterSize = 5,
            emptyList()
        )
        HdbscanUtils.propagateTree(hierarchyAndClusterTree.clusters)

        val prominentClusters = HdbscanUtils.findProminentClusters(hierarchyAndClusterTree.clusters, hierarchyAndClusterTree.hierarchy, numPoints)
        val membershipProbabilities = HdbscanUtils.findMembershipScore(prominentClusters, coreDistances)

        val result = mutableMapOf<Int, MutableSet<T>>()

        for (i in prominentClusters.indices) {
            if (membershipProbabilities[i] < filterProbability) {
                continue
            }

            val clusterId = prominentClusters[i]
            if (clusterId == 0) {
                continue
            }

            if (!result.containsKey(clusterId)) {
                result[clusterId] = mutableSetOf()
            }

            result[clusterId]?.add(points[i])
        }

        return ArrayList(result.values)
    }

}