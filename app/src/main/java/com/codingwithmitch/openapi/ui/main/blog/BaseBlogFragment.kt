package com.codingwithmitch.openapi.ui.main.blog

import android.content.Context
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.codingwithmitch.openapi.ui.UICommunicationListener
import com.codingwithmitch.openapi.ui.main.blog.list.BlogViewModel

abstract class BaseBlogFragment
constructor(
    @LayoutRes
    private val layoutRes: Int,
): Fragment(layoutRes)
{

    val TAG: String = "AppDebug"

    val viewModel: BlogViewModel by activityViewModels()

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

    }
}