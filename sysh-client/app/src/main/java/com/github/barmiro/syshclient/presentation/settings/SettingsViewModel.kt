package com.github.barmiro.syshclient.presentation.settings

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.dataimport.FileStatus
import com.github.barmiro.syshclient.data.common.dataimport.ImportRepository
import com.github.barmiro.syshclient.data.common.dataimport.UploadStatus
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val importRepo: ImportRepository
) : ViewModel() {


    private val _fileStatusList = MutableStateFlow<List<FileStatus>>(emptyList())
    val fileStatusList: StateFlow<List<FileStatus>> = _fileStatusList.asStateFlow()

    private val _zipFileStatus = MutableStateFlow<FileStatus?>(null)
    val zipFileStatus: StateFlow<FileStatus?> = _zipFileStatus

    private fun updateFileStatus(file: File, status: UploadStatus) {
        _fileStatusList.value = _fileStatusList.value.map { entry ->
            if (entry.file == file) {
                entry.copy(status = status)
            } else {
                entry
            }
        }
    }

    fun handleZipFile(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _fileStatusList.value = emptyList()
            val contentResolver = context.contentResolver

            val zipFileName = contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else {
                    "unknown.zip"
                }
            } ?: "unknown.zip"

            val inputStream = contentResolver.openInputStream(uri)

            inputStream?.use { stream ->
                val tempFile = File(context.cacheDir, zipFileName)
                tempFile.outputStream().use { output -> stream.copyTo(output) }

                _zipFileStatus.value = FileStatus(tempFile, UploadStatus.Waiting)

                val extractedFiles = sortJsonFiles(
                    importRepo.extractJsonFiles(tempFile)
                )

//                so that all files don't appear in a single frame
                viewModelScope.launch {
                    for (file:File in extractedFiles) {
                        _fileStatusList.update {
                            it + FileStatus(file, UploadStatus.Waiting)
                        }
                        delay(30)
                    }
                }


                for (jsonFile in extractedFiles) {
                    importRepo.uploadJsonFile(jsonFile).collect { result ->
                        when (result) {
                            is Resource.Success -> updateFileStatus(
                                jsonFile,
                                UploadStatus.Success(
                                    message = result.data ?: "an unknown number of"))
                            is Resource.Error -> updateFileStatus(
                                jsonFile,
                                UploadStatus.Failed(
                                    message = result.message
                                )
                            )
                            is Resource.Loading -> {
                                if (result.isLoading) {
                                    updateFileStatus(
                                        jsonFile,
                                        UploadStatus.Processing
                                    )
                                }
                            }
                        }
                    }
                }

                importRepo.recent().collect { result ->
                    when (result) {
                        is Resource.Success -> _zipFileStatus.value = FileStatus(
                            tempFile,
                            UploadStatus.Success(
                                message = "Processed"
                            )
                        )
                        is Resource.Loading -> _zipFileStatus.value = FileStatus(
                            tempFile,
                            UploadStatus.Processing
                        )
                        is Resource.Error -> _zipFileStatus.value = FileStatus(
                            tempFile,
                            UploadStatus.Failed(
                                message = result.message
                            )
                        )
                    }
                }

            }
        }
    }
}


fun sortJsonFiles(jsonFiles: List<File>): List<File> {
    return jsonFiles.sortedBy { file ->
        file.name
            .substringAfterLast("_")
            .substringBefore(".")
            .toIntOrNull() ?: Int.MAX_VALUE
    }
}