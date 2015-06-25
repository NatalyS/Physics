package ru.spb.herzen.physics;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Описание точки
 */
public class MaterialPoint extends PObject {
    public Sphere sphere;           //визуальный 3D-объект
    private int radius = 2;         //радиус в мм
    private double k1;              //коэффициент лобового сопротивления
    private Model model;            //связь с моделью
    private BranchGroup point = null;   //предыдущая ветка объекта в 3D-графе

    public MaterialPoint(Point start, Point end, int stepTimeInMilliSeconds, Model model) {
        this(model.getCoordinateConverter().toPoint3d(end), stepTimeInMilliSeconds, model);
    }

    public MaterialPoint(Point3d center0, int stepTimeInMilliSeconds, Model model) {
        this.constants = model.getConstants();
        this.center0 = center0;
        this.model = model;
        setVelocity(new PVector3D(50, 30, 0, true));
        setShift(new PVector3D(0, 0, 0, true));

        if (model.isHaveResistanceForce()) // Если сила сопротивления учитывается
            k1 = 6 * Math.PI * constants.getDynamicViscosityOfTheEnvironment() * radius * 0.01;
        else
            k1 = 0;

        setResistanceForce(velocity0.multiply(-k1));
        setGravityForce(new PVector3D(0, -mass * constants.getAccelerationOfGravity(), 0, false));
        setAcceleration0(resistanceForce0.add(gravityForce).multiply(1 / mass));
        goToStartPosition();

        setMobile(true);                    // Подвижный объект
        setElasticCollision(true);          // упругое столкновение

        this.stepTimeInMilliSeconds = stepTimeInMilliSeconds;

        startRecord();

        appearance = new Appearance();
        sphere = new Sphere(radius, Sphere.GENERATE_NORMALS, 200, appearance);

        vectors = new Vectors(this);
        color = Color.black;
    }

    /**
     * Перейти в начальное положение
     */
    public void goToStartPosition() {
        center = center0;
        velocity = velocity0;
        shift = shift0;
        resistanceForce = resistanceForce0;
        acceleration = acceleration0;
        startRecord();
    }

    public boolean isBelong(Point point) {
        Point3d point3d = model.getCoordinateConverter().toPoint3d(point);
        if (Math.pow(point3d.getX() - center.getX(), 2) + Math.pow(point3d.getY() - center.getY(), 2) <= Math.pow(radius, 2)) {
            return true;
        } else return false;
    }

    /**
     * Изобразить материальную точку на плоскости
     *
     * @param g         Графический контекст
     * @param timestamp Момент времени
     */
    public void draw(Graphics g, int timestamp) {
        if (model.getMeasurement() != 2) return;
        Point3d location = dataBase.coordinates.get(timestamp);
        Point center = model.getCoordinateConverter().toScreen(location);
        if (this.isHighlighted()) {
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(Color.black);
            float dash[] = {3.0f};
            g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g2d.drawLine((int) center.getX(), 0, (int) center.getX(), model.getCoordinateConverter().getHeight());
            g2d.drawLine(0, (int) center.getY(), model.getCoordinateConverter().getWidth(), (int) center.getY());
            g.setColor(Color.blue);
            g2d.setStroke(new BasicStroke(1.0f));
        } else {
            g.setColor(color);
        }
        int screenRadius = model.getCoordinateConverter().scaleSize(radius);
        g.fillOval((int) center.getX() - screenRadius, (int) center.getY() - screenRadius, 2 * screenRadius, 2 * screenRadius);
        if (isShowVectors) drawVectors(model, g, timestamp);
    }

    /**
     * Визуальные размеры объекта (высота)
     *
     * @return Высота
     */
    public double getHeight() {
        return 2 * radius;
    }

    /**
     * Визуальные размеры объекта (ширина)
     *
     * @return Ширина
     */
    public double getWidth() {
        return 2 * radius;
    }

    public void recordDistanceTravelled(int timestamp) {
        dataBase.distanceTravelled.put(0, 0d);
        for (int i = 1; i < dataBase.times.size(); i++) {
            dataBase.distanceTravelled.put(dataBase.times.get(i),
                    dataBase.distanceTravelled.get(dataBase.times.get(i - 1)) +
                            Math.sqrt(Math.pow((dataBase.coordinates.get(dataBase.times.get(i)).getX() -
                                    dataBase.coordinates.get(dataBase.times.get(i - 1)).getX()), 2) +
                                    Math.pow((dataBase.coordinates.get(dataBase.times.get(i)).getY() -
                                            dataBase.coordinates.get(dataBase.times.get(i - 1)).getY()), 2)));
        }
    }

    public void recordDistanceToObject(int timestamp, PObject object, HashMap<Integer, Double> distanceToObject) {
        for (int i = 0; i < dataBase.times.size(); i++) {
            distanceToObject.put(dataBase.times.get(i),
                    object.getDistance(new Point3d((int) dataBase.coordinates.get(dataBase.times.get(i)).getX(), (int) dataBase.coordinates.get(dataBase.times.get(i)).getY(), 0)) - radius);
        }
    }

    /**
     * Изменение скорости после удара
     *
     * @param angleToNormal Угол между вектором скорости в момент удара и перпендикуляром к поверхности (в градусах)
     */
    private void velocityAfterCollision(double angleToNormal) {
        System.out.println("____ Before Collision: angle = " + velocity.getAzimuthAngle());
        System.out.println("angleToNormal = " + angleToNormal);
        velocity.setAzimuthAngle(90 - angleToNormal);
        System.out.println("____ After Collision: angle = " + velocity.getAzimuthAngle());
        dataBase.velocities.put(dataBase.times.get(dataBase.times.size()) - 1, velocity);
    }

    public ArrayList<PObject> getCurrCollidingObjects() {
        ArrayList<PObject> currCollidingObjects = new ArrayList<PObject>();
        Point3d point;
        for (int i = 0; i < collidingObjects.size(); i++) {
            point = new Point3d((int) center.getX(), (int) center.getY(), 0);
            if (collidingObjects.get(i).getDistance(point) <= radius)
                currCollidingObjects.add(collidingObjects.get(i));
        }
        return currCollidingObjects;
    }

    public void move(int timeStep, int dividerTime, int deceleration) {

        int lastTimestamp = dataBase.times.get(dataBase.times.size() - 1);

        if (this.isMobile()) { // Если объект подвижный
            if (!getCurrCollidingObjects().isEmpty()) { // Если объект "слипся" с хотя бы одним объектом
                velocityAfterCollision(getCurrCollidingObjects().get(0).angleToNormal(velocity.getAzimuthAngle()));
            }
            double dt = (double) timeStep / dividerTime;

            //Метод Эйлера

            // S = V * t + a * t^2 / 2
            setShift(velocity.multiply(dt).add(acceleration.multiply(dt * dt / 2)));

            // V = V0 + a0 * t
            setVelocity(dataBase.velocities.get(lastTimestamp).add(dataBase.accelerations.get(lastTimestamp).multiply(dt)));

            // F_сопр. = - k1 * V
            setResistanceForce(velocity.multiply(-k1));

            // a = (F_тяж. + F_сопр.) / m

            setAcceleration(getResistanceForce().add(getGravityForce()).multiply(1.0 / getMass()));

            center = getCenterInTime();
        }

        //Сохранение измерений в историю
        int nextTime = lastTimestamp + timeStep;
        dataBase.times.add(nextTime);
        dataBase.velocities.put(nextTime, getVelocity());
        dataBase.accelerations.put(nextTime, getAcceleration());
        dataBase.resistanceForces.put(nextTime, getResistanceForce());
        dataBase.shifts.put(nextTime, getShift());
        dataBase.coordinates.put(nextTime, center);
    }

    // Через шаг
    public Point3d getCenterInTime() {
        return new Point3d(center0.getX() + shift.getX(),
                center0.getY() + shift.getY(),
                center0.getZ());
    }

    public void draw3D() {
        if (model.getMeasurement() != 3) return;

//        if (point != null) {
//            point.detach();
//        }

        TransformGroup transformGroup = new TransformGroup();
        Transform3D locateTransform = new Transform3D();
        Point3d centerScaled = (Point3d) center.clone();
        centerScaled.scale(0.1);
        locateTransform.setTranslation(new Vector3d(centerScaled));
        transformGroup.setTransform(locateTransform);

        if (point == null) {
            point = new BranchGroup();
            point.setCapability(BranchGroup.ALLOW_DETACH);
            point.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            point.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            point.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            point.addChild(sphere);
            point.compile();

            BranchGroup contentBranch;
            contentBranch = ((Model3D) model).getContentBranch();
            contentBranch.addChild(point);
        }
    }

    public void setAppearance(BufferedImage image) {
        super.setAppearance(image);
        if (point != null) {
            point.removeChild(sphere);
        }
        sphere = new Sphere(radius / 10, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, 200, appearance);
        draw3D();
    }

    public HashMap<String, Object> getState() {
        HashMap<String, Object> state = super.getState();
        state.put("radius", String.valueOf(radius));
        state.put("k1", String.valueOf(k1));
        return state;
    }

    public void setState(HashMap<String, Object> state) {
        super.setState(state);
        radius = Integer.parseInt(String.valueOf(state.get("radius")));
        k1 = Double.parseDouble(String.valueOf("k1"));
    }
}
