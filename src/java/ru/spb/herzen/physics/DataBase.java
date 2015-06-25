package ru.spb.herzen.physics;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для хранения данных о траектории движения объекта и истории изменения векторов
 */
public class DataBase {
    public ArrayList<Integer> times = new ArrayList<Integer>(); // Фиксированное время

    public HashMap<Integer, PVector3D> velocities = new HashMap<Integer, PVector3D>(); // Скорость от времени

    public HashMap<Integer, PVector3D> shifts = new HashMap<Integer, PVector3D>(); // Перемещение от времени

    public HashMap<Integer, Point3d> coordinates = new HashMap<Integer, Point3d>(); // Координаты от времени

    public HashMap<Integer, PVector3D> resistanceForces = new HashMap<Integer, PVector3D>(); // Сила сопротивления от времени

    public HashMap<Integer, PVector3D> accelerations = new HashMap<Integer, PVector3D>(); // Ускорение от времени

    public HashMap<Integer, Double> distanceTravelled = new HashMap<Integer, Double>(); // Пройденный путь от времени

    public HashMap<Integer, Double> distanceToEarth = new HashMap<Integer, Double>(); // Расстояние до земли от времени

    public HashMap<Double, ArrayList<Double>> distanceToFixedObject = new HashMap<Double, ArrayList<Double>>(); // Расстояние до неподвижных объектов
}
