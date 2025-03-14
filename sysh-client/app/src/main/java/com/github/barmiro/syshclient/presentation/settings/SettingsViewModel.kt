package com.github.barmiro.syshclient.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.dataimport.ImportRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val importRepo: ImportRepository
) : ViewModel() {


    fun handleZipFile(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            inputStream?.use { stream ->
                val tempFile = File(context.cacheDir, "temp.zip")
                tempFile.outputStream().use { output -> stream.copyTo(output) }

                importRepo.processZip(tempFile).collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let {
                                println(it)
                            }
                        }
                        is Resource.Error -> {
                            println(result.message + result.data)
                        }
                        is Resource.Loading -> {
                        }
                    }
                }
            }
        }
    }
}