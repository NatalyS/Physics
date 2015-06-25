package ru.spb.herzen.physics;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Обобщённый интерфейс для хранения и визуального представления коллекции физических объектов
 */
public interface Model extends Storable {

    /**
     * Получить панель для отображения модели и элементов управления
     *
     * @return Панель для содержания
     */
    JPanel getContentPane();

    /**
     * Получить панель для отображения данных
     *
     * @return Панель для данных
     */
    JPanel getDataPane();

    /**
     * Получить упорядоченную коллекцию физических объектов
     * @return Коллекция физических объектов
     */
    ArrayList<PObject> getObjects();

    /**
     * Установить упорядоченную коллекцию физических объектов
     * @param objects Коллекция физических объектов
     */
    void setObjects(ArrayList<PObject> objects);

    /**
     * Добавить в коллекцию физический объект
     *
     * @param object Физический объект
     */
    void addObject(PObject object);

    /**
     * Получить текущий объект (при создании с помощью мыши)
     * @return Текущий физический объект
     */
    PObject getCurrObject();

    /**
     * Изменить текущий объект
     * @param object Новый физический объект
     */
    void setCurrObject(PObject object);

    /**
     * Получить тип текущего объекта
     * @return Тип объекта
     */
    int getNumCurrObject();

    /**
     * Изменить тип текущего объекта
     * @param number Тип объекта
     */
    void setNumCurrObject(int number);

    /**
     * Запущено ли моделирование?
     *
     * @return true - если запущено
     */
    boolean isRunning();


    /**
     * Запустить/остановить моделирование
     *
     * @param isRunning true - если запущено
     */
    void setRunning(boolean isRunning);

    /**
     * Получить тип визуализации (количество размерностей)
     * @return Количество размерностей (2 - плоское, 3 - объёмное)
     */
    int getMeasurement();

    /**
     * Изменить тип визуализации (количество размерностей)
     * @param measurement Количество размерностей (2 - плоское, 3 - объёмное)
     */
    void setMeasurement(int measurement);

    /**
     * Получить делитель для замедления времени
     * @return Делитель замедления времени
     */
    int getDeceleration();

    /**
     * Изменить делитель для замедления времени
     * @param deceleration делитель для замедления времени
     */
    void setDeceleration(int deceleration);

    /**
     * Получить полное время моделирования
     * @return Полное время моделирования в миллисекундах
     */
    int getTotalTime();

    /**
     * Изменить полное время моделирования
     */
    void setTotalTime(int totalTime);

    /**
     * Получить текущее время моделирования
     * @return Текущее время моделирования в миллисекундах
     */
    int getTimestamp();

    /**
     * Изменить текущее время моделирования
     * @param timestamp Текущее время моделирования в миллисекундах
     */
    void setTimestamp(int timestamp);

    /**
     * Получить параметры физического мира
     * @return Объект для доступа к параметрам физического мира
     */
    public PConstants getConstants();

    /**
     * Изменить параметры физического мира
     * @param constants Объект для доступа к параметрам физического мира
     */
    public void setConstants(PConstants constants);

    /**
     * Получить тип окружающей среды
     * @return Тип окружающей среды (константы из PConstants)
     */
    public int getEnvironment();

    /**
     * Изменить тип окружающей среды
     * @param environment Тип окружающей среды (константы из PConstants)
     */
    public void setEnvironment(int environment);

    /**
     * Проверить, учитывается ли действие силы сопротивления
     * @return true - если учитывается
     */
    public boolean isHaveResistanceForce();

    /**
     * Установить необходимость учёта силы сопротивления
     * @param haveResistanceForce true - если учитывается
     */
    public void setHaveResistanceForce(boolean haveResistanceForce);

    /**
     * Сброс визуального представления в исходное состояние
     */
    public void reset();

    public CoordinateConverter getCoordinateConverter();

    public void setCoordinateConverter(CoordinateConverter coordinateConverter);


    /**
     * Отображение всех объектов на панели
     * @param g Графический контекст
     */
    public void drawAll(Graphics g);
}
