package com.elmorshdi.internTask.view.util

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.helper.toast
import com.elmorshdi.internTask.view.adapter.HorizontalProductAdapter

@BindingAdapter("loadImageUrl")
fun loadImage(imageView: ImageView, url: String?) {

    if (!url.isNullOrBlank()) {
        Glide.with(imageView)
            .load(url)
            .into(imageView)
    }
}


@BindingAdapter("ProductsListHor")
fun bindProductsListHor(recyclerView: RecyclerView, list: List<Product>?) {
    list?.let { (recyclerView.adapter as HorizontalProductAdapter).submitList(list) }

}

@BindingAdapter("showToast")
fun showToast(view: View, msg: String?) {
    if (!msg.isNullOrBlank()) {
        view.context.toast(msg)

    }
}

@BindingAdapter("setQuantity")
fun setQuantity(textView: AppCompatTextView, quantity: String?) {
    val text = StringBuilder().append("Quantity:").append(quantity).toString()
    textView.text = text
}

