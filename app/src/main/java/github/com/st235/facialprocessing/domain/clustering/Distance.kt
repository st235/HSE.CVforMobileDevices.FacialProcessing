package github.com.st235.facialprocessing.domain.clustering

interface Distance<V> {

    fun calculateDistance(one: V, another: V): Double

}
