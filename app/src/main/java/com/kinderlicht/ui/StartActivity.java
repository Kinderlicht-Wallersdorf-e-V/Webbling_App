package com.kinderlicht.ui;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kinderlicht.json.Member;
import com.kinderlicht.json.Parser;
import com.kinderlicht.sql.Connector;
import com.kinderlicht.ui.Util;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener, BirthdayFragment.OnFragmentInteractionListener,
        TodoFragment.OnFragmentInteractionListener, NewsletterFragment.OnFragmentInteractionListener, DonationFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {


    private Connector connector;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences  = this.getPreferences(this.MODE_PRIVATE);
        //Util.onActivityCreateSetTheme(this);
        int theme =  sharedPreferences.getInt("Theme", 0); // R.style.AppTheme_RED
        System.out.println(theme);
        switch (theme){
            case 0:
                setTheme(R.style.AppTheme);
                break;
            case 1:
                setTheme(R.style.AppThemeDark);
                break;
        }

        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        try {
            Menu menuNav = navigationView.getMenu();
            MenuItem menuItem = menuNav.findItem(R.id.nav_Overview);

            FragmentManager fragMan = getSupportFragmentManager();
            fragMan.beginTransaction().replace(R.id.frag_lay, MainFragment.class.newInstance())
                    .addToBackStack("a")
                    .commit();
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        //requestPermissions(permissions, WRITE_REQUEST_CODE);

        verifyStoragePermissions(this);


        init();
        weblingImportData();
        createNotificationChannel();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        connector.triggerTempDelete();

        System.out.println("Entitys in db: " + connector.getData().getCount());
        super.onDestroy();
    }

    private final int WRITE_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted.
                    //Continue with writing files...
                } else {
                    //Permission denied.
                }
                break;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment frag = null;
        Class fragmentClass;
        int id = item.getItemId();

        if (id == R.id.nav_Overview) {
            // Handle the camera action
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_Birthdays) {
            fragmentClass = BirthdayFragment.class;
        } else if (id == R.id.nav_ToDo) {
            fragmentClass = TodoFragment.class;
        } else if (id == R.id.nav_Newsletter) {
            fragmentClass = NewsletterFragment.class;
        } else if (id == R.id.nav_Settings) {
            fragmentClass = SettingsFragment.class;
        } else if (id == R.id.nav_Donations) {
            fragmentClass = DonationFragment.class;
        }  else {
            fragmentClass = MainFragment.class;
        }

        try {
            frag = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragMan = getSupportFragmentManager();
        fragMan.beginTransaction().replace(R.id.frag_lay, frag)
                .addToBackStack("a")
                .commit();

        item.setChecked(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        connector = new Connector(this);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public Connector getConnector() {
        return connector;
    }


    public void weblingImportData() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://kinderlichtwdorf.webling.eu/api/1/member?format=full&apikey=eaab12f49595f7d8ca8a938cf0d082ec";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = response.toString();
                System.out.println(output);
                ArrayList<Member> list = Parser.createMembers(output);
                System.out.println(list.size());
                Toast.makeText(getApplicationContext(), "Fetched", Toast.LENGTH_SHORT).show();
                for (Member mem : list) {
                    connector.addMemberData(mem);
                }
                Toast.makeText(getApplicationContext(), "Imported", Toast.LENGTH_SHORT).show();

                Cursor c = connector.getDataCount();
                if (c.getCount() >= 1) {
                    while (c.moveToNext()) {
                        Toast.makeText(getApplicationContext(), c.getString(0), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Hasn't worked", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }

    private static final String CHANNEL_ID = "1";

    public void triggerNotification() {
        Intent intent = new Intent(this, StartActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_base)
                .setContentTitle("Hey dickhead, I'm doing something")
                .setContentText("Paninihead")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(11, builder.build());
    }

    public void switchTheme(){
        //Util.changeToTheme(StartActivity.this, 1);
        SharedPreferences sharedPreferences = this.getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Theme", 1);
        editor.commit();
        this.recreate();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test";
            String description = "This is only a test channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public FloatingActionButton getFab() {
        return fab;
    }
}
