package com.sirelon.discover.location.feature.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

open class BaseFragment(
    @LayoutRes
    layoutId: Int
) : Fragment(layoutId) {

    // Just for avoid dublicates
    private var previousError: Throwable? = null

    fun subsribeForErrors(viewModel: BaseViewModel) {
        viewModel.errosLiveData.observe(this) {
            errorHappened(it)
        }
    }

    // Child fragment can ovveride it and do some custom handler
    open fun errorHappened(error: Throwable?) {
        // Skip logic, if root view is null
        val view = view ?: return
        val message = error?.message ?: return
        // We don't want to show the same error again.
        if (previousError?.message == message) {
            return
        }
        previousError = error
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        // Would be nice to implement here "Retry" option.
        snackbar.setAction("Show") {
            AlertDialog.Builder(it.context).setMessage(message).show()
        }
        snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                // Just clear error
                previousError = null
            }
        })
        snackbar.show()
    }

}