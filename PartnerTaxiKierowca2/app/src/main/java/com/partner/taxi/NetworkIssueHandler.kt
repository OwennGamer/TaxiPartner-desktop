package com.partner.taxi

import android.content.Context
import android.util.Log
import android.widget.Toast

private const val NETWORK_ISSUE_TAG = "NetworkIssue"

fun Context.showConnectionIssueToast(throwable: Throwable) {
    Log.w(NETWORK_ISSUE_TAG, "Problem z połączeniem. Sesja pozostaje aktywna.", throwable)
    RemoteLogService.logWarning(
        summary = "Problem z połączeniem z API",
        details = throwable.message,
        metadata = mapOf(
            "exception" to throwable.javaClass.name,
            "cause" to (throwable.cause?.javaClass?.name ?: ""),
            "thread" to Thread.currentThread().name
        )
    )
    Toast.makeText(
        this,
        getString(R.string.connection_issue_retry),
        Toast.LENGTH_LONG
    ).show()
}