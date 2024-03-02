package github.com.st235.facialprocessing.domain.clustering.hdbscan

data class Constraint(
    val pointA: Int,
    val pointB: Int,
    val type: Type,
) {
    enum class Type {
        MUST_LINK,
        CANNOT_LINK,
    }
}