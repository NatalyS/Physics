package ru.spb.herzen.physics;

import javax.vecmath.Point3d;
import java.awt.*;

/**
 * Интерфейс преобразователя экранных координат в логические и наоборот
 */
public interface CoordinateConverter {
    /**
     * Преобразовать логическую точку в экранные координаты
     *
     * @param point Точка в логической системе координат
     * @return Положение точки на JPanel
     */
    Point toScreen(Point3d point);

    /**
     * Преобразовать экранные координаты в логическую точку
     *
     * @param point Положение точки на JPanel
     * @return Точка в логической системе координат
     */
    Point3d toPoint3d(Point point);

    /**
     * Преобразовать размерный параметр из логически координат в экранные
     *
     * @param size Расстояние в логической системе координат
     * @return Расстояние в пикселях
     */
    int scaleSize(double size);

    /**
     * Получить физический размер панели по горизонтали
     *
     * @return Размер в пикселях
     */
    int getWidth();

    /**
     * Получить физический размер панели по вертикали
     *
     * @return Размер в пикселях
     */
    int getHeight();
}
