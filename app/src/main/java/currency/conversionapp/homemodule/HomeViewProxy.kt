package currency.conversionapp.homemodule

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import currency.conversionapp.R

import currency.conversionapp.adapter.CurrencyAdapter
import currency.conversionapp.di.HomeContract
import currency.conversionapp.room.CurrencyRate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject


class HomeViewProxy @Inject constructor(
    private val currencySpinnerAdapter: ArrayAdapter<String>,
    private val currencyAdapter: CurrencyAdapter,
    private val gridLayoutManager: GridLayoutManager,
    private val listLayoutManager: LinearLayoutManager,
    private val listenerFactory: ListenerFactory,
    private val toast: Toast,
    override val containerView: View
) : HomeContract.ViewProxy, LayoutContainer {
    @VisibleForTesting
    internal var viewType: CurrencyAdapter.ListViewType = CurrencyAdapter.ListViewType.GRID

    override fun setup(refreshAction: () -> Unit, onItemChange: (Int) -> Unit) {
        recycler_view.apply {
            layoutManager = if (viewType == CurrencyAdapter.ListViewType.GRID) {
                gridLayoutManager
            } else {
                listLayoutManager
            }
            currencyAdapter.setListType(viewType)
            adapter = currencyAdapter
        }
        currency_spinner.adapter = currencySpinnerAdapter
        currency_spinner.onItemSelectedListener =
            listenerFactory.getOnItemSelectedListener(onItemChange)
        user_rate.doOnTextChanged(listenerFactory.getOnTextChanged(this::updateRateText))

        grid_list_button.setOnClickListener {
            if (viewType == CurrencyAdapter.ListViewType.GRID)
                updateRecyclerLayout(CurrencyAdapter.ListViewType.LIST)
            else
                updateRecyclerLayout(CurrencyAdapter.ListViewType.GRID)
        }
        refresh_button.setOnClickListener {
            refreshAction()
        }
    }

    override fun updateList(list: List<CurrencyRate>) {
        currencyAdapter.setAdapterAndRefresh(list)
        currencySpinnerAdapter.run {
            clear()
            addAll(list.map { it.name })
            notifyDataSetChanged()
        }
    }


    override fun updateRateText(rate: Float) {
        currencyAdapter.setCurrentRate(rate)
    }

    override fun updateConversionRate(conversionRate: Float) {
        currencyAdapter.setConversionRate(conversionRate)
    }

    override fun updateRecyclerLayout(viewType: CurrencyAdapter.ListViewType) {
        if (viewType == CurrencyAdapter.ListViewType.GRID) {
            recycler_view.layoutManager = gridLayoutManager
            grid_list_button.run {
                setImageDrawable(
                    resources.getDrawable(
                        R.drawable.list_icon,
                        null
                    )
                )
            }
        } else {
            recycler_view.layoutManager = listLayoutManager
            grid_list_button.run {
                setImageDrawable(
                    resources.getDrawable(
                        R.drawable.grid_icon,
                        null
                    )
                )
            }

        }
        this.viewType = viewType
        currencyAdapter.setListType(viewType)
    }

    override fun selectPosition(selectedPosition: Int) {
        currency_spinner.setSelection(selectedPosition)
    }

    override fun getViewType(): CurrencyAdapter.ListViewType = viewType
    override fun setViewType(viewType: CurrencyAdapter.ListViewType) {
        this.viewType = viewType
    }

    override fun showLoading() {
        progress_bar.isVisible = true
    }

    override fun hideLoading() {
        progress_bar.isVisible = false
    }

    override fun showErrorToast() {
        toast.show()
    }

    override fun showErrorScreen() {
        error_screen.isVisible = true
        recycler_view.isVisible = false
    }

    override fun hideErrorScreen() {
        error_screen.isVisible = false
        recycler_view.isVisible = true
    }
}