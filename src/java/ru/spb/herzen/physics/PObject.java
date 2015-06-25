package ru.spb.herzen.physics;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.Appearance;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Базовый класс для физических объектов
 */
public abstract class PObject implements Storable {

    public PConstants constants;                    // Константы физического мира
    public boolean isShowVectors = true;           // Отображать вектора
    public ArrayList<PObject> collidingObjects = new ArrayList<PObject>(); // Объекты, с которыми произошло столкновение
    public int stepTimeInMilliSeconds = 0;          // Шаг таймера

    public DataBase dataBase = new DataBase();      // История изменения значений объекта
    protected double mass = 0.1; // Масса тела по умолчанию 0.1кг
    protected Point3d center0;  // Начальное положение центра
    protected PVector3D shift0;           //
    protected PVector3D velocity0;        // Начальная скорость
    protected PVector3D acceleration0;    // Начальное ускорение
    protected PVector3D resistanceForce0; // Начальная сила сопротивления
    protected Point3d center;          // Текущее положение центра
    protected PVector3D shift;            // Текущее смещение
    protected PVector3D velocity;         // Текущая скорость
    protected PVector3D acceleration;     // Текущее ускорение
    protected PVector3D resistanceForce;  // Текущая сила сопротивления
    protected PVector3D gravityForce;     // Сила гравитации
    protected Appearance appearance;                // Визуальное преобразование для 3D
    protected Color color;                          // Визуальный цвет
    protected Vectors vectors = new Vectors(this);  // Набор векторов для отображения
    protected boolean mobile;                     // Подвижность объекта
    protected boolean elasticCollision;           // Упругое столкновение
    protected boolean highlighted;                // Выделение

    public PObject() {
        PVector3D empty = new PVector3D(0, 0, 0, true);
        setShift0(empty);
        setShift(empty);
        setAcceleration0(empty);
        setAcceleration(empty);
        setResistanceForce0(empty);
        setResistanceForce(empty);
        setVelocity0(empty);
        setVelocity(empty);
        startRecord();
    }

    public PObject(Dimension start, Dimension end) {
        startRecord();
    }

    public int getLastTimestamp() {
        return dataBase.times.get(dataBase.times.size() - 1);
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    /**
     * Переместиться в начальные условия
     */
    public void goToStartPosition() {}              // Переместиться в начальные условия

    /**
     * Получить значение массы
     * @return Масса тела в кг
     */
    public double getMass() {
        return mass;
    }

    /**
     * Изменить значение массы
     * @param mass Масса тела в кг
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Получить текущее положение центра объекта
     * @return Положение центра объекта
     */
    public Point3d getCenter() {
        return center;
    }

    /**
     * Изменить текущее положение центра объекта
     * @param center Текущее положение центра

     */
    public void setCenter(Point3d center) {
        this.center = center;
    }

    /**
     * Получить начальное положение центра объекта
     * @return Положение центра объекта
     */
    public Point3d getCenter0() {
        return center0;
    } // Начальное положение

    /**
     * Изменить начальное положение
     * @param newCenter Новое начальное положение
     */
    public void setCenter0(Point3d newCenter) {
        center0 = newCenter;
    }

    /**
     * Изменить начальное положение
     * @param x Координата X начального положения
     * @param y Координата Y начального положения
     * @param z Координата Z начального положения
     */
    public void setCenter0(double x, double y, double z) {
        center0 = new Point3d(x, y, z);
    }

    /**
     * Получить текущую скорость
     *
     * @return Текущая скорость
     */
    public PVector3D getVelocity() {
        return velocity;
    }

    /**
     * Установить текущую скорость
     *
     * @param newVelocity Текущая скорость
     */
    public void setVelocity(PVector3D newVelocity) {
        velocity = newVelocity;
    }

    /**
     * Получить начальную скорость
     *
     * @return Начальная скорость
     */
    public PVector3D getVelocity0() {
        return velocity0;
    }

    /**
     * Установить начальную скорость
     *
     * @param newVelocity0 Начальная скорость
     */
    public void setVelocity0(PVector3D newVelocity0) {
        velocity0 = newVelocity0;
    }

    /**
     * Получить текущее ускорение
     * @return Текущее ускорение
     */
    public PVector3D getAcceleration() {
        return acceleration;
    }

    /**
     * Установить текущее ускорение
     * @param newAcceleration Текущее ускорение
     */
    public void setAcceleration(PVector3D newAcceleration) {
        acceleration = newAcceleration;
    }

    /**
     * Получить начальное ускорение
     * @return Начальное ускорение
     */
    public PVector3D getAcceleration0() {
        return acceleration0;
    }

    /**
     * Установить начальное ускорение
     * @param newAcceleration0 Начальное ускорение
     */
    public void setAcceleration0(PVector3D newAcceleration0) {
        acceleration0 = newAcceleration0;
    }

    /**
     * Получить текущее перемещение
     * @return Текущее перемещение
     */
    public PVector3D getShift() {
        return shift;
    }

    /**
     * Изменить текущее смещение
     * @param shift Текущее смещение
     */

    public void setShift(PVector3D shift) {
        this.shift = shift;
    }

    /**
     * Получить начальное перемещение
     * @return Начальное перемещение
     */
    public PVector3D getShift0() {
        return shift0;
    }

    /**
     * Изменить начальное смещение
     * @param shift0 Начальное смещение
     */
    public void setShift0(PVector3D shift0) {
        this.shift0 = shift0;
    }

    /**
     * Получить текущее значение силы сопротивления
     * @return Текущая сила сопротивления
     */
    public PVector3D getResistanceForce() {
        return resistanceForce;
    }

    /**
     * Текущее значение силы сопротивления
     * @param resistanceForce Сила сопротивления
     */
    public void setResistanceForce(PVector3D resistanceForce) {
        this.resistanceForce = resistanceForce;
    }

    /**
     * Получить начальное значение силы сопротивления
     * @return Начальная сила сопротивления
     */
    public PVector3D getResistanceForce0() {
        return resistanceForce0;
    }

    /**
     * Начальное значение силы сопротивления
     * @param resistanceForce0 Сила сопротивления
     */
    public void setResistanceForce0(PVector3D resistanceForce0) {
        this.resistanceForce0 = resistanceForce0;
    }

    /**
     * Получить значение силы тяжести
     * @return Сила тяжести
     */
    public PVector3D getGravityForce() {
        return gravityForce;
    }

    /**
     * Текущее значение гравитационной силы
     * @param gravityForce Гравитационная сила
     */
    public void setGravityForce(PVector3D gravityForce) {
        this.gravityForce = gravityForce;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    /**
     * Определение внешнего вида 3D-объекта
     *
     * @param image Текстура
     */
    public void setAppearance(BufferedImage image) {
        appearance = new Appearance(); // создание оформления объекта
        // загрузка текстуры для объекта сцены
        TextureLoader textureLoader = new TextureLoader(image);
        // установка битов для использования текстуры
        Texture texture = textureLoader.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);

        appearance.setTexture(texture);
        appearance.setTextureAttributes(texAttr);
    }

    /**
     * Получить флаг упругого столкновения
     *
     * @return true - если упругое столкновение
     */
    public boolean isElasticCollision() {
        return elasticCollision;
    }

    /**
     * Установить упругое столкновение
     *
     * @param elasticCollision true - если упругое столкновение
     */
    public void setElasticCollision(boolean elasticCollision) {
        this.elasticCollision = elasticCollision;
    }

    /**
     * Получить подвижность объекта
     * @return true - если подвижен
     */
    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    /**
     * Получить высоту объекта
     * @return Высота объекта (в метрах)
     */
    public double getHeight() { return 0; }

    /**
     * Получить ширину объекта
     * @return Ширина объекта (в метрах)
     */

    public double getWidth() { return 0; }

    /**
     * Получить цвет объекта
     * @return Цвет объекта
     */
    public Color getColor() {
        return color;
    }

    /**
     * Изменить цвет объекта
     * @param newColor Новый цвет объекта
     */
    public void setColor(Color newColor) {
        color = newColor;
    }

    /**
     * Проверить принадлежность точки объекту (по экранным координатам)
     * @param point Координаты точки
     * @return true - если принадлежит
     */
    public boolean isBelong(Point point) {
        return false; }

    /**
     * Получить расстояние до точки
     * @param point Координаты точки
     * @return Расстояние (в метрах)
     */
    public double getDistance(Point3d point) {
        return 0; }

    /**
     * Получить угол между некоторым вектором и перпендикуляром
     *
     * @param angle Угол направления вектора в градусах
     * @return Угол между вектором и перпендикуляром в градусах
     */
    public double angleToNormal(double angle) { return 0; }

    /**
     * Получить положение нормали
     * @param point Положение нормали
     * @return Наведение на нормаль
     */

    public Point getPointNormal(Point point) { return null; }

    /**
     * Получить список объектов, с которыми произошло столкновение в текущий момент
     *
     * @return Список объектов при столкновении
     */
    public ArrayList<PObject> getCurrCollidingObjects() { return null; }

    public void addCollidingObject(PObject object) {
        boolean isNotBelongCollidingObjects = true; // Проверка, не содержится ли уже объект в списке столкнувшихся объектов
        for (int i = 0; i < collidingObjects.size(); i++) {
            if (collidingObjects.get(i).equals(object))
                isNotBelongCollidingObjects = false;
        }
        if (isNotBelongCollidingObjects)
            collidingObjects.add(object);
    }

    /**
     * Отобразить 2D-объект на графическом контексте
     * @param g Графический контекст
     * @param timestamp Момент времени
     */
    public abstract void draw(Graphics g, int timestamp);

    /**
     * Построение 3D-объекта в текущем положении
     */
    public abstract void draw3D();

    /**
     * Переместить объект
     * @param timestamp Шаг таймера
     * @param dividerTime Делитель для замедления времени
     * @param deceleration ?
     */
    public abstract void move(int timestamp, int dividerTime, int deceleration);

    /**
     * Очистить историю и вернуться в начальное состояние
     */
    public void startRecord() {
        dataBase = new DataBase();
        dataBase.times.add(0);
        dataBase.velocities.put(0, getVelocity0());
        dataBase.shifts.put(0, getShift0());
        dataBase.coordinates.put(0, getCenter0());
        dataBase.accelerations.put(0, acceleration0);
        dataBase.resistanceForces.put(0, resistanceForce0);
    }

    /**
     * Время перемещения объекта
     * @param timestamp Шаг таймера
     */
    public abstract void recordDistanceTravelled(int timestamp);

    /**
     * Расстояние до объекта
     * @param timestamp Шаг таймера
     * @param object Объект-направление
     * @param distanceToObject Вектор расстояний до объектов
     */
    public abstract void recordDistanceToObject(int timestamp, PObject object, HashMap<Integer, Double> distanceToObject);

    /**
     * ?
     * @return ?
     */
    public Point3d getCenterInTime() {
        return null; }

    /**
     * Отобразить вектора
     * @param g Графический контекст
     * @param timestamp Момент времени
     */
    public void drawVectors(Model model, Graphics g, int timestamp) {
        vectors.drawAll(model, this, g, Color.blue, timestamp);
    }

    public HashMap<String, Object> getState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("showVectors", String.valueOf(isShowVectors));
        state.put("mobile", String.valueOf(mobile));
        state.put("elasticCollision", String.valueOf(elasticCollision));
        state.put("stepTimeInMilliSeconds", String.valueOf(stepTimeInMilliSeconds));
        state.put("mass", String.valueOf(mass));
        state.put("shift0", shift0.getState());
        state.put("velocity0", velocity0.getState());
        state.put("acceleration0", acceleration0.getState());
        state.put("resistanceForce0", resistanceForce0.getState());
        state.put("shift", shift.getState());
        state.put("velocity", velocity.getState());
        state.put("acceleration", acceleration.getState());
        state.put("resistanceForce", resistanceForce.getState());
        state.put("gravityForce", gravityForce.getState());
        state.put("color", Helper3d.getStateColor(color));
        state.put("center0", Helper3d.getStatePoint3d(center0));
        state.put("center", Helper3d.getStatePoint3d(center));
        state.put("class", getClass().toString());
        return state;
    }

    public void setState(HashMap<String, Object> state) {
        shift0 = PVector3D.createFromState((HashMap<String, Object>) state.get("shift0"));
        velocity0 = PVector3D.createFromState((HashMap<String, Object>) state.get("velocity0"));
        acceleration0 = PVector3D.createFromState((HashMap<String, Object>) state.get("acceleration0"));
        resistanceForce0 = PVector3D.createFromState((HashMap<String, Object>) state.get("resistanceForce0"));
        shift = PVector3D.createFromState((HashMap<String, Object>) state.get("shift"));
        velocity = PVector3D.createFromState((HashMap<String, Object>) state.get("velocity"));
        acceleration = PVector3D.createFromState((HashMap<String, Object>) state.get("acceleration"));
        resistanceForce = PVector3D.createFromState((HashMap<String, Object>) state.get("resistanceForce"));
        gravityForce = PVector3D.createFromState((HashMap<String, Object>) state.get("gravityForce"));

        isShowVectors = Boolean.parseBoolean(String.valueOf(state.get("showVectors")));
        mobile = Boolean.parseBoolean(String.valueOf(state.get("mobile")));
        elasticCollision = Boolean.parseBoolean(String.valueOf(state.get("elasticCollision")));
        stepTimeInMilliSeconds = Integer.parseInt(String.valueOf(state.get("stepTimeInMilliSeconds")));
        mass = Double.parseDouble(String.valueOf(state.get("mass")));

        center0 = Helper3d.createPoint3dFromState((HashMap<String, Object>) state.get("center0"));
        center = Helper3d.createPoint3dFromState((HashMap<String, Object>) state.get("center"));
        color = Helper3d.createColorFromState((HashMap<String, Object>) state.get("color"));
    }
}
