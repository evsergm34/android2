package android2.emelyanovsergey.android2;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public class weatherService extends Service {

    private WeatherBinder weatherBinder = new WeatherBinder();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return weatherBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int timeSleep = intent.getIntExtra(MainActivity.weatherServiceParamTIMESLEEP, 1);
        PendingIntent weatherPI = intent.getParcelableExtra(MainActivity.weatherServiceParamPINTENT);

        WeatherServiceRun weatherServiceRun = new WeatherServiceRun(timeSleep, weatherPI);
        weatherServiceRun.run();

        return super.onStartCommand(intent, flags, startId);
    }


    private class WeatherServiceRun implements Runnable {

        private int timer;
        private PendingIntent weatherPendingIntent;

        public WeatherServiceRun(int timeSleep, PendingIntent weatherPI) {
            this.timer = timeSleep;
            this.weatherPendingIntent = weatherPI;
        }

        @Override
        public void run() {

            try {

                weatherPendingIntent.send(MainActivity.weatherServiceStatusSTART);

                TimeUnit.SECONDS.sleep(timer);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(MainActivity.weatherServiceParamResultCity, "Москва");
                resultIntent.putExtra(MainActivity.weatherServiceParamResultTemp, "30 градусов");

                weatherPendingIntent.send(weatherService.this, MainActivity.weatherServiceStatusFINISH, resultIntent);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        }
    }


    class WeatherBinder extends Binder {
        weatherService getService() {
            return weatherService.this;
        }
    }
}
