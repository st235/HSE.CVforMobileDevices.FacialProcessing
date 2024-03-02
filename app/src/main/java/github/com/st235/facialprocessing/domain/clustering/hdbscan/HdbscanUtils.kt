package org.example.hdbscan

import github.com.st235.facialprocessing.domain.clustering.Distance
import java.util.*
import kotlin.math.max


object HdbscanUtils {

    private var clusterIdFactory = 0

    fun <T> getPairwiseDistances(points: List<T>, distanceMetric: Distance<T>): Array<DoubleArray> {
        val n = points.size
        val distances = Array(size = n) { DoubleArray(n) }

        for (i in 0 until n) {
            for (j in 0 until i) {
                val distance = distanceMetric.calculateDistance(points[i], points[j])
                distances[i][j] = distance
                distances[j][i] = distance
            }
        }

        return distances
    }

    fun calculateCoreDistances(distances: Array<DoubleArray>, k: Int): DoubleArray {
        val n = distances.size

        val numNeighbors = k - 1
        if (numNeighbors == 0) {
            return DoubleArray(n) { 0.0 }
        }

        val coreDistances = DoubleArray(n)

        for (point in 0 until n) {
            val knnDistances = DoubleArray(numNeighbors) { Double.MAX_VALUE }

            for (neighbor in 0 until n) {
                if (point == neighbor) {
                    continue
                }

                val distance = distances[point][neighbor]

                // TODO: replace with a binary search?
                var insertionIndex = knnDistances.size
                while (insertionIndex >= 1 && distance < knnDistances[insertionIndex - 1]) {
                    insertionIndex -= 1
                }

                // InsertionIndex points exactly at the place where we want to insert the distance.
                if (insertionIndex < knnDistances.size) {
                    var i = knnDistances.size - 1
                    while (i > insertionIndex) {
                        knnDistances[i] = knnDistances[i - 1]
                        i--
                    }
                    // Inserting the value.
                    knnDistances[insertionIndex] = distance
                }
            }

            coreDistances[point] = knnDistances.last()
        }

        return coreDistances
    }

    fun constructMinimalSpanningTree(
        distances: Array<DoubleArray>,
        coreDistances: DoubleArray,
        selfEdges: Boolean
    ): UndirectedGraph {
        val n = distances.size

        val selfEdgeCapacity = if (selfEdges) {
            n
        } else {
            0
        }

        val attachedPoints = BitSet(n)

        val nearestMutualReachabilityNeighbors = IntArray(n - 1 + selfEdgeCapacity)
        val nearestMutualReachabilityDistances = DoubleArray(n - 1 + selfEdgeCapacity)
        for (i in 0 until (n - 1)) {
            nearestMutualReachabilityDistances[i] = Double.MAX_VALUE
        }

        var currentPoint = n - 1
        attachedPoints.set(currentPoint)
        var attachedPointsCount = 1
        while (attachedPointsCount < n) {
            var nearestMutualReachabilityPoint = -1
            var nearestMutualReachabilityDistance = Double.MAX_VALUE

            for (neighbour in 0 until n) {
                if (currentPoint == neighbour) {
                    continue
                }
                if (attachedPoints.get(neighbour)) {
                    continue
                }

                val distance = distances[currentPoint][neighbour]

                // Defining lambda-space.
                val mutualReachabilityDistance = max(
                    distance,
                    max(
                        coreDistances[currentPoint],
                        coreDistances[neighbour]
                    )
                )

                if (mutualReachabilityDistance < nearestMutualReachabilityDistances[neighbour]) {
                    nearestMutualReachabilityDistances[neighbour] = mutualReachabilityDistance
                    nearestMutualReachabilityNeighbors[neighbour] = currentPoint
                }

                if (nearestMutualReachabilityDistances[neighbour] <= nearestMutualReachabilityDistance) {
                    nearestMutualReachabilityDistance = nearestMutualReachabilityDistances[neighbour]
                    nearestMutualReachabilityPoint = neighbour
                }
            }

            attachedPoints.set(nearestMutualReachabilityPoint)
            attachedPointsCount += 1
            currentPoint = nearestMutualReachabilityPoint
        }

        val otherVertexIndices = IntArray(n - 1 + selfEdgeCapacity)
        for (i in 0 until (n - 1)) {
            otherVertexIndices[i] = i
        }

        if (selfEdges) {
            for (i in (n - 1) until (n * 2 - 1)) {
                val vertex = i - (n - 1)
                nearestMutualReachabilityNeighbors[i] = vertex
                otherVertexIndices[i] = vertex
                nearestMutualReachabilityDistances[i] = coreDistances[vertex]
            }
        }

        return UndirectedGraph(
            n,
            nearestMutualReachabilityNeighbors,
            otherVertexIndices,
            nearestMutualReachabilityDistances
        )
    }

    data class ComputationResult(
        val hierarchy: List<IntArray>,
        val pointNoiseLevels: DoubleArray,
        val pointLastClusters: IntArray,
        val clusters: List<Cluster?>
    )

    /**
     *  <summary>
     * Calculates the number of constraints satisfied by the new clusters and virtual children of the
     * parents of the new clusters.
     * </summary>
     * <param name="newClusterLabels">Labels of new clusters</param>
     * <param name="clusters">An List of clusters</param>
     * <param name="constraints">An List of constraints</param>
     * <param name="clusterLabels">An array of current cluster labels for points</param>
     **/
    fun calculateNumConstraintsSatisfied(
        newClusterLabels: Set<Int>,
        clusters: List<Cluster?>,
        constraints: List<Constraint>,
        clusterLabels: IntArray,
    ) {

        if (constraints.isEmpty()) {
            return
        }

        val parents = mutableListOf<Cluster>()
        for (label in newClusterLabels) {
            val parent = clusters[label]?.parent
            if (parent != null && (parents.find { it.getClusterId() == parent.getClusterId() } == null)) {
                parents.add(parent)
            }
        }

        for (constraint in constraints) {
            val labelA = clusterLabels[constraint.pointA]
            val labelB = clusterLabels[constraint.pointB]

            if (constraint.type == Constraint.Type.MUST_LINK && labelA == labelB) {
                if (newClusterLabels.contains(labelA)) {
                    clusters[labelA]?.addConstraintsSatisfied(2)
                }
            } else if (constraint.type == Constraint.Type.CANNOT_LINK && (labelA != labelB || labelA == 0)) {
                if (labelA != 0 && newClusterLabels.contains(labelA)) {
                    clusters[labelA]?.addConstraintsSatisfied(1)
                }
                // TODO: was labelA?
                if (labelB != 0 && newClusterLabels.contains(labelB)) {
                    clusters[labelB]?.addConstraintsSatisfied(1)
                }
                if (labelA == 0) {
                    for (parent in parents) {
                        if (parent.virtualChildClusterConstraintsPoint(constraint.pointA)) {
                            parent.addVirtualChildConstraintsSatisfied(1)
                            break
                        }
                    }
                }
                if (labelB == 0) {
                    for (parent in parents) {
                        if (parent.virtualChildClusterConstraintsPoint(constraint.pointB)) {
                            parent.addVirtualChildConstraintsSatisfied(1)
                            break
                        }
                    }
                }
            }
        }

        for (parent in parents) {
            parent.releaseVirtualChildCluster()
        }
    }

    /**
     * Removes the set of points from their parent Cluster, and creates a new Cluster, provided the
     * clusterId is not 0 (noise).
     * @param points The set of points to be in the new Cluster
     * @param clusterLabels An array of cluster labels, which will be modified
     * @param parentCluster The parent Cluster of the new Cluster being created
     * @param clusterLabel The label of the new Cluster
     * @param edgeWeight The edge weight at which to remove the points from their previous Cluster
     * @return The new Cluster, or null if the clusterId was 0
     */
    private fun createNewCluster(
        points: Set<Int>,
        clusterLabels: IntArray,
        parentCluster: Cluster?,
        clusterLabel: Int,
        edgeWeight: Double
    ): Cluster? {
        for (point in points) {
            clusterLabels[point] = clusterLabel
        }

        parentCluster?.detachPoints(points.size, edgeWeight)

        if (clusterLabel != 0) {
            val cluster = Cluster(clusterIdFactory, clusterLabel, parentCluster, edgeWeight, points.size)
            clusterIdFactory += 1
            return cluster
        }

        parentCluster?.addPointsToVirtualChildCluster(points)
        return null
    }

    fun computeHierarchyAndClusterTree(
        n: Int,
        mst: UndirectedGraph,
        minClusterSize: Int,
        constraints: List<Constraint>,
    ): ComputationResult {
        val hierarchy = mutableListOf<IntArray>()
        val pointNoiseLevels = DoubleArray(n)
        val pointLastClusters = IntArray(n)

        var hierarchyPosition = 0

        // The current edge being removed from the MST. The one with higher edge weight.
        var currentEdgeIndex = mst.getNumberOfEdges() - 1
        var nextClusterLabel = 2
        var nextLevelSignificant = true

        // The previous and current cluster numbers of each point in the data set.
        val previousClusterLabels = IntArray(mst.getNumberOfVertices())
        val currentClusterLabels = IntArray(mst.getNumberOfVertices())

        for (i in currentClusterLabels.indices) {
            currentClusterLabels[i] = 1
            previousClusterLabels[i] = 1
        }

        // A list of clusters in the cluster tree, with the 0th cluster (noise) null.
        val clusters = mutableListOf<Cluster?>()
        clusters.add(null)

        //cluster cluster_object(1, NULL, std::numeric_limits<double>::quiet_NaN(),  mst->getNumVertices());
        clusters.add(Cluster(clusterIdFactory, 1, null, Double.NaN, mst.getNumberOfVertices()))
        clusterIdFactory += 1

        val clusterOne = TreeSet<Int>()
        clusterOne.add(1)
        calculateNumConstraintsSatisfied(clusterOne, clusters, constraints, currentClusterLabels)

        val affectedClusterLabels = LinkedList<Int>()
        val affectedVertices = LinkedList<Int>()

        // Loop to remove all edges tied with the current edge weight and keep track of affected clusters and vertices.
        while (currentEdgeIndex >= 0) {
            // Retrieves the largest edge weight in the current tree.
            val currentEdgeWeight = mst.getEdgeWeightAtIndex(currentEdgeIndex)


            // ArrayList to store the clusters being created at this iteration.
            val newClusters = mutableListOf<Cluster?>()
            while (currentEdgeIndex >= 0 && mst.getEdgeWeightAtIndex(currentEdgeIndex) == currentEdgeWeight) {
                val firstVertex = mst.getFirstVertexAtIndex(currentEdgeIndex)
                val secondVertex = mst.getSecondVertexAtIndex(currentEdgeIndex)

                mst.removeVertexFromAdjacencySet(firstVertex, secondVertex)
                mst.removeVertexFromAdjacencySet(secondVertex, firstVertex)

                if (currentClusterLabels[firstVertex] == 0) {
                    currentEdgeIndex--
                    continue
                }
                affectedVertices.add(firstVertex)
                affectedVertices.add(secondVertex)
                affectedClusterLabels.add(currentClusterLabels[firstVertex])
                currentEdgeIndex--
            }

            // Treats the case where edge removals do not affect any of the clusters.
            if (affectedClusterLabels.isEmpty()) {
                continue
            }

            // Check each affected cluster for a possible split:
            while (!affectedClusterLabels.isEmpty()) {

                // Retrieves and removes the last affected cluster.
                val examinedClusterLabel = affectedClusterLabels.poll()
                affectedClusterLabels.remove(examinedClusterLabel)

                val examinedVertices = TreeSet<Int>()

                // Get all affected vertices that are members of the cluster currently being examined:
                val vertexIterator = affectedVertices.iterator()
                while (vertexIterator.hasNext()) {
                    val vertex = vertexIterator.next()

                    if (currentClusterLabels[vertex] == examinedClusterLabel) {
                        examinedVertices.add(vertex)
                        vertexIterator.remove()
                    }
                }

                val firstChildCluster = TreeSet<Int>()
                val unexploredFirstChildClusterPoints = LinkedList<Int>()
                var numChildClusters = 0

                /*
                * Check if the cluster has split or shrunk by exploring the graph from each affected
                * vertex.  If there are two or more valid child clusters (each has >= minClusterSize
                * points), the cluster has split.
                * Note that firstChildCluster will only be fully explored if there is a cluster
                * split, otherwise, only spurious components are fully explored, in order to label
                * them noise.
                */
                while (!examinedVertices.isEmpty()) {
                    val constructingSubCluster = TreeSet<Int>()

                    val unexploredSubClusterPoints = LinkedList<Int>()
                    var anyEdges = false
                    var incrementedChildCount = false

                    val rootVertex = examinedVertices.last()
                    constructingSubCluster.add(rootVertex)
                    unexploredSubClusterPoints.add(rootVertex)
                    examinedVertices.remove(rootVertex)

                    // Explore this potential child cluster as long as there are unexplored points:
                    while (!unexploredSubClusterPoints.isEmpty()) {
                        val vertexToExplore = unexploredSubClusterPoints.poll()

                        for (neighbor in mst.getEdgeListForVertex(vertexToExplore)) {
                            anyEdges = true
                            if (constructingSubCluster.add(neighbor)) {
                                unexploredSubClusterPoints.add(neighbor)
                                examinedVertices.remove(neighbor)
                            }
                        }

                        // Check if this potential child cluster is a valid cluster:
                        if (!incrementedChildCount && constructingSubCluster.size >= minClusterSize && anyEdges) {
                            incrementedChildCount = true
                            numChildClusters++

                            // If this is the first valid child cluster, stop exploring it:
                            if (firstChildCluster.isEmpty()) {
                                firstChildCluster.addAll(constructingSubCluster)
                                unexploredFirstChildClusterPoints.clear()
                                unexploredFirstChildClusterPoints.addAll(unexploredSubClusterPoints)
                                break
                            }
                        }
                    }

                    // If there could be a split, and this child cluster is valid:
                    if (numChildClusters >= 2 && constructingSubCluster.size >= minClusterSize && anyEdges) {
                        // Check this child cluster is not equal to the unexplored first child cluster:
                        val firstChildClusterMember = firstChildCluster.iterator().next()
                        if (constructingSubCluster.contains(firstChildClusterMember)) {
                            numChildClusters--
                        } else {
                            //Otherwise, c a new cluster:
                            val newCluster = createNewCluster(
                                constructingSubCluster, currentClusterLabels,
                                clusters[examinedClusterLabel], nextClusterLabel, currentEdgeWeight
                            );
                            newClusters.add(newCluster)
                            clusters.add(newCluster);
                            nextClusterLabel++;
                        }
                    } else if (constructingSubCluster.size < minClusterSize || !anyEdges) {
                        createNewCluster(
                            constructingSubCluster, currentClusterLabels,
                            clusters[examinedClusterLabel], 0, currentEdgeWeight
                        );

                        for (point in constructingSubCluster) {
                            pointNoiseLevels[point] = currentEdgeWeight
                            pointLastClusters[point] = examinedClusterLabel
                        }
                    }
                }
                // Finish exploring and cluster the first child cluster if there was a split and it was not already clustered:
                if (numChildClusters >= 2 && currentClusterLabels[firstChildCluster.iterator()
                        .next()] == examinedClusterLabel
                ) {
                    while (!unexploredFirstChildClusterPoints.isEmpty()) {
                        val vertexToExplore = unexploredFirstChildClusterPoints.poll()

                        for (neighbor in mst.getEdgeListForVertex(vertexToExplore)) {
                            if (firstChildCluster.add(neighbor)) {
                                unexploredFirstChildClusterPoints.add(neighbor)
                            }
                        }
                    }
                    val newCluster = createNewCluster(
                        firstChildCluster, currentClusterLabels,
                        clusters[examinedClusterLabel], nextClusterLabel, currentEdgeWeight
                    )
                    newClusters.add(newCluster)
                    clusters.add(newCluster)
                    nextClusterLabel++
                }
            }
            if (nextLevelSignificant || newClusters.isNotEmpty()) {
                val lineContents = IntArray(previousClusterLabels.size)
                for (i in previousClusterLabels.indices) {
                    lineContents[i] = previousClusterLabels[i]
                }
                hierarchy.add(lineContents)
                hierarchyPosition++
            }
            val newClusterLabels = TreeSet<Int>()
            for (newCluster in newClusters) {
                if (newCluster != null) {
                    newCluster.hierarchyPosition = hierarchyPosition
                    newClusterLabels.add(newCluster.label)
                }
            }
            if (newClusterLabels.isNotEmpty()) {
                calculateNumConstraintsSatisfied(newClusterLabels, clusters, constraints, currentClusterLabels)
            }

            for (i in previousClusterLabels.indices) {
                previousClusterLabels[i] = currentClusterLabels[i]
            }
            if (newClusters.isEmpty()) {
                nextLevelSignificant = false;
            } else {
                nextLevelSignificant = true;
            }
        }

        val lineContents = IntArray(previousClusterLabels.size + 1)
        for (i in previousClusterLabels.indices) {
            lineContents[i] = 0
        }
        hierarchy.add(lineContents)

        return ComputationResult(
            hierarchy,
            pointNoiseLevels,
            pointLastClusters,
            clusters
        )
    }

    /**
     * Propagates constraint satisfaction, stability, and lowest child death level from each child
     * cluster to each parent cluster in the tree.  This method must be called before calling
     * findProminentClusters() or calculateOutlierScores().
     * @param clusters A list of Clusters forming a cluster tree
     * @return true if there are any clusters with infinite stability, false otherwise
     */
    fun propagateTree(clusters: List<Cluster?>): Boolean {
        val clustersToExamine = TreeMap<Int, Cluster?>()
        val addedToExaminationList = BitSet(clusters.size)
        var infiniteStability = false

        //Find all leaf clusters in the cluster tree:
        for (cluster in clusters) {
            if (cluster != null && !cluster.hasChildren) {
                clustersToExamine[cluster.label] = cluster
                addedToExaminationList.set(cluster.label)
            }
        }

        //Iterate through every cluster, propagating stability from children to parents:
        while (!clustersToExamine.isEmpty()) {
            val currentCluster = clustersToExamine.pollLastEntry().value
            currentCluster!!.propagate()

            if (currentCluster!!.stability == Double.POSITIVE_INFINITY) infiniteStability = true

            if (currentCluster!!.parent != null) {
                val parent = currentCluster!!.parent

                if (!addedToExaminationList[parent!!.label]) {
                    clustersToExamine[parent!!.label] = parent
                    addedToExaminationList.set(parent!!.label)
                }
            }
        }

        return infiniteStability
    }

    fun findProminentClusters(
        clusters: List<Cluster?>,
        hierarchy: List<IntArray>,
        numPoints: Int
    ): IntArray {
        //Take the list of propagated clusters from the root cluster:
        val solution = clusters[1]!!.propagatedDescendants
        val flatPartitioning = IntArray(numPoints)

        //Store all the hierarchy positions at which to find the birth points for the flat clustering:
        val significantHierarchyPositions = TreeMap<Int, MutableList<Int>>()

        for (cluster in solution) {
            val hierarchyPosition = cluster.hierarchyPosition
            if (!significantHierarchyPositions.contains(hierarchyPosition)) {
                significantHierarchyPositions[hierarchyPosition] = mutableListOf()
            }

            significantHierarchyPositions.getValue(hierarchyPosition).add(cluster.label)
        }

        //Go through the hierarchy file, setting labels for the flat clustering:
        while (!significantHierarchyPositions.isEmpty()) {
            val entry = significantHierarchyPositions.pollFirstEntry()
            val clusterList = entry.value
            val hierarchyPosition = entry.key

            significantHierarchyPositions.remove(hierarchyPosition)

            val lineContents = hierarchy[hierarchyPosition]

            for (i in 1 until lineContents.size) {
                val label = lineContents[i].toInt()
                if (clusterList.contains(label)) {
                    flatPartitioning[i] = label
                }
            }
        }
        return flatPartitioning;
    }

    fun findMembershipScore(
        clusterids: IntArray,
        coreDistances: DoubleArray
    ): DoubleArray {
        val length = clusterids.size
        val prob = DoubleArray(length) { Double.MAX_VALUE }

        var i = 0
        while (i < length) {
            if (prob[i] == Double.MAX_VALUE) {

                val clusterno = clusterids[i]
                val indices = mutableListOf<Int>()
                for ((j, c) in clusterids.withIndex()) {
                    if (c == clusterno) {
                        indices.add(j)
                    }
                }

                if (clusterno == 0) {
                    for (index in indices) {
                        prob[index] = 0.0
                    }
                    i++
                    continue
                }
                val tempCoreDistances = DoubleArray(indices.size) { j -> coreDistances[j] }
                val maxCoreDistance = tempCoreDistances.max()
                for (j in tempCoreDistances.indices) {
                    prob[indices[j]] = (maxCoreDistance - tempCoreDistances[j]) / maxCoreDistance
                }

            }

            i++
        }
        return prob
    }
}