package ru.spb.herzen.physics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PMouseListener implements MouseListener, MouseMotionListener {
    private JPopupMenu popupMenu = new JPopupMenu();
    private JCheckBoxMenuItem pVectors = new JCheckBoxMenuItem("Показать вектора");
    private JMenuItem pProperties = new JMenuItem("Свойства объекта");
    private JMenuItem pDelete = new JMenuItem("Удалить объект");

    private Model model;                        //Связь с моделью
    private int stepTimeInMilliSeconds = 0;     //Шаг приближенного вычисления
    private ArrayList<PObject> objects;         //Коллекция объектов
    private Point startPoint;                   //Начальная точка (для протяженных объектов)
    private boolean drawing;                    //Режим построения объекта

    public PMouseListener(Model model, int stepTimeInMilliSeconds) {
        this.model = model;
        this.stepTimeInMilliSeconds = stepTimeInMilliSeconds;
        objects = model.getObjects();
    }

    /**
     * Проверка принадлежности точки на экране объектам
     *
     * @param point Координаты точки на JPanel
     * @return true - если принадлежит объекту
     */
    private boolean isBelongToObjects(Point point) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).isBelong(point)) return true;
        }
        return false;
    }

    /**
     * Получить идентификатор объекта, которому принадлежит точка на экране
     * @param point Координаты точки на JPanel
     * @return Индекс в коллекции objects
     */
    private int numBelongToObjects(Point point) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).isBelong(point)) return i;
        }
        return -1;
    }

    /**
     * Выделение объектов
     * @param e Текущее событие мыши с координатами
     */
    public void mouseClicked(MouseEvent e) {
        if (!model.isRunning()) {
            int x = e.getX();
            int y = e.getY();

            drawing = false;

            for (PObject object : objects) {
                object.setHighlighted(object.isBelong(new Point(x, y)));
            }

        }
    }

    /**
     * Начало создание объекта или отображение контекстного меню
     * @param e Текущее событие мыши с координатами
     */
    public void mousePressed(MouseEvent e) {
        if (!model.isRunning()) {
            int x = e.getX();
            int y = e.getY();
            if (!isBelongToObjects(new Point(x, y))) {
                if (e.getModifiers() == e.BUTTON1_MASK) {
                    //Нажата левая кнопка мыши вне существующих объектов
                    startPoint = new Point(x, y);
                    drawing = true;
                }
            } else if (e.getModifiers() == e.BUTTON3) {
                //Нажата правая кнопка мыши
                if (objects.get(numBelongToObjects(new Point(x, y))).isShowVectors) pVectors.setState(true);
                else pVectors.setState(false);
                popupMenu.add(pVectors);
                popupMenu.add(pProperties);
                popupMenu.add(pDelete);
                popupMenu.show(model.getDataPane(), x, y);

                for (PObject object : objects) {
                    object.setHighlighted(object.isBelong(new Point(x, y)));
                }
                model.getDataPane().invalidate();

                pDelete.addActionListener(new ActionListener() {
                    //Удаление объекта
                    public void actionPerformed(ActionEvent e) {
                        objects.remove(numBelongToObjects(new Point(x, y)));
                    }
                });

                pProperties.addActionListener(new ActionListener() {
                    //Отображение панели свойств объекта
                    public void actionPerformed(ActionEvent e) {
                        ObjectPropertiesForm propertiesForm = new ObjectPropertiesForm(model.getCurrObject(), model.getTimestamp());
                    }
                });

                pVectors.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        objects.get(numBelongToObjects(new Point(x, y))).isShowVectors = (e.getStateChange() == 1);
                        objects.get(numBelongToObjects(new Point(x, y))).setHighlighted(true);
                    }
                });
            }
        }
    }

    /**
     * Завершение создания объекта
     * @param e Событие мыши с координатами
     */
    public void mouseReleased(MouseEvent e) {
        if (!model.isRunning()) {
            int x = e.getX();
            int y = e.getY();

            if (drawing && e.getModifiers() == e.BUTTON1_MASK) {
                switch (model.getNumCurrObject()) {
                    case 1:
                        PObject object = new MaterialPoint(startPoint, new Point(x, y), stepTimeInMilliSeconds, model);
                        model.addObject(object);
                        model.getDataPane().invalidate();
                        model.getDataPane().repaint();
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        drawing = false;
    }

    public void mouseDragged(MouseEvent e) {
    }
}
