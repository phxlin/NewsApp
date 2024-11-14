package lin.yufan.newsapp.data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import lin.yufan.newsapp.data.remote.NewsApi
import lin.yufan.newsapp.data.remote.inValidGetBreakingNewsResponse
import lin.yufan.newsapp.data.remote.validGetBreakingNewsResponse
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NewsRepositoryImpTest {

    private lateinit var repository: NewsRepositoryImp
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var api: NewsApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        okHttpClient = OkHttpClient.Builder()
            .writeTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build()
        api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(NewsApi::class.java)
        repository = NewsRepositoryImp(
            api = api,
            dao = mockk(relaxed = true)
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Test if getting breaking news with a valid response returns success`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(validGetBreakingNewsResponse)
        )
        val result = repository.getBreakingNews("us", 1)

        assertThat(result.isSuccessful).isTrue()
    }

    @Test
    fun `Test if getting breaking news with an invalid response returns failure`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(403)
                .setBody(inValidGetBreakingNewsResponse)
        )
        val result = repository.getBreakingNews("", 1)

        assertThat(result.isSuccessful).isFalse()
    }
}