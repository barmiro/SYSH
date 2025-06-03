package com.github.barmiro.syshclient.data.common.startup

import com.github.barmiro.syshclient.util.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class ImportStatusDTO(
    val zipUploadItem: ZipUploadItem?,
    val jsonInfoList: List<JsonInfo>? = null
)

@Serializable
data class ZipUploadItem(
    val uploadID: String? = null,
    val zipName: String,
    val status: FileProcessingStatus,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val completedOn: ZonedDateTime? = null,
    val message: String? = null
)

@Serializable
data class JsonInfo(
    val filename: String,
    val status: FileProcessingStatus,
    val entriesAdded: Int?
)

@Serializable
enum class FileProcessingStatus {
    WAITING, PREPARING, PROCESSING, FINALIZING, SUCCESS, COMPLETE, ERROR
}