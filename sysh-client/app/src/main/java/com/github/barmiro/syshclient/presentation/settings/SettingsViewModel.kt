package com.github.barmiro.syshclient.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.dataimport.FileStatus
import com.github.barmiro.syshclient.data.common.dataimport.ImportRepository
import com.github.barmiro.syshclient.data.common.dataimport.UploadStatus
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val importRepo: ImportRepository
) : ViewModel() {


    private val _fileStatusList = MutableStateFlow<List<FileStatus>>(emptyList())
    val fileStatusList: StateFlow<List<FileStatus>> = _fileStatusList.asStateFlow()

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
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            inputStream?.use { stream ->
                val tempFile = File(context.cacheDir, "temp.zip")
                tempFile.outputStream().use { output -> stream.copyTo(output) }

                val extractedFiles = sortJsonFiles(
                    importRepo.extractJsonFiles(tempFile)
                )

                _fileStatusList.value = extractedFiles.map { file ->
                    FileStatus(file, UploadStatus.Waiting)
                }

                println(fileStatusList.value)


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