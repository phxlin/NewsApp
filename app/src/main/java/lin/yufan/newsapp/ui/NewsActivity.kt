package lin.yufan.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import lin.yufan.newsapp.R
import lin.yufan.newsapp.data.local.ArticleDatabase
import lin.yufan.newsapp.data.remote.RetrofitInstance
import lin.yufan.newsapp.data.repository.NewsRepositoryImp
import lin.yufan.newsapp.ui.viewmodel.NewsViewModel

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        viewModel = ViewModelProvider(
            this,
            NewsViewModelProviderFactory(
                NewsRepositoryImp(
                    RetrofitInstance.api,
                    ArticleDatabase(this).getArticleDao()
                )
            )
        )[NewsViewModel::class.java]

        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setupWithNavController(
            findViewById<FragmentContainerView>(R.id.newsNavHostFragment).findNavController()
        )
    }
}