package com.ktl.mvvm.ai.Utils

import android.util.Log

class LogUtils {

    companion object{
        val TAG = "Observable"
        @JvmStatic
        fun LogInObservable(msg:String){
            Log.e(TAG,msg)
        }
    }


}