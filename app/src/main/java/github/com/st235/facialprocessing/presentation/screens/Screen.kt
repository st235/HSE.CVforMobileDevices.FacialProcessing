package github.com.st235.facialprocessing.presentation.screens

sealed class Screen(val route: String) {
    data object Feed: Screen(route = "feed")

    data object Details: Screen("details/{media_id}/{cluster_id}") {
        const val MEDIA_ID = "media_id"
        const val CLUSTER_ID = "cluster_id"
        const val CLUSTER_NULL = -1

        fun create(
            mediaId: Long,
            clusterId: Int? = null,
        ): String {
            val cluster = if (clusterId == null || clusterId < 0) {
                CLUSTER_NULL
            } else {
                clusterId
            }
            return "details/$mediaId/$cluster"
        }
    }

    data object Search: Screen(route = "search/{query}") {
        const val QUERY = "query"

        fun create(): String {
            return "search/all"
        }

        fun createForAttribute(id: Int): String {
            return "search/a:$id"
        }

        fun creteForCluster(id: Int): String {
            return "search/c:$id"
        }

        fun getAttributes(query: String): List<Int> {
            return parseSerialisedQuery(query, argument = "a")
        }

        fun getPersonId(query: String): Int? {
            return parseSerialisedQuery(query, argument = "c").firstOrNull()
        }

        private fun parseSerialisedQuery(query: String, argument: String): List<Int> {
            val parts = query.split(':')
            if (parts[0] != argument) {
                return emptyList()
            }
            return parts[1].split(",").map { Integer.parseInt(it) }
        }
    }

    data object Clusters: Screen("clusters")
}
