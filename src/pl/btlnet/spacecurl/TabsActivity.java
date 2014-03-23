/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.btlnet.spacecurl;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class TabsActivity extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.tabs);

    // create the TabHost that will contain the Tabs
    TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
            
    TabSpec tab1 = tabHost.newTabSpec("Sensors");
    TabSpec tab2 = tabHost.newTabSpec("Second Tab");
    TabSpec tab3 = tabHost.newTabSpec("Config");
//
//   // Set the Tab name and Activity
//   // that will be opened when particular Tab will be selected
    tab1.setIndicator("Sensors");
    tab1.setContent(new Intent(this,SensorsActivity.class));

    tab2.setIndicator("Empty");
    tab2.setContent(new Intent(this,EmptyActivity.class));

    tab3.setIndicator("Config");
    tab3.setContent(new Intent(this,EmptyActivity.class));
//    
//    /** Add the tabs  to the TabHost to display. */
    tabHost.addTab(tab1);
    tabHost.addTab(tab2);
    tabHost.addTab(tab3);
    }

}
