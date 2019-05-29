package android2.emelyanovsergey.android2;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int selectedActionMenuItem = 0;
    private boolean silentModeOn = false;
    private SensorManager sensorManager;
    private List<Sensor> sensors;
    private Sensor sensorTemperature;
    private Sensor sensorHumidity;

    private TextView sensorTemperatureView;
    private TextView sensorHumidityView;
    private MySensorView mySensorView;

    private Button btnWeatherServiceStart;
    private TextView weatherServiceResultView;

    public final static int weatherServiceStatusSTART = 100;
    public final static int weatherServiceStatusFINISH = 101;
    public final static String weatherServiceParamPINTENT = "pendingIntent";
    public final static String weatherServiceParamTIMESLEEP = "timeSleep";
    public final static String weatherServiceParamResultCity = "ResultCity";
    public final static String weatherServiceParamResultTemp = "ResultTemp";

    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        mySensorView = (MySensorView) findViewById(R.id.mySensorView);
        sensorTemperatureView = (TextView) findViewById(R.id.sensorTemperatureView);
        sensorHumidityView = (TextView) findViewById(R.id.sensorHumidityView);

        //Менеджер дачиков
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //все датчики
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        //дачик температуры
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        //в onResume registerSensorListener(sensorTemperature, listenerSensor, true, sensorTemperatureView);

        //дачик влажности
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        // в onResume registerSensorListener(sensorHumidity, listenerSensor, true, sensorHumidityView);


        weatherServiceResultView = (TextView) findViewById(R.id.weatherServiceResult);
        btnWeatherServiceStart = (Button) findViewById(R.id.btnWeatherServiceStart);
        btnWeatherServiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PendingIntent weatherPI = createPendingResult(0, new Intent(), 0);
                serviceIntent = new Intent(MainActivity.this, weatherService.class);
                serviceIntent.putExtra(weatherServiceParamPINTENT, weatherPI);
                serviceIntent.putExtra(weatherServiceParamTIMESLEEP, 5);
                startService(serviceIntent);
            }

            ;
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //bindService( new Intent(MainActivity.this, weatherService.class) )
        //с bindService пока не успел до конца разобраться - разберусь отдельно
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == weatherServiceStatusSTART) {
            weatherServiceResultView.setText("загрузка в службе выполняется");
        }
        if (resultCode == weatherServiceStatusFINISH) {
            String weatherCity = data.getStringExtra(weatherServiceParamResultCity);
            String weatherTemp = data.getStringExtra(weatherServiceParamResultTemp);
            weatherServiceResultView.setText("Погода " + weatherCity + " " + weatherTemp);
            stopService(serviceIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        registerSensorListener(sensorTemperature, listenerSensor, false, sensorTemperatureView);
        registerSensorListener(sensorHumidity, listenerSensor, false, sensorHumidityView);
    }

    private void registerSensorListener(Sensor sensor, SensorEventListener sensorEventListener, boolean regState, TextView sensorView) {
        if (regState) {
            if (sensor != null) {
                sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                sensorView.setText("датчик готов");
            } else {
                if (sensorView != null) sensorView.setText("датчик не обнаружен");
            }
        } else {
            sensorManager.unregisterListener(sensorEventListener);
            if (sensorView != null) sensorView.setText("пауза");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListener(sensorTemperature, listenerSensor, true, sensorTemperatureView);
        registerSensorListener(sensorHumidity, listenerSensor, true, sensorHumidityView);

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
    public boolean onPrepareOptionsMenu(Menu menu) {


        for (int i = 0; i < menu.size(); i++) {
            MenuItem item;
            item = menu.getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            s.setSpan(new ForegroundColorSpan(
                    (selectedActionMenuItem == item.getItemId()) ? Color.RED : Color.BLACK
            ), 0, s.length(), 0);
            item.setTitle(s);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        selectedActionMenuItem = id;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showToast("settings");
            return true;
        }
        if (id == R.id.action_silientmode) {
            showToast("silientmode");
            item.setChecked(!item.isChecked());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(CharSequence text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            showToast("history");
        } else if (id == R.id.nav_alerts) {
            showToast("alerts");
        } else if (id == R.id.nav_about) {
            showToast("about");
        } else if (id == R.id.nav_feedback) {
            showToast("feedback");
        } else if (id == R.id.nav_share) {
            showToast("nav_share");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    SensorEventListener listenerSensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor == sensorTemperature) {
                sensorTemperatureView.setText("Температура:" + sensorEvent.values[0]);
                mySensorView.setTemperature(sensorEvent.values[0] + " С");
                mySensorView.postInvalidate();
            }
            if (sensorEvent.sensor == sensorHumidity) {
                sensorHumidityView.setText("Влажность:" + sensorEvent.values[0]);
                mySensorView.setHumidity(sensorEvent.values[0] + " %");
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}
