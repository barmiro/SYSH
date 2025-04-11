package com.github.barmiro.syshclient.data.top

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.barmiro.syshclient.domain.top.TopItemData
import retrofit2.HttpException
import java.time.LocalDateTime


class TrackPagingSource(
    private val topApi: TopApi,
    private val rangePath: String,
    private val start: LocalDateTime?,
    private val end: LocalDateTime?,
    private val sort: String?
) : PagingSource<Int, TopItemData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopItemData> {
        return try {
            val offset = params.key ?: 0
            val size = params.loadSize

            val response = topApi.fetchTopTracks(
                rangePath = rangePath,
                start = start,
                end = end,
                sort = sort,
                offset = offset,
                size = size
            )

            if (response.isSuccessful) {
                val list = response.body().orEmpty()

                if (list.isEmpty() && offset == 0) {
                    return LoadResult.Error(IllegalStateException("No tracks found"))
                }

                LoadResult.Page(
                    data = list.map{ trackDTO ->
                        trackDTO.toTopItemData()
                    },
                    prevKey = if (offset == 0) null else offset - size,
                    nextKey = if (list.isEmpty()) null else offset + size
                )

            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, TopItemData>): Int? {
//        go back to top on refresh
        return null
    }
}


class AlbumPagingSource(
    private val topApi: TopApi,
    private val rangePath: String,
    private val start: LocalDateTime?,
    private val end: LocalDateTime?,
    private val sort: String?
) : PagingSource<Int, TopItemData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopItemData> {
        return try {
            val offset = params.key ?: 0
            val size = params.loadSize

            val response = topApi.fetchTopAlbums(
                rangePath = rangePath,
                start = start,
                end = end,
                sort = sort,
                offset = offset,
                size = size
            )

            if (response.isSuccessful) {
                val list = response.body().orEmpty()

                if (list.isEmpty() && offset == 0) {
                    return LoadResult.Error(IllegalStateException("No albums found"))
                }

                LoadResult.Page(
                    data = list.map{ albumDTO ->
                        albumDTO.toTopItemData()
                    },
                    prevKey = if (offset == 0) null else offset - size,
                    nextKey = if (list.isEmpty()) null else offset + size
                )

            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, TopItemData>): Int? {
//        go back to top on refresh
        return null
    }
}




class ArtistPagingSource(
    private val topApi: TopApi,
    private val start: LocalDateTime?,
    private val end: LocalDateTime?,
    private val sort: String?
) : PagingSource<Int, TopItemData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopItemData> {
        return try {
            val offset = params.key ?: 0
            val size = params.loadSize

            val response = topApi.fetchTopArtists(
                start = start,
                end = end,
                sort = sort,
                offset = offset,
                size = size
            )

            if (response.isSuccessful) {
                val list = response.body().orEmpty()

                if (list.isEmpty() && offset == 0) {
                    return LoadResult.Error(IllegalStateException("No artists found"))
                }

                LoadResult.Page(
                    data = list.map{ artistDTO ->
                        artistDTO.toTopItemData()
                    },
                    prevKey = if (offset == 0) null else offset - size,
                    nextKey = if (list.isEmpty()) null else offset + size
                )

            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, TopItemData>): Int? {
//        go back to top on refresh
        return null
    }
}