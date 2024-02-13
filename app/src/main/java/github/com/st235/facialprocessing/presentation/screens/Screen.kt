package github.com.st235.facialprocessing.presentation.screens

sealed class Screen(val route: String) {
    data object ClusteringFeed: Screen(route = "main/feed")

    data object PersonPhotosFeed: Screen(route = "person/feed/{id}") {
        const val ID = "id"

        fun create(id: String): String {
            return "person/feed/$ID"
        }
    }

    data object PhotoDetails: Screen("person/details")


}
