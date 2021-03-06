package com.main;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.widget.Toast;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 */
public class RepeatingAlarm extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "R.string.repeating_received", Toast.LENGTH_SHORT).show();
    }
}
