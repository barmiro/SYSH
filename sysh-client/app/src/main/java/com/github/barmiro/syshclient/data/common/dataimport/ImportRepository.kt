package com.github.barmiro.syshclient.data.common.dataimport

import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.ConnectException
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:5754/")
        .client(client)
//        .addConverterFactory(
//            Json.asConverterFactory(
//                "application/json; charset=UTF8".toMediaType()))
        .build()

    val importApi = retrofit.create(ImportApi::class.java)


    fun processZip(zipFile: File): Flow<Resource<String>> = flow {
            val extractedFiles = extractJsonFiles(zipFile)
            println("processZip")
            for (jsonFile in extractedFiles) {
                println("jsonFile")
                uploadJsonFile(jsonFile).collect { result ->
                    emit(result)
                }
            }
    }

    private fun extractJsonFiles(zipFile: File): List<File> {
        val extractedFiles = mutableListOf<File>()
        val zipInputStream = ZipInputStream(FileInputStream(zipFile))
        println("extract")
        zipInputStream.use { inputStream ->
            var entry = inputStream.nextEntry
            while (entry != null) {
//                this is very fragile
                println(entry.name)
                if (entry.name.matches(Regex(".*/Streaming_History_Audio.*\\.json"))) {
                    val outputFile = File(zipFile.parent, File(entry.name).name)
                    outputFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                    println("valid entry")
                    extractedFiles.add(outputFile)
                }
                entry = inputStream.nextEntry
            }
        }
        return extractedFiles
    }


    private fun uploadJsonFile(jsonFile: File): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val requestBody = jsonFile.readBytes()
                    .toRequestBody(
                        "application/json"
                            .toMediaTypeOrNull())

                val response = importApi.addJson(requestBody)

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()?.string()))
                }

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                emit(Resource.Error("ConnectException:\n" + e.message))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Exception:\n" + e.message))
            }
        }
    }


}