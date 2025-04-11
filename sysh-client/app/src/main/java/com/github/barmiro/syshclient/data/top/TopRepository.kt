package com.github.barmiro.syshclient.data.top

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.domain.top.TopItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .addInterceptor(ServerErrorInterceptor(userPrefRepo))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/top/")
        .client(client)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val topApi = retrofit.create(TopApi::class.java)


    fun getTopTracks(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
        sort: String? = null
    ): Flow<PagingData<TopItemData>> {
        var rangePath= ""
        var startValue = start
        var endValue = end
        if (start == null || end == null) {
            rangePath = "/all"
            startValue = null
            endValue = null
        }

        return Pager(
            config = PagingConfig(
                pageSize = 500,
                prefetchDistance = 150,
                initialLoadSize = 250,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TrackPagingSource(
                    topApi,
                    rangePath,
                    startValue,
                    endValue,
                    sort)
            }
        ).flow
    }

    fun getTopAlbums(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
        sort: String? = null
    ): Flow<PagingData<TopItemData>> {
        var rangePath= ""
        var startValue = start
        var endValue = end
        if (start == null || end == null) {
            rangePath = "/all"
            startValue = null
            endValue = null
        }

        return Pager(
            config = PagingConfig(
                pageSize = 500,
                prefetchDistance = 150,
                initialLoadSize = 250,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AlbumPagingSource(
                    topApi,
                    rangePath,
                    startValue,
                    endValue,
                    sort)
            }
        ).flow
    }

    fun getTopArtists(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
        sort: String? = null
    ): Flow<PagingData<TopItemData>> {
        var rangePath= ""
        var startValue = start
        var endValue = end
        if (start == null || end == null) {
            rangePath = "/all"
            startValue = null
            endValue = null
        }

        return Pager(
            config = PagingConfig(
                pageSize = 500,
                prefetchDistance = 150,
                initialLoadSize = 250,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ArtistPagingSource(
                    topApi,
                    startValue,
                    endValue,
                    sort)
            }
        ).flow
    }
}

