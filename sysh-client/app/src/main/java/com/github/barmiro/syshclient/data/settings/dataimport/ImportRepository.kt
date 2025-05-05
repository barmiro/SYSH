package com.github.barmiro.syshclient.data.settings.dataimport

import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.handleNetworkException
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .addInterceptor(ServerErrorInterceptor(userPrefRepo))
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .build()

    val importApi = retrofit.create(ImportApi::class.java)


    fun extractJsonFiles(zipFile: File): List<File> {
        val extractedFiles = mutableListOf<File>()
        val zipInputStream = ZipInputStream(FileInputStream(zipFile))
        zipInputStream.use { inputStream ->
            var entry = inputStream.nextEntry
            while (entry != null) {
//                this is very fragile
                if (entry.name.matches(Regex(".*/Streaming_History_Audio.*\\.json"))) {
                    val outputFile = File(zipFile.parent, File(entry.name).name)
                    outputFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                    extractedFiles.add(outputFile)
                }
                entry = inputStream.nextEntry
            }
        }
        return extractedFiles
    }


    fun uploadJsonFile(jsonFile: File): Flow<Resource<Int>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val requestBody = jsonFile.readBytes()
                    .toRequestBody(
                        "application/json"
                            .toMediaTypeOrNull())

                val response = importApi.addJson(requestBody)

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()?.string()?.toInt())) //TODO: make int native
                } else {
                    emit(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
        }
    }


    fun recent(): Flow<Resource<String>> {
        return flow{
            emit(Resource.Loading(true))
            try {
                val response = importApi.recent()

                if (response.isSuccessful) {
                    emit(Resource.Success(""))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
        }
    }
}

data class FileStatus(
    val file: File,
    val status: UploadStatus
)

sealed class UploadStatus {
    object Waiting : UploadStatus()
    object Processing : UploadStatus()
    data class Success(val message: Int) : UploadStatus()
    data class Failed(val message: String?) : UploadStatus()
}

