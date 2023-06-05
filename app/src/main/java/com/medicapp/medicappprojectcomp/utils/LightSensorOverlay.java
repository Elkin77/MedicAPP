package com.medicapp.medicappprojectcomp.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class LightSensorOverlay extends Overlay {
    private float mLightIntensity;

    public LightSensorOverlay(float lightIntensity) {
        mLightIntensity = lightIntensity;
    }

    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // No se requiere ninguna lógica de dibujo aquí
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        // Actualice el color del mapa en respuesta al evento de toque
        updateMapColor(mapView);
        return super.onSingleTapConfirmed(e, mapView);
    }

    private void updateMapColor(MapView mapView) {
        // Calcule el nuevo color del mapa en función del valor del sensor de luminosidad
        int color = calculateMapColor();

        // Establezca el color del mapa utilizando el método `setBackgroundColor` de la clase `MapView`
        mapView.setBackgroundColor(color);
    }

    private int calculateMapColor() {
        // Calcule el nuevo color del mapa en función del valor del sensor de luminosidad
        // Aquí hay un ejemplo simple que establece el color del mapa en función del nivel de luz
        if (mLightIntensity < 10000) {
            return Color.BLACK;
        } else if (mLightIntensity < 15000) {
            return Color.GRAY;
        } else {
            return Color.WHITE;
        }
    }
}