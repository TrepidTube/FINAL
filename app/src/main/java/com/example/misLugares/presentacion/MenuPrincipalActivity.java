package com.example.misLugares.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.misLugares.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView textHora;
    private TextView textSaludo;
    private TextView iconoHora;
    private View rootLayout;
    private Timer timer;

    // Colores reales del cielo por hora del día
    // Madrugada → amanecer → mañana → mediodía → tarde → atardecer → noche
    private static final int[][] COLORES_CIELO = {
            // hora 0-4: noche profunda, azul muy oscuro casi negro
            {0xFF0A0E1A, 0xFF0D1525},   // 00:00
            {0xFF0A0E1A, 0xFF0D1525},   // 01:00
            {0xFF0A0E1A, 0xFF0D1525},   // 02:00
            {0xFF0C1020, 0xFF101828},   // 03:00
            {0xFF0E1428, 0xFF141E35},   // 04:00
            // hora 5: amanecer temprano, azul oscuro con toque rosa
            {0xFF1A1A3A, 0xFF2E2050},   // 05:00
            // hora 6: amanecer, naranja-rosa sobre azul
            {0xFFE8936A, 0xFFB05A8A},   // 06:00
            // hora 7: mañana temprana, naranja suave
            {0xFFEDAA72, 0xFFD4956A},   // 07:00
            // hora 8-10: mañana, cielo azul claro luminoso
            {0xFF87CEEB, 0xFF5BAED4},   // 08:00
            {0xFF72C8EF, 0xFF4AAEE8},   // 09:00
            {0xFF5ABDE8, 0xFF3A9ED8},   // 10:00
            // hora 11-14: mediodía, azul intenso saturado
            {0xFF3FB0E8, 0xFF2090D0},   // 11:00
            {0xFF2FA8E0, 0xFF1880C0},   // 12:00
            {0xFF2FA8E0, 0xFF1880C0},   // 13:00
            {0xFF3AAAE0, 0xFF1E88C8},   // 14:00
            // hora 15-16: tarde, azul más suave
            {0xFF5BB8E0, 0xFF3A9EC8},   // 15:00
            {0xFF6EC0DC, 0xFF4AACC4},   // 16:00
            // hora 17-18: pre-atardecer, melocotón suave
            {0xFFE8B080, 0xFFD08060},   // 17:00
            {0xFFE8986A, 0xFFCC7050},   // 18:00
            // hora 19: atardecer, naranja-rojo
            {0xFFCC6844, 0xFFB84A38},   // 19:00
            // hora 20: crepúsculo, violeta-azul
            {0xFF5A3A6A, 0xFF3A2850},   // 20:00
            // hora 21-23: noche, azul oscuro
            {0xFF1A2440, 0xFF0E1628},   // 21:00
            {0xFF0E1830, 0xFF0A1020},   // 22:00
            {0xFF0A0E1A, 0xFF080C18},   // 23:00
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        textHora   = findViewById(R.id.textHora);
        textSaludo = findViewById(R.id.textSaludo);
        iconoHora  = findViewById(R.id.iconoHora);
        rootLayout = findViewById(R.id.rootLayout);

        actualizarHoraYFondo();

        // Reloj en tiempo real
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                runOnUiThread(MenuPrincipalActivity.this::actualizarHoraYFondo);
            }
        }, 1000, 1000);

        animarEntrada();

        // ── Navegación ────────────────────────────────────────────────────────

        LinearLayout btnSenderismo = findViewById(R.id.btnSenderismo);
        btnSenderismo.setOnClickListener(v -> {
            animarClick(v);
            v.postDelayed(() -> {
                Intent i = new Intent(this, MapaActivity.class);
                i.putExtra("filtro", "NATURALEZA");
                startActivity(i);
            }, 130);
        });

        LinearLayout btnTrazarRutas = findViewById(R.id.btnTrazarRutas);
        btnTrazarRutas.setOnClickListener(v -> {
            animarClick(v);
            v.postDelayed(() -> {
                Intent i = new Intent(this, MapaActivity.class);
                i.putExtra("modo", "TRAZAR_RUTA");
                startActivity(i);
            }, 130);
        });

        LinearLayout btnListaRutas = findViewById(R.id.btnListaRutas);
        btnListaRutas.setOnClickListener(v -> {
            animarClick(v);
            v.postDelayed(() ->
                    startActivity(new Intent(this, MainActivity.class)), 130);
        });
    }

    // ─── Hora y fondo dinámico ────────────────────────────────────────────────

    private void actualizarHoraYFondo() {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);

        // Reloj
        textHora.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));

        // Colores del cielo según la hora exacta
        int colorTop    = COLORES_CIELO[hora][0];
        int colorBottom = COLORES_CIELO[hora][1];

        // Aplicar color de fondo (interpolación simple entre top y bottom)
        // Usamos el color "medio" como fondo sólido del rootLayout
        int colorMedio = blendColors(colorTop, colorBottom, 0.5f);
        rootLayout.setBackgroundColor(colorTop);

        // Saludo y emoji según el período
        if (hora >= 5 && hora < 7) {
            textSaludo.setText("Buenos días ✨");
            iconoHora.setText("🌄");
        } else if (hora >= 7 && hora < 12) {
            textSaludo.setText("Buenos días");
            iconoHora.setText("☀");
        } else if (hora >= 12 && hora < 15) {
            textSaludo.setText("Buenas tardes");
            iconoHora.setText("☀");
        } else if (hora >= 15 && hora < 18) {
            textSaludo.setText("Buenas tardes");
            iconoHora.setText("🌤");
        } else if (hora >= 18 && hora < 20) {
            textSaludo.setText("Buenas tardes");
            iconoHora.setText("🌅");
        } else if (hora >= 20 && hora < 21) {
            textSaludo.setText("Buenas noches");
            iconoHora.setText("🌆");
        } else {
            textSaludo.setText("Buenas noches");
            iconoHora.setText("🌙");
        }

        // Color del texto: blanco siempre, pero opacidad según claridad del fondo
        // De noche el texto ya es blanco y contrasta bien
        // De día (cielo claro) también contrasta por sombra de texto
        if (hora >= 7 && hora < 18) {
            // Día: texto blanco con sombra para contraste sobre cielo claro
            textHora.setTextColor(0xFFFFFFFF);
            textSaludo.setTextColor(0xEEFFFFFF);
        } else {
            // Noche/atardecer: blanco puro
            textHora.setTextColor(0xFFFFFFFF);
            textSaludo.setTextColor(0xDDFFFFFF);
        }
    }

    private int blendColors(int c1, int c2, float ratio) {
        float ir = 1f - ratio;
        int a = (int)((c1 >> 24 & 0xff) * ir + (c2 >> 24 & 0xff) * ratio);
        int r = (int)((c1 >> 16 & 0xff) * ir + (c2 >> 16 & 0xff) * ratio);
        int g = (int)((c1 >> 8  & 0xff) * ir + (c2 >> 8  & 0xff) * ratio);
        int b = (int)((c1       & 0xff) * ir + (c2       & 0xff) * ratio);
        return a << 24 | r << 16 | g << 8 | b;
    }

    // ─── Animaciones ──────────────────────────────────────────────────────────

    private void animarEntrada() {
        int[] ids = {
                R.id.textSaludo,
                R.id.textHora,
                R.id.iconoHora,
                R.id.btnSenderismo,
                R.id.btnTrazarRutas,
                R.id.btnListaRutas
        };
        for (int i = 0; i < ids.length; i++) {
            View v = findViewById(ids[i]);
            if (v == null) continue;
            AnimationSet set = new AnimationSet(true);
            set.setInterpolator(new DecelerateInterpolator(2f));

            AlphaAnimation fade = new AlphaAnimation(0f, 1f);
            fade.setDuration(450);

            TranslateAnimation slide = new TranslateAnimation(0, 0, 40, 0);
            slide.setDuration(450);

            set.addAnimation(fade);
            set.addAnimation(slide);
            set.setStartOffset(i * 90L);
            set.setFillAfter(true);
            v.startAnimation(set);
        }
    }

    private void animarClick(View v) {
        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(90)
                .withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
                ).start();
    }

    // ─── Ciclo de vida ────────────────────────────────────────────────────────

    @Override protected void onDestroy() {
        super.onDestroy();
        if (timer != null) { timer.cancel(); timer = null; }
    }
}