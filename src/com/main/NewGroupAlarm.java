package com.main;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NewGroupAlarm extends Activity {
    Toast mToast;
    private static final int CONTACT_PICKER_RESULT = 1001;
    TimePicker alarm_time = null;
    TextView dataview = null;
    
    Calendar calendar = Calendar.getInstance();
    int hours = calendar.get(Calendar.HOUR);
    int mins = calendar.get(Calendar.MINUTE);
    ArrayList<GroupList> groups = new ArrayList<GroupList>();
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newgroupalarm);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.one_shot_group);
        button.setOnClickListener(mOneShotListener);
        button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        Button new_group = (Button)findViewById(R.id.new_list);
        new_group.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        new_group.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(NewGroupAlarm.this, ContactPickerActivity.class);
                
                startActivityForResult(intent,99);
                
            }
            
        });
    
        
        alarm_time = (TimePicker)findViewById(R.id.timePickerGroup);
        alarm_time.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker arg0, int hour, int min) {
                hours = hour;
                mins = min;
                
            }
            
        });
        dataview = (TextView)findViewById(R.id.groupdata);
        String s = getInput("groups");
        getGroups(s);
        String output = writeGroupsData();
        dataview.setText(output);
        
        ArrayAdapter <CharSequence> adapter =
                new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Basketball Team");
        Spinner spin = (Spinner) findViewById(R.id.spinner1);
        spin.setAdapter(adapter);
        //save(output);
        
    }
    
    private String getInput(String filename) {
        FileInputStream fis;
        String input = "";
        try {
            fis = openFileInput(filename);
            byte[] b = new byte[10000];
            int current = 0;
            fis.read(b);
            input = new String(b);
        } catch (FileNotFoundException e) {
            Button button = (Button)findViewById(R.id.one_shot_group);
            button.setText("error1");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Button button = (Button)findViewById(R.id.one_shot_group);
            button.setText("error2");
            e.printStackTrace();
        }
        Button button = (Button)findViewById(R.id.one_shot_group);
        //button.setText(input);
        return input;
        
    }
    
    private void getGroups(String input) {
        GroupList g = new GroupList("error");
        if (input.length() > 0) {
        String [] lines = input.split("\n");
        
        for (String l : lines) {
           if (l.length() > 0 && Character.getNumericValue(l.charAt(0)) >= 0) {
               g.addContact(l);
           }
           else if (l.length() > 0 && Character.getNumericValue(l.charAt(0)) < 0) {
               if (!g.getName().equals("error")){
               groups.add(g);
               }
               g = new GroupList(l);
           }
        }
        
        }
        
    }
    
    private String writeGroupsData() {
        String output = "";
        if (groups.size() > 0) {
        for (GroupList g : groups) {
            output += g.getName() + "\n";
            
            for (String c : g.getContacts()) {
                output += c + "\n";
            }
        }
        }
        return output;
    }
    
    private void save(String output) {
        String FILENAME = "groups";
 

        
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(output.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    private OnClickListener mOneShotListener = new OnClickListener() {
        public void onClick(View v) {
            // When the alarm goes off, we want to broadcast an Intent to our
            // BroadcastReceiver.  Here we make an Intent with an explicit class
            // name to have our own receiver (which has been published in
            // AndroidManifest.xml) instantiated and called, and then create an
            // IntentSender to have the intent executed as a broadcast.
            
            Intent intent = new Intent(NewGroupAlarm.this, OneShotAlarm.class);
            PendingIntent sender = PendingIntent.getBroadcast(NewGroupAlarm.this,
                    0, intent, 0);

          
            Date date = new Date();
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);
            calendar.set(year, month, day, hours, mins, 0);
            
            

            // Schedule the alarm!
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

            // Tell the user about what we did.
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(NewGroupAlarm.this, "one_shot_scheduled",
                    Toast.LENGTH_LONG);
            mToast.show();
        }
    };

    String person = "";
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case CONTACT_PICKER_RESULT:  
                
                // handle contact results  
                Bundle extras = data.getExtras();  
                Set<String> keys = extras.keySet();  
                Iterator<String> iterate = keys.iterator();  
                String ks = "";
                while (iterate.hasNext()) {  
                    ks  += iterate.next() + "  ";  
                    //extras.toString();
                    person = extras.get("android.intent.extra.shortcut.NAME").toString();
                 
                }  
                
                Uri result = data.getData(); 
                //text.setText(extras.toString());
                break;
            case 99:
                String s = getInput("groups");
                getGroups(s);
                Button new_group = (Button)findViewById(R.id.one_shot_group);
                new_group.setText(groups.toString());
                Bundle extras2 = data.getExtras();  
                ArrayList<String> newGroupNumbers = extras2.getStringArrayList("numbers");
                String newGroupName = extras2.getString("name");
                
                GroupList g = new GroupList(newGroupName);
                g.addAll(newGroupNumbers);
                groups.add(g);
                String o = writeGroupsData();
                new_group.setText(o);
                save(o);
                
          
  
            }  
      
        } else {  
            // gracefully handle failure  
            
        }  
    }
}



