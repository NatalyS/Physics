package ru.spb.herzen.physics;

import java.awt.*;
import java.util.HashMap;

public class Vectors {
    public static final int G = 0; // Ускорение свободного падения
    public static final int A = 1; // Ускорение
    public static final int FG = 2; // Сила тяжести
    public static final int FR = 3; // Сила сопротивления
    public static final int V = 4; // Скорость
    private VectorArrow arrow;
    private PObject object;
    private int coef = 2; // для дополнительного увеличения стрелки
    private int[] shiftXArrow = {400, 300, 0, 0, 150};

    private int[] shiftXText = {405, 305, 10, 10, 155};

    private HashMap<Integer, Integer> shiftY = new HashMap<>();

    Vectors(PObject object) {
        this.object = object;
    }

    private String getText(int type, PObject object, int timestamp) {
        switch (type) {
            case G:
                return "g = " + String.format("%.2f", object.constants.getAccelerationOfGravity()) + " м/с2";
            case A:
                return "a = " + String.format("%.2f", object.dataBase.accelerations.get(timestamp).getValue()) + " м/с2";
            case FG:
                return "Fтяж. = " + String.format("%.2f", object.constants.getAccelerationOfGravity() * object.getMass()) + " Н";
            case FR:
                return "Fсопр. = " + String.format("%.2f", object.dataBase.resistanceForces.get(timestamp).getValue()) + " Н";
            case V:
                return "V = " + String.format("%.2f", object.dataBase.velocities.get(timestamp).getValue()) + " м/с";
        }
        return "";
    }

    private double getValue(int type, PObject object, int timestamp) {
        switch (type) {
            case G:
                return object.constants.getAccelerationOfGravity();
            case A:
                return object.dataBase.accelerations.get(timestamp).getValue();
            case FG:
                return object.constants.getAccelerationOfGravity() * object.getMass();
            case FR:
                return object.dataBase.resistanceForces.get(timestamp).getValue();
            case V:
                return object.dataBase.velocities.get(timestamp).getValue();
        }
        return 0;
    }

    public void setObject(PObject object) {
        this.object = object;
    }

    private void drawVector(Model model, PObject object, Graphics g, Color color, int type, int timestamp) {
        g.setColor(color);
        Point p = model.getCoordinateConverter().toScreen(object.getCenter());

        arrow = new VectorArrow((int) p.getX() + shiftXArrow[type], (int) p.getY(), coef * getValue(type, object, timestamp), -90, color);
        arrow.draw(g);

        if (!shiftY.containsKey(new Integer(shiftXText[type]))) {
            shiftY.put(shiftXText[type], 0);
        } else {
            shiftY.put(shiftXText[type], shiftY.get(shiftXText[type]) + 20);
        }
        g.drawString(getText(type, object, timestamp), (int) p.getX() + shiftXText[type], (int) p.getY() + shiftY.get(shiftXText[type]));
    }


    public void drawG(Model model, PObject object, Graphics g, Color color, int timestamp) {
        drawVector(model, object, g, color, G, timestamp);
    }

    public void drawGravityForce(Model model, PObject object, Graphics g, Color color, int timestamp) {
        drawVector(model, object, g, color, FG, timestamp);
    }

    public void drawVelocity(Model model, PObject object, Graphics g, Color color, int timestamp) {
        drawVector(model, object, g, color, V, timestamp);
    }

    public void drawAcceleration(Model model, PObject object, Graphics g, Color color, int timestamp) {
        drawVector(model, object, g, color, A, timestamp);
    }

    public void drawResistanceForce(Model model, PObject object, Graphics g, Color color, int timestamp) {
        drawVector(model, object, g, color, FR, timestamp);
    }

    public void drawAll(Model model, PObject object, Graphics g, Color color) {
        drawAll(model, object, g, color, object.getLastTimestamp());
    }

    public void drawAll(Model model, PObject object, Graphics g, Color color, int timestamp) {
        shiftY = new HashMap<>();

        drawG(model, object, g, color, timestamp);
        drawResistanceForce(model, object, g, color, timestamp);
        drawGravityForce(model, object, g, color, timestamp);
        drawVelocity(model, object, g, color, timestamp);
        drawAcceleration(model, object, g, color, timestamp);
    }
}
