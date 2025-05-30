package com.github.barmiro.syshclient.data.settings.dataimport

import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.CreateUserDTO
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.handleNetworkException
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.common.startup.ImportStatusDTO
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.time.ZoneId
import java.util.concurrent.TimeUnit
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
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .client(client)
        .build()

    val importApi = retrofit.create(ImportApi::class.java)

    fun uploadZipFile(zipFile: File): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val requestFile = zipFile.asRequestBody("application/zip".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = zipFile.name,
                    body = requestFile
                )

                val response = importApi.uploadZip(multipartBody)

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()?.string()))
                } else {
                    emit(Error(response.message()))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
        }
    }

    fun mockZipUpload(uploadID: String?): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            try {

                // temporary workaround
                val response = importApi.mockZipUpload(
                    CreateUserDTO(
                        uploadID ?: "",
                        "",
                        ZoneId.systemDefault().id
                    )
                )

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()?.string()))
                } else {
                    emit(Error(response.message()))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
        }
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


    private val oksse = OkSse(client)
    private val requestUrl = "http://localhost/".toHttpUrl()
    private val serverUrl = userPrefRepo.serverUrlFlow.value?.toHttpUrl()

    var urlBuilder: HttpUrl.Builder = requestUrl.newBuilder()
        .scheme(serverUrl?.scheme ?: requestUrl.scheme)
        .host(serverUrl?.host ?: requestUrl.host)
        .port(serverUrl?.port ?: requestUrl.port)
        .encodedPath("/zipStatusStream")

    var event: ServerSentEvent? = null

    fun startSseConnection(onStatusReceived: (ImportStatusDTO) -> Unit,
                           onDisconnect: () -> Unit,
                           uploadID: String?) {

        // for demo version
        if (uploadID != null) {
            urlBuilder = urlBuilder.addQueryParameter("username", uploadID)
        }
        val request = Request.Builder().url(urlBuilder.build()).build()

        val listener = object : ServerSentEvent.Listener {
            override fun onOpen(sse: ServerSentEvent, response: Response) {
                println("Connection opened")
            }

            override fun onMessage(
                sse: ServerSentEvent,
                id: String?,
                event: String?,
                message: String
            ) {
                if(event == "status") {
                    val status = Json.decodeFromString<ImportStatusDTO>(message)
                    onStatusReceived(status)

                }
            }

            override fun onComment(sse: ServerSentEvent, comment: String) {}
            override fun onRetryTime(sse: ServerSentEvent, milliseconds: Long): Boolean = true
            override fun onRetryError(sse: ServerSentEvent, throwable: Throwable, response: Response?): Boolean = false
            override fun onClosed(sse: ServerSentEvent) {
                onDisconnect()
                println("Connection closed")
            }

            override fun onPreRetry(sse: ServerSentEvent?, originalRequest: Request?): Request {
                TODO("Not yet implemented")
            }

        }
        event = oksse.newServerSentEvent(request, listener)
    }

    fun closeSseConnection() {
        event?.let {
            it.close()
            event = null
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

fun uploadStatusMessageParser(message: String?,
                              placeholder: String,
                              replaceConnectionError: Boolean = false): String {
    return message?.let {
        when {
            it.contains("ENOENT") -> placeholder
            it.contains("ENOSPC") -> "No space left on device"
            replaceConnectionError && it.contains("connect") -> placeholder
            else -> it
        }
    } ?: placeholder
}

