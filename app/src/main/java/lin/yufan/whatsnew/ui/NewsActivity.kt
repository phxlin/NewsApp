package lin.yufan.whatsnew.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import lin.yufan.whatsnew.R
import lin.yufan.whatsnew.data.local.ArticleDatabase
import lin.yufan.whatsnew.data.remote.RetrofitInstance
import lin.yufan.whatsnew.data.repository.NewsRepositoryImp
import lin.yufan.whatsnew.ui.viewmodel.NewsViewModel

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