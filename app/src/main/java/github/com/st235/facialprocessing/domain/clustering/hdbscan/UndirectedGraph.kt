package github.com.st235.facialprocessing.domain.clustering.hdbscan

class UndirectedGraph(
    private val numVertices: Int,
    private val verticesA: IntArray,
    private val verticesB: IntArray,
    private val edgeWeights: DoubleArray,
) {

    private val adjacencyList = mutableMapOf<Int, MutableList<Int>>()

    init {
        if (verticesA.size != verticesB.size) {
            throw IllegalArgumentException()
        }

        if (verticesA.size != edgeWeights.size) {
            throw IllegalArgumentException()
        }

        for (i in verticesA.indices) {
            val vertexA = verticesA[i]
            val vertexB = verticesB[i]

            if (!adjacencyList.containsKey(vertexA)) {
                adjacencyList[vertexA] = mutableListOf()
            }

            adjacencyList.getValue(vertexA).add(vertexB)

            if (vertexA == vertexB) {
                continue
            }
            if (!adjacencyList.containsKey(vertexB)) {
                adjacencyList[vertexB] = mutableListOf()
            }

            adjacencyList.getValue(vertexB).add(vertexA)
        }
    }

    fun getNumberOfVertices(): Int {
        return numVertices
    }

    fun getNumberOfEdges(): Int {
        return edgeWeights.size
    }

    fun getFirstVertexAtIndex(index: Int): Int {
        return verticesA[index]
    }

    fun getSecondVertexAtIndex(index: Int): Int {
        return verticesB[index]
    }

    fun getEdgeWeightAtIndex(index: Int): Double {
        return edgeWeights[index]
    }

    fun getEdgeListForVertex(vertex: Int): List<Int> {
        return ArrayList(adjacencyList.getValue(vertex))
    }

    fun removeVertexFromAdjacencySet(parentVertex: Int, adjacentVertex: Int) {
        adjacencyList.getValue(parentVertex).remove(adjacentVertex)
    }

    fun quicksortByEdgeWeight() {
        if (edgeWeights.size <= 1) {
            return
        }

        quicksortByEdgeWeight(0, edgeWeights.size - 1)
    }

    private fun quicksortByEdgeWeight(startIndex: Int, endIndex: Int) {
        if (startIndex >= endIndex) {
            return
        }

        val pivotIndex = partition(startIndex, endIndex, selectPivot(startIndex, endIndex))
        quicksortByEdgeWeight(startIndex, pivotIndex - 1)
        quicksortByEdgeWeight(pivotIndex + 1, endIndex)
    }

    private fun selectPivot(startIndex: Int, endIndex: Int): Int {
        return startIndex
    }

    private fun partition(startIndex: Int, endIndex: Int, pivotIndex: Int): Int {
        val pivotValue = edgeWeights[pivotIndex]

        swapEdges(pivotIndex, endIndex)
        var lowIndex = startIndex

        for (i in startIndex until endIndex) {
            if (edgeWeights[i] < pivotValue) {
                swapEdges(i, lowIndex)
                lowIndex++
            }
        }

        // Pivot element was at endIndex and should be swapped again.
        swapEdges(lowIndex, endIndex)
        return lowIndex
    }

    private fun swapEdges(oneIndex: Int, anotherIndex: Int) {
        if (oneIndex == anotherIndex) {
            return
        }

        val tVertexA = verticesA[oneIndex]
        val tVertexB = verticesB[oneIndex]
        val tDistance = edgeWeights[oneIndex]

        verticesA[oneIndex] = verticesA[anotherIndex]
        verticesB[oneIndex] = verticesB[anotherIndex]
        edgeWeights[oneIndex] = edgeWeights[anotherIndex]

        verticesA[anotherIndex] = tVertexA
        verticesB[anotherIndex] = tVertexB
        edgeWeights[anotherIndex] = tDistance
    }
}