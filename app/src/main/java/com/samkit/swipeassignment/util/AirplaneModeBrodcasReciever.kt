package com.samkit.swipeassignment.util
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AirplaneModeBroadcastReciever: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val isAirplaneModeEnabled = intent?.getBooleanExtra("state",false)?:return
        if(isAirplaneModeEnabled){
            Toast.makeText(context,"enabled", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context,"disabled", Toast.LENGTH_SHORT).show()
        }
    }

}