package ru.spb.herzen.physics;

import java.util.HashMap;

/**
 * Интерфейс для объектов, которые могут сохранять и восстанавливать своё состояние
 */
public interface Storable {

    /**
     * Получить текущее состояние объекта (возможно, с вложенными сложными структурами)
     *
     * @return Текущее состояние в виде ассоциативного массива
     */
    public HashMap<String, Object> getState();

    /**
     * Изменить текущее состояние объекта
     * @param state Новое состояние в виде ассоциативного массива
     */
    public void setState(HashMap<String, Object> state);
}
