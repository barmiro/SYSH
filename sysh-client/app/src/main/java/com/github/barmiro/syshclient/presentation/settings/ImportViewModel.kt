package com.github.barmiro.syshclient.presentation.settings

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.startup.FileProcessingStatus
import com.github.barmiro.syshclient.data.common.startup.ImportStatusDTO
import com.github.barmiro.syshclient.data.common.startup.ZipUploadItem
import com.github.barmiro.syshclient.data.settings.dataimport.ImportRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val importRepo: ImportRepository
) : ViewModel() {

    private val _importStatus: MutableStateFlow<ImportStatusDTO?> = MutableStateFlow(null)
    val importStatus: StateFlow<ImportStatusDTO?> = _importStatus

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun startImportStateSseConnection() {
        importRepo.startSseConnection(
            onStatusReceived = { status ->
                _isConnected.value = true
                _importStatus.value = status
            },
            onDisconnect = {
                _isConnected.value = false
            }
        )
    }

    fun handleZipFile(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
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

                try {
                    tempFile.outputStream().use { output ->
                       stream.copyTo(output)
                    }

                    if (!_isConnected.value) {
                        startImportStateSseConnection()
                    }

                    importRepo.uploadZipFile(tempFile).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _importStatus.value = ImportStatusDTO(
                                    ZipUploadItem(
                                        zipName = zipFileName,
                                        status = FileProcessingStatus.PREPARING
                                    )
                                )
                            }
                            is Resource.Loading -> {
                                _importStatus.value = ImportStatusDTO(
                                    ZipUploadItem(
                                        zipName = zipFileName,
                                        status = FileProcessingStatus.WAITING
                                    )
                                )
                            }
                            is Resource.Error -> {
                                _importStatus.value = ImportStatusDTO(
                                    ZipUploadItem(
                                        zipName = zipFileName,
                                        status = FileProcessingStatus.ERROR
                                    )
                                )
                                _errorMessage.value = result.message
                            }
                        }
                    }
                } catch (e: Exception) {
                    _importStatus.value = ImportStatusDTO(
                        ZipUploadItem(
                            zipName = zipFileName,
                            status = FileProcessingStatus.ERROR
                        )
                    )
                    _errorMessage.value = e.message
                } finally {
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                }
            }

        }
    }
}