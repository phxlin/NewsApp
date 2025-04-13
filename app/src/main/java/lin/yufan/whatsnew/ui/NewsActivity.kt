package lin.yufan.whatsnew.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import lin.yufan.whatsnew.R
import lin.yufan.whatsnew.data.local.ArticleDatabase
import lin.yufan.whatsnew.data.remote.RetrofitInstance
import lin.yufan.whatsnew.data.repository.NewsRepositoryImp
import lin.yufan.whatsnew.databinding.ActivityNewsBinding
import lin.yufan.whatsnew.ui.viewmodel.NewsViewModel

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            NewsViewModelProviderFactory(
                NewsRepositoryImp(
                    RetrofitInstance.api,
                    ArticleDatabase(this).getArticleDao()
                )
            )
        )[NewsViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}