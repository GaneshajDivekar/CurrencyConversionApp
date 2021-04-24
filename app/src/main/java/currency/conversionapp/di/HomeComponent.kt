package currency.conversionapp.di

import android.view.View
import currency.conversionapp.HomeActivity
import currency.conversionapp.network.HomeApi
import currency.conversionapp.room.CurrencyDao
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [HomeModule::class]
)
interface HomeComponent {
    @Component.Builder
    interface Builder {
        fun build(): HomeComponent

        @BindsInstance
        fun homeApi(homeApi: HomeApi): Builder

        @BindsInstance
        fun view(view: View): Builder

        @BindsInstance
        fun databaseDao(currencyDao: CurrencyDao): Builder
    }

    fun inject(activity: HomeActivity)
}