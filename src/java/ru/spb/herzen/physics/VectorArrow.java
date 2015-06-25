package ru.spb.herzen.physics;

import java.awt.*;

/**
 * Класс для отображения вектора со стрелкой
 */
public class VectorArrow {
    private Color color;        //Цвет
    private int x1, y1;         //Экранные координаты начала вектора
    private double length;      //Модуль вектора (в пикселях)
    private double angle;       //Угол поворота вектора (в градусах)

    private int x2, y2;
    private double angleRisk = Math.PI / 7; // Угол между риской и основной линией стрелки
    private double lengthRisk = 10; // Длина риски

    public VectorArrow(int x1, int y1, double length, double angle, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.length = length;
        this.angle = - angle;
        this.color = color;
    }

    public void setAngleRisk(double angle) {
        angleRisk = angle;
    }

    public void setLengthRisk(double length) {
        lengthRisk = length;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        x2 = (int) (x1 + length * Math.cos(angle * Math.PI / 180));
        y2 = (int) (y1 + length * Math.sin(angle * Math.PI / 180));
        g.drawLine(x1, y1, x2, y2);
        double beta = Math.atan2(y1 - y2, x2 - x1);

        int x = (int) Math.round(x2 - lengthRisk * Math.cos(beta + angleRisk));
        int y = (int) Math.round(y2 + lengthRisk * Math.sin(beta + angleRisk));
        g.drawLine(x2, y2, x, y);

        x = (int) Math.round(x2 - lengthRisk * Math.cos(beta - angleRisk));
        y = (int) Math.round(y2 + lengthRisk * Math.sin(beta - angleRisk));
        g.drawLine(x2, y2, x, y);
    }
}
