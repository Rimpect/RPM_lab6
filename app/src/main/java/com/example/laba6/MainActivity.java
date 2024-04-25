package com.example.laba6;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "default_channel";
    private Button button;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.setNotificationButton);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); // Установка формата 24-часового времени

        datePicker = findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

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
                        Toast.makeText(MainActivity.this, "Уведомления отключены", Toast.LENGTH_SHORT).show();
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
        // Получаем выбранное время
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        // Проверяем, что время не в прошлом и не отрицательное
        Calendar currentCalendar = Calendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);


        // Проверяем, что дата и время не в прошлом
        if (year < currentYear ||
                (year == currentYear && month < currentMonth) ||
                (year == currentYear && month == currentMonth && day < currentDay) ||
                (year == currentYear && month == currentMonth && day == currentDay && hour < currentHour) ||
                (year == currentYear && month == currentMonth && day == currentDay && hour == currentHour && minute < currentMinute)) {
            Toast.makeText(MainActivity.this, "Дата и время должны быть в будущем", Toast.LENGTH_SHORT).show();
            return;
        }


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); // Устанавливаем секунды в 0, чтобы уведомление срабатывало точно в указанное время

        // Вычисляем задержку до выбранного времени и даты
        long delayMs = calendar.getTimeInMillis() - System.currentTimeMillis();

        // Откладываем отправку уведомления до выбранного времени и даты
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotification();
            }
        }, delayMs);
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