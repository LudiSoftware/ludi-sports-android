package io.usys.report.utils.androidx

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun FragmentManager.addFragment(@IdRes containerViewId: Int, fragment: Fragment, tag: String) {
    this.beginTransaction()
        .add(containerViewId, fragment, tag)
        .commit()
}

fun FragmentManager.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment, tag: String) {
    this.beginTransaction()
        .replace(containerViewId, fragment, tag)
        .commit()
}


fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle? = null) {
    findNavController(this).navigate(resId, args)
}

//fun Fragment.goBack() {
//    findNavController(this).popBackStack()
//}

inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    return ViewModelProvider(this)[T::class.java]
}

fun Fragment.hideKeyboard() {
    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Fragment.setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    recyclerView.apply {
        layoutManager = LinearLayoutManager(context)
        this.adapter = adapter
    }
}

fun Fragment.getStringArg(key: String): String? {
    return arguments?.getString(key)
}
fun Fragment.getIntArg(key: String): Int? {
    return arguments?.getInt(key)
}
fun Fragment.getFloatArg(key: String): Float? {
    return arguments?.getFloat(key)
}
fun Fragment.getDoubleArg(key: String): Double? {
    return arguments?.getDouble(key)
}
fun Fragment.getBooleanArg(key: String): Boolean? {
    return arguments?.getBoolean(key)
}
fun Fragment.getParcelableArg(key: String): Bundle? {
    return arguments?.getParcelable(key)
}

