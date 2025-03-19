package com.github.barmiro.syshclient.data.top

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.domain.top.TopAlbum
import com.github.barmiro.syshclient.domain.top.TopArtist
import com.github.barmiro.syshclient.domain.top.TopTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
        .addInterceptor(JwtInterceptor(userPrefRepo))
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
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<PagingData<TopTrack>> {
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
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<PagingData<TopAlbum>> {
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
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<PagingData<TopArtist>> {
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

