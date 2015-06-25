package ru.spb.herzen.physics;

import java.util.HashMap;

/**
 * Класс для представления значения и выполнения операций над векторами в трёхмерном пространстве
 */
public class PVector3D implements Storable {

    double value;           //модуль вектора
    double azimuthAngle;    //азимутальный угол
    double zenithAngle;     //зенитный угол

    /**
     * Создание вектора
     *
     * @param p1        Модуль вектора или проекция на ось OX
     * @param p2        Азимутальный угол или проекция на ось OY
     * @param p3        Зенитный угол или проекция на ось OZ
     * @param isSpheric true - если параметры заданы в сферических координатах,false - в декартовых
     */
    public PVector3D(double p1, double p2, double p3, boolean isSpheric) {
        if (isSpheric) {
            this.value = p1;
            this.azimuthAngle = p2;
            this.zenithAngle = p3;
        } else {
            fromCartesian(p1, p2, p3);
        }
    }

    /**
     * Создание вектора из описания состояния (используется при загрузке состояния из файла)
     * @param state Параметры вектора
     * @return Новый объект с указанными параметрами
     */
    public static PVector3D createFromState(HashMap<String, Object> state) {
        double value = Double.parseDouble(String.valueOf(state.get("value")));
        double azimuth = Double.parseDouble(String.valueOf(state.get("azimuth")));
        double zenith = Double.parseDouble(String.valueOf(state.get("zenith")));
        return new PVector3D(value, azimuth, zenith, true);
    }

    /**
     * Получить модуль вектора
     * @return Модуль вектора
     */
    public double getValue() {
        return value;
    }

    /**
     * Изменить модуль вектора
     * @param value Модуль вектора
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Получить азимутальный угол
     * @return Азимутальный угол
     */
    public double getAzimuthAngle() {
        return azimuthAngle;
    }

    /**
     * Изменить азимутальный угол
     * @param azimuthAngle Азимутальный угол
     */
    public void setAzimuthAngle(double azimuthAngle) {
        this.azimuthAngle = azimuthAngle;
    }

    /**
     * Получить зенитный угол
     * @return Зенитный угол
     */
    public double getZenithAngle() {
        return zenithAngle;
    }

    /**
     * Установить зенитный угол
     * @param zenithAngle Зенитный угол
     */
    public void setZenithAngle(double zenithAngle) {
        this.zenithAngle = zenithAngle;
    }

    /**
     * Преобразование декартовых координат в сферические
     * @param x Проекция на ось OX
     * @param y Проекция на ось OY
     * @param z Проекция на ось OZ
     */
    private void fromCartesian(double x, double y, double z) {
        value = Math.sqrt(x * x + y * y + z * z);
        azimuthAngle = Math.atan2(y, x) / Math.PI * 180.0;
        if (value != 0) {
            zenithAngle = Math.acos(z / value) / Math.PI * 180.0;
        } else {
            zenithAngle = 0;
        }
    }

    /**
     * Получить проекцию на ось OX
     * @return Проекция на ось OX
     */
    public double getX() {
        return value * Math.cos(azimuthAngle / 180.0 * Math.PI) * Math.sin(zenithAngle / 180.0 * Math.PI);
    }

    /**
     * Изменить проекцию на ось OX
     * @param x Проекция на ось OX
     */
    public void setX(double x) {
        fromCartesian(x, this.getY(), this.getZ());
    }

    /**
     * Получить проекцию на ось OY
     * @return Проекция на ось OY
     */
    public double getY() {
        return value * Math.sin(azimuthAngle / 180.0 * Math.PI) * Math.sin(zenithAngle / 180.0 * Math.PI);
    }

    /**
     * Изменить проекцию на ось OY
     * @param y Проекция на ось OY
     */
    public void setY(double y) {
        fromCartesian(this.getX(), y, this.getZ());
    }

    /**
     * Получить проекцию на ось OZ
     * @return Проекция на ось OZ
     */
    public double getZ() {
        return value * Math.cos(zenithAngle / 180.0 * Math.PI);
    }

    /**
     * Изменить проекцию на ось OZ
     * @param z Проекция на ось OZ
     */
    public void setZ(double z) {
        fromCartesian(this.getX(), this.getY(), z);
    }

    /**
     * Умножить вектор на скаляр
     * @param n Множитель
     * @return Новый вектор
     */
    public PVector3D multiply(double n) {
        if (n >= 0) {
            return new PVector3D(this.getValue(), this.getAzimuthAngle(), this.getZenithAngle(), true);
        } else {
            return new PVector3D(this.getValue() * Math.abs(n), this.getAzimuthAngle() + 180, this.getZenithAngle(), true);
        }
    }

    /**
     * Сложить с вектором
     * @param vector Слагаемое
     * @return Новый вектор
     */
    public PVector3D add(PVector3D vector) {
        return new PVector3D(this.getX() + vector.getX(), this.getY() + vector.getY(), this.getZ() + vector.getZ(), false);
    }

    /**
     * Получить текущее состояние вектора
     * @return Набор параметров, описывающих состояние вектора
     */
    public HashMap<String, Object> getState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("value", String.valueOf(this.getValue()));
        state.put("azimuth", String.valueOf(this.getAzimuthAngle()));
        state.put("zenith", String.valueOf(this.getZenithAngle()));
        return state;
    }

    /**
     * Установить текущее состояние вектора
     * @param state Новое состояние в виде ассоциативного массива
     */
    public void setState(HashMap<String, Object> state) {
        this.setValue(Double.parseDouble(String.valueOf(state.get("value"))));
        this.setAzimuthAngle(Double.parseDouble(String.valueOf(state.get("azimuth"))));
        this.setZenithAngle(Double.parseDouble(String.valueOf(state.get("zenith"))));
    }

    /**
     * Строковое представление вектора
     * @return Строка, содержащая параметры вектора
     */
    public String toString() {
        return "Value:" + value + " AA:" + azimuthAngle + " ZA:" + zenithAngle;
    }
}
