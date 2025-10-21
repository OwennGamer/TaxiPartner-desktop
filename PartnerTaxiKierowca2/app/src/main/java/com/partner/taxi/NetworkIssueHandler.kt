package com.partner.taxi

import android.content.Context
import android.util.Log
import android.widget.Toast

private const val NETWORK_ISSUE_TAG = "NetworkIssue"

fun Context.showConnectionIssueToast(throwable: Throwable) {
    Log.w(NETWORK_ISSUE_TAG, "Problem z połączeniem. Sesja pozostaje aktywna.", throwable)
    Toast.makeText(
        this,
        getString(R.string.connection_issue_retry),
        Toast.LENGTH_LONG
    ).show()
}