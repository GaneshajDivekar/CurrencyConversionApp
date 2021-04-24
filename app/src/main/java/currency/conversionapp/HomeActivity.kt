package currency.conversionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import currency.conversionapp.di.DaggerHomeComponent
import currency.conversionapp.di.HomeContract
import currency.conversionapp.network.HomeApi
import currency.conversionapp.network.RetrofitApiClient
import currency.conversionapp.room.AppDatabase
import okhttp3.OkHttpClient
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var presenter: HomeContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))
        DaggerHomeComponent.builder()
            .homeApi(
                RetrofitApiClient(
                    OkHttpClient.Builder().build()
                )
                    .getApiInstance(HomeApi::class.java)
            )
            .view(findViewById<View>(android.R.id.content).rootView)
            .databaseDao(AppDatabase.getDatabase(application).currencyDao())
            .build()
            .inject(this)
        if (savedInstanceState != null) {
            presenter.onRestoreInstanceState(savedInstanceState)
        }
        presenter.onCreateActivity()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        presenter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}