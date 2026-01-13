package com.mubashshir.lokalmusic.data.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mubashshir.lokalmusic.data.model.Results
import com.mubashshir.lokalmusic.data.remote.SongApiService


class SongPagingSource(
    private val songApiService: SongApiService,
    private val query: String,
    private val name: String? = null
) : PagingSource<Int, Results>()
{


    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Results>
    {


        return try
        {
            val currentPage = params.key ?: 1
            val response = songApiService.searchSongs(
                query = query,
                name = name,
                page = currentPage,
                limit = params.loadSize
            )


            val songs = response.data.results


            LoadResult.Page(
                data = songs,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (songs.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception)
        {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(
        state: PagingState<Int, Results>
    ): Int?
    {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(
                    1
                )
        }
    }
}

