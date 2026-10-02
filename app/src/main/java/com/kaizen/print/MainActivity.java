package com.kaizen.print;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.neptunelite.api.NeptuneLiteUser;

public class MainActivity extends Activity {
    private IPrinter printer;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createUI();
        connectPrinter();
    }

    private void createUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(40, 60, 40, 40);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("KAIZEN PRINT");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView device = new TextView(this);
        device.setText("\nPAX E800\nAndroid 6.0.1\n");
        device.setTextSize(16);
        device.setGravity(Gravity.CENTER);
        root.addView(device);

        status = new TextView(this);
        status.setText("Printer: Checking...");
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        root.addView(status);

        Button testButton = new Button(this);
        testButton.setText("TEST PRINT");
        root.addView(testButton);

        Button feedButton = new Button(this);
        feedButton.setText("FEED PAPER");
        root.addView(feedButton);

        Button cutButton = new Button(this);
        cutButton.setText("CUT PAPER");
        root.addView(cutButton);

        testButton.setOnClickListener(v -> testPrint());
        feedButton.setOnClickListener(v -> feed());
        cutButton.setOnClickListener(v -> cut());

        setContentView(root);
    }

    private void connectPrinter() {
        try {
            IDAL dal = NeptuneLiteUser.getInstance().getDal(getApplicationContext());
            printer = dal.getPrinter();
            status.setText("Printer: CONNECTED");
        } catch (Exception e) {
            status.setText("Printer connection failed\n" + String.valueOf(e.getMessage()));
        }
    }

    private void testPrint() {
        if (printer == null) {
            toast("Printer not connected");
            return;
        }
        try {
            printer.init();
            printer.printStr(
                    "\n\n          KAIZEN\n\n" +
                    "------------------------------\n" +
                    "       PRINTER TEST OK\n" +
                    "------------------------------\n\n" +
                    "Device : PAX E800\n" +
                    "Bridge : Kaizen Print\n" +
                    "Version: 0.1\n\n" +
                    "       SUCCESS\n\n\n",
                    null
            );
            printer.step(80);
            int result = printer.start();
            toast(result == 0 ? "Print successful" : "Printer result: " + result);
        } catch (Exception e) {
            toast("Print error: " + String.valueOf(e.getMessage()));
        }
    }

    private void feed() {
        if (printer == null) {
            toast("Printer not connected");
            return;
        }
        try {
            printer.init();
            printer.step(120);
            printer.start();
        } catch (Exception e) {
            toast(String.valueOf(e.getMessage()));
        }
    }

    private void cut() {
        if (printer == null) {
            toast("Printer not connected");
            return;
        }
        try {
            printer.cutPaper(0);
            toast("Cut command sent");
        } catch (Exception e) {
            toast("Cutter API unavailable: " + String.valueOf(e.getMessage()));
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
