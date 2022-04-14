package com.elmorshdi.internTask.helper

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog

fun alertDialog(
    title: String,
    message: String,
    context: Context,
    myFunction: (Int) -> Unit,
    id: Int
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    //performing positive action
    builder.setPositiveButton("Yes") { _, _ ->
        myFunction(id)
    }
    builder.setNeutralButton("Cancel") { _, _ ->

    }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun alertDialog(
    title: String,
    message: String,
    context: Context,
    myFunction: (View) -> Unit,
    view: View
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    //performing positive action
    builder.setPositiveButton("Yes") { _, _ ->
        myFunction(view)
    }
    builder.setNeutralButton("Cancel") { _, _ ->

    }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

