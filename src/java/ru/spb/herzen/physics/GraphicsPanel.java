package ru.spb.herzen.physics;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;

/**
    Класс, отображающий анализ движения заданного объекта
 */
public class GraphicsPanel extends JPanel {

    public GraphicsPanel(PObject object, int timeInMilliSeconds, int dividerTime, PObject earth) {
        super();

        PGraphics graphics = new PGraphics();

        object.recordDistanceTravelled(timeInMilliSeconds);
        object.recordDistanceToObject(timeInMilliSeconds, earth, object.dataBase.distanceToEarth);

        DataBase dataBase = object.dataBase;

        XYSeries seriesS_ = new XYSeries("L(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesS_.add((double) dataBase.times.get(i) / dividerTime, dataBase.distanceTravelled.get(dataBase.times.get(i)));
        }
        ChartPanel distanceTravelledPanel = new ChartPanel(graphics.getChart(seriesS_, "Зависимость пройденного пути от времени", "t", "L"));

        XYSeries seriesS = new XYSeries("S(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesS.add((double) dataBase.times.get(i) / dividerTime, dataBase.shifts.get(dataBase.times.get(i)).getValue());
        }
        ChartPanel shiftPanel = new ChartPanel(graphics.getChart(seriesS, "Зависимость перемещения от времени", "t", "S"));

        XYSeries seriesSx = new XYSeries("Sx(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesSx.add((double) dataBase.times.get(i) / dividerTime, dataBase.shifts.get(dataBase.times.get(i)).getX());
        }
        ChartPanel shiftXPanel = new ChartPanel(graphics.getChart(seriesSx, "Зависимость проекции перемещения на ось X от времени", "t", "Sx"));

        XYSeries seriesSy = new XYSeries("Sy(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesSy.add((double) dataBase.times.get(i) / dividerTime, dataBase.shifts.get(dataBase.times.get(i)).getY());
        }
        ChartPanel shiftYPanel = new ChartPanel(graphics.getChart(seriesSy, "Зависимость проекции перемещения на ось Y от времени", "t", "Sy"));


        XYSeries seriesV = new XYSeries("V(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesV.add((double) dataBase.times.get(i) / dividerTime, dataBase.velocities.get(dataBase.times.get(i)).getValue());
        }
        ChartPanel velocityPanel = new ChartPanel(graphics.getChart(seriesV, "Зависимость скорости от времени", "t", "V"));

        XYSeries seriesVx = new XYSeries("Vx(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesVx.add((double) dataBase.times.get(i) / dividerTime, dataBase.velocities.get(dataBase.times.get(i)).getX());
        }
        ChartPanel velocityXPanel = new ChartPanel(graphics.getChart(seriesVx, "Зависимость проекции скорости на ось X от времени", "t", "Vx"));

        XYSeries seriesVy = new XYSeries("Vy(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesVy.add((double) dataBase.times.get(i) / dividerTime, dataBase.velocities.get(dataBase.times.get(i)).getY());
        }
        ChartPanel velocityYPanel = new ChartPanel(graphics.getChart(seriesVy, "Зависимость проекции скорости на ось Y от времени", "t", "Vy"));

        XYSeries seriesAngle = new XYSeries("αv(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesAngle.add((double) dataBase.times.get(i) / dividerTime, dataBase.velocities.get(dataBase.times.get(i)).getAzimuthAngle());
        }
        ChartPanel anglePanel = new ChartPanel(graphics.getChart(seriesAngle, "Зависимость угла направления вектора скорости от времени", "t", "αv"));

        XYSeries seriesA = new XYSeries("a(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesA.add((double) dataBase.times.get(i) / dividerTime, dataBase.accelerations.get(dataBase.times.get(i)).getValue());
        }
        ChartPanel accelerationPanel = new ChartPanel(graphics.getChart(seriesA, "Зависимость ускорения от времени", "t", "a"));

        XYSeries seriesAx = new XYSeries("ax(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesAx.add((double) dataBase.times.get(i) / dividerTime, dataBase.accelerations.get(dataBase.times.get(i)).getX());
        }
        ChartPanel accelerationXPanel = new ChartPanel(graphics.getChart(seriesAx, "Зависимость проекции ускорения на ось X от времени", "t", "ax"));

        XYSeries seriesAy = new XYSeries("ay(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesAy.add((double) dataBase.times.get(i) / dividerTime, dataBase.accelerations.get(dataBase.times.get(i)).getY());
        }
        ChartPanel accelerationYPanel = new ChartPanel(graphics.getChart(seriesAy, "Зависимость проекции ускорения на ось Y от времени", "t", "ay"));

        XYSeries seriesAngleA = new XYSeries("αa(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesAngleA.add((double) dataBase.times.get(i) / dividerTime, dataBase.accelerations.get(dataBase.times.get(i)).getAzimuthAngle());
        }
        ChartPanel angleAPanel = new ChartPanel(graphics.getChart(seriesAngleA, "Зависимость угла направления вектора ускорения от времени", "t", "αa"));

        XYSeries seriesF = new XYSeries("F сопр.(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesF.add((double) dataBase.times.get(i) / dividerTime, dataBase.resistanceForces.get(dataBase.times.get(i)).getValue());
        }
        ChartPanel resistanceForcePanel = new ChartPanel(graphics.getChart(seriesF, "Зависимость силы сопротивления от времени", "t", "F сопр."));

        XYSeries seriesFx = new XYSeries("F сопр.x(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesFx.add((double) dataBase.times.get(i) / dividerTime, dataBase.resistanceForces.get(dataBase.times.get(i)).getX());
        }
        ChartPanel resistanceForceXPanel = new ChartPanel(graphics.getChart(seriesFx, "Зависимость проекции силы сопротивления на ось X от времени", "t", "F сопр.x"));

        XYSeries seriesFy = new XYSeries("F сопр.y(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesFy.add((double) dataBase.times.get(i) / dividerTime, dataBase.resistanceForces.get(dataBase.times.get(i)).getY());
        }
        ChartPanel resistanceForceYPanel = new ChartPanel(graphics.getChart(seriesFy, "Зависимость проекции силы сопротивления на ось Y от времени", "t", "F сопр.y"));


        XYSeries seriesH = new XYSeries("H(t)");
        for (int i = 0; i < dataBase.times.size(); i++) {
            seriesH.add((double) dataBase.times.get(i) / dividerTime, dataBase.distanceToEarth.get(dataBase.times.get(i)));
        }
        ChartPanel distanceToEarthPanel = new ChartPanel(graphics.getChart(seriesH, "Зависимость расстояния до земли от времени", "t", "H"));

        add(distanceTravelledPanel);
        add(shiftPanel);
        add(shiftXPanel);
        add(shiftYPanel);
        add(velocityPanel);
        add(velocityXPanel);
        add(velocityYPanel);
        add(anglePanel);
        add(resistanceForcePanel);
        add(resistanceForceXPanel);
        add(resistanceForceYPanel);
        add(accelerationPanel);
        add(accelerationXPanel);
        add(accelerationYPanel);
        add(angleAPanel);
        add(distanceToEarthPanel);
    }
}
