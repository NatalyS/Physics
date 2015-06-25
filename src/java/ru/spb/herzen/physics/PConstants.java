package ru.spb.herzen.physics;
/*
    Класс, содержащий константы физического мира
 */
public class PConstants {
    public final static int AIR = 0;            // воздух при t=20С и давлении 1атм.
    public final static int WATER = 1;          // вода
    public final static int GLYCEROL = 2;       // глицерин

    public final static int DISK = 0;           // диск
    public final static int HEMISPHERE = 1;     // полусфера
    public final static int SPHERE = 2;         // шар
    public final static int DROP_SHAPE = 3;     // каплевидное тело

    private double accelerationOfGravity = 9.8;
    private double dynamicViscosityOfTheEnvironment = 0.0182;

    /**
     * Получение ускорения свободного падения
     *
     * @return Ускорение свободного падения
     */
    public double getAccelerationOfGravity() {
        return accelerationOfGravity;
    }

    /**
     * Установка ускорения свободного падения
     *
     * @param accelerationOfGravity Ускорение свободного падения
     */
    public void setAccelerationOfGravity(double accelerationOfGravity) {
        this.accelerationOfGravity = accelerationOfGravity;
    }

    /**
     * Получение динамической вязкости среды
     *
     * @return Динамическая вязкость среды
     */
    public double getDynamicViscosityOfTheEnvironment() {
        return dynamicViscosityOfTheEnvironment;
    }

    /**
     * Установка динамической вязкости среды
     *
     * @param environment Окружающая среда
     */
    public void setDynamicViscosityOfTheEnvironment(int environment) {
        switch (environment) {
            case AIR:
                dynamicViscosityOfTheEnvironment = 0.0182;  // воздух при t=20С и давлении 1атм.
                break;
            case WATER:
                dynamicViscosityOfTheEnvironment = 1.002;  // вода
                break;
            case GLYCEROL:
                dynamicViscosityOfTheEnvironment = 1480;  // глицерин
                break;
        }
    }

    /**
     * Получение коэффициента лобового сопротивления
     *
     * @param shape Форма поперечного сечения объекта
     * @return Коэффициента лобового сопротивления
     */
    public double getCoefficientOfDrag(int shape) {
        double coefficient = 0;
        switch (shape) {
            case DISK:
                coefficient = 1.11;  // диск
                break;
            case HEMISPHERE:
                coefficient = 0.55;  // полусфера
                break;
            case SPHERE:
                coefficient = 0.4;  // шар
                break;
            case DROP_SHAPE:
                coefficient = 0.045;  // каплевидное тело
                break;
        }
        return coefficient;
    }
}
