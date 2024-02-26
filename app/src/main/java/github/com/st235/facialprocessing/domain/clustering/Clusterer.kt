package github.com.st235.facialprocessing.domain.clustering

interface Clusterer<V> {

    fun cluster(points: List<V>): List<Set<V>>

}