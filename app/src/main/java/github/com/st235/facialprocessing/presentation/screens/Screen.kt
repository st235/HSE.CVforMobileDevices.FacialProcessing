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

    data object PersonPhotosFeed: Screen(route = "person/feed/{id}") {
        const val ID = "id"

        fun create(id: String): String {
            return "person/feed/$ID"
        }
    }
}
