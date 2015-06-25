package ru.spb.herzen.physics;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Обобщённая модель для доступа и управления коллекцией физических объектов
 */
public abstract class GenericModel implements Model {
    public int environment = 0;                 // Тип окружающей среды
    public PConstants constants = new PConstants();
    private boolean drawing = false;            // Режим рисования
    private boolean running = false;

    private boolean haveResistanceForce = true; // Учёт силы сопротивления
    private ArrayList<PObject> objects = new ArrayList<PObject>();
    private PObject currObject;
    private int numCurrObject = 0;
    private int measurement = 0;
    private int deceleration = 0;
    private int totalTime = 0;
    private int timestamp = 0;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isHaveResistanceForce() {
        return haveResistanceForce;
    }

    public void setHaveResistanceForce(boolean haveResistanceForce) {
        this.haveResistanceForce = haveResistanceForce;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean run) {
        this.running = run;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    public PConstants getConstants() {
        return constants;
    }

    public void setConstants(PConstants constants) {
        this.constants = constants;
    }

    public ArrayList<PObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<PObject> objects) {
        this.objects = objects;
    }

    public PObject getCurrObject() {
        return currObject;
    }

    public void setCurrObject(PObject object) {
        this.currObject = object;
    }

    public int getNumCurrObject() {
        return numCurrObject;
    }

    public void setNumCurrObject(int number) {
        this.numCurrObject = number;
    }

    public int getMeasurement() {
        return measurement;
    }

    public void setMeasurement(int measurement) {
        this.measurement = measurement;
    }

    public int getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(int deceleration) {
        this.deceleration = deceleration;
    }

    public void addObject(PObject object) {
        objects.add(object);
    }

    public HashMap<String, Object> getState() {
        HashMap<String, Object> state = new HashMap<String, Object>();
        state.put("drawing", String.valueOf(isDrawing()));
        state.put("run", String.valueOf(isRunning()));
        state.put("haveResistanceForce", String.valueOf(isHaveResistanceForce()));
        state.put("numCurrObject", String.valueOf(getNumCurrObject()));
        state.put("measurement", String.valueOf(getMeasurement()));
        state.put("deceleration", String.valueOf(getDeceleration()));
        state.put("totalTime", String.valueOf(getTotalTime()));
        state.put("timestamp", String.valueOf(getTimestamp()));
        HashMap<String, Object> objectsMap = new HashMap<>();
        for (PObject object : objects) {
            objectsMap.put(String.valueOf(object.hashCode()), object.getState());
        }
        state.put("objects", objectsMap);
        return state;
    }

    public void setState(HashMap<String, Object> state) {
        setDrawing(Boolean.valueOf(String.valueOf(state.get("drawing"))));
        setRunning(Boolean.valueOf(String.valueOf(state.get("run"))));
        setHaveResistanceForce(Boolean.valueOf(String.valueOf(state.get("haveResistanceForce"))));
        setNumCurrObject(Integer.valueOf(String.valueOf(state.get("numCurrObject"))));
        setMeasurement(Integer.valueOf(String.valueOf(state.get("measurement"))));
        setDeceleration(Integer.valueOf(String.valueOf(state.get("deceleration"))));
        setTotalTime(Integer.valueOf(String.valueOf(state.get("totalTime"))));
        setTimestamp(Integer.valueOf(String.valueOf(state.get("timestamp"))));
        HashMap<String, Object> objectsMap = (HashMap<String, Object>) state.get("objects");
        for (String objectHash : objectsMap.keySet()) {
            HashMap<String, Object> objectDescription = (HashMap<String, Object>) objectsMap.get(objectHash);
            String className = String.valueOf(objectDescription.get("class"));
            setObjects(new ArrayList<>());
            try {
                Class cl = Class.forName(className);
                PObject object = (PObject) cl.newInstance();
                object.setState(objectDescription);
                addObject(object);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                System.out.println("Class " + className + " isn't found");
            }
        }
    }
}
