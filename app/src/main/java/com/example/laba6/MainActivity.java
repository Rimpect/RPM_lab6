package com.example.laba6;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "default_channel";
    private static final int NOTIFICATION_DELAY_MS = 5000; // Задержка в 5 секунд

    private Button button;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.setNotificationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем, есть ли разрешение на отображение уведомлений
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Для Android 13 и выше
                    if (getSystemService(NotificationManager.class).areNotificationsEnabled()) {
                        scheduleNotification();
                    } else {
                        // Обработка случая, когда уведомления отключены
                    }
                } else {
                    // Для старых версий Android
                    scheduleNotification();
                }
            }
        });

        // Создаем канал уведомлений
        createNotificationChannel();

        // Инициализируем Handler
        handler = new Handler();
    }

    private void createNotificationChannel() {
        // Создаем канал для уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Регистрируем канал в системе
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification() {
        // Откладываем отправку уведомления на 5 секунд
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotification();
            }
        }, NOTIFICATION_DELAY_MS);
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Уведомление")
                .setContentText("Это уведомление появилось после нажатия кнопки")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Отменяем все задачи в очереди Handler'а при уничтожении активности
        handler.removeCallbacksAndMessages(null);
    }
}