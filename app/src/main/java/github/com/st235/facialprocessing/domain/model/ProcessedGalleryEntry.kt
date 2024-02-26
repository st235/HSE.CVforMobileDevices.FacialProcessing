package github.com.st235.facialprocessing.domain.model

import android.net.Uri

class ProcessedGalleryEntry(
    val id: Long,
    val contentUri: Uri,
    val descriptors: List<FaceDescriptor>
)
