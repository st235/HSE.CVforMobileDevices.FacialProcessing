package github.com.st235.facialprocessing.presentation.screens

sealed class Screen(val route: String) {
    data object Feed: Screen(route = "feed")

    data object Details: Screen("details/{media_id}/{face_id}") {
        const val MEDIA_ID = "media_id"
        const val FACE_ID = "face_id"
        const val FACE_NULL = -1

        fun create(
            mediaId: Long,
            faceId: Int? = null,
        ): String {
            val face = if (faceId == null || faceId < 0) {
                FACE_NULL
            } else {
                faceId
            }
            return "details/$mediaId/$face"
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
}
