package ru.spb.herzen.physics;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ObjectPropertiesForm extends JFrame {
    private JPanel panel;
    private JButton saveButton;
    private JButton cancelButton;
    private JTable table2;
    private JPanel table2Panel;
    private JPanel colorPanel;
    private JButton colorButton;
    private JLabel colorLabel;
    private JButton textureButton;
    private JLabel textureLabel;
    private JLabel mobileLabel;
    private JComboBox mobileComboBox;
    private JLabel collisionLabel;
    private JComboBox collisionComboBox;
    private JLabel label0;
    private JLabel label1;
    private JPanel timePanel;
    private JTable timeTable;
    private JPanel table3Panel;
    private JTable table3;
    private JTable table1;
    private JPanel table1Panel;

    private Color color;
    private Image image;
    private BufferedImage originalImage;
    private boolean isMobile;
    private boolean isElasticCollision;

    private Font FONT = new Font("Calibri", Font.PLAIN, 13);

    private int countVariableParameters = 0;

    public ObjectPropertiesForm(final PObject object, int currTimeInMilliseconds) {
        super();
        setContentPane(panel);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Таблица с текущим и общим временами движения

        timeTable = new PTable(false);
        DefaultTableModel timeTableModel = new DefaultTableModel(2, 3);
        timeTable.setModel(timeTableModel);
        timeTable.setDefaultRenderer(Object.class, null);
        timeTable.setDefaultRenderer(Object.class, new PRenderer());
        timeTable.getTableHeader().setResizingAllowed(false);
        timeTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        timeTable.getColumnModel().getColumn(2).setPreferredWidth(35);
        timeTable.getColumnModel().getColumn(2).setMaxWidth(35);

        timeTable.setValueAt("Текущее время движения", 0, 0);
        timeTable.setValueAt((double) currTimeInMilliseconds / 1000, 0, 1);
        timeTable.setValueAt(" с", 0, 2);

        timeTable.setValueAt("Общее время движения", 1, 0);
        timeTable.setValueAt((double) object.dataBase.times.get(object.dataBase.times.size() - 1) / 1000, 1, 1);
        timeTable.setValueAt(" с", 1, 2);

        // Таблица с размерами

        table1 = new PTable(true);
        DefaultTableModel table1Model = new DefaultTableModel(3, 3);
        table1.setModel(table1Model);
        table1.setDefaultRenderer(Object.class, null);
        table1.setDefaultRenderer(Object.class, new PRenderer());
        table1.getTableHeader().setResizingAllowed(false);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table1.getColumnModel().getColumn(2).setPreferredWidth(35);
        table1.getColumnModel().getColumn(2).setMaxWidth(35);

        table1.setValueAt("Масса", 0, 0);
        table1.setValueAt(object.getMass(), 0, 1);
        table1.setValueAt(" кг", 0, 2);

        table1.setValueAt("Ширина", 1, 0);
        table1.setValueAt(object.getWidth(), 1, 1);
        table1.setValueAt(" cм", 1, 2);

        table1.setValueAt("Высота", 2, 0);
        table1.setValueAt(object.getHeight(), 2, 1);
        table1.setValueAt(" cм", 2, 2);

        // Таблица с параметрами начального состояния объекта

        table2 = new PTable(true);
        DefaultTableModel table2Model = new DefaultTableModel(7, 3);
        countVariableParameters = 7;
        table2.setModel(table2Model);
        table2.setDefaultRenderer(Object.class, null);
        table2.setDefaultRenderer(Object.class, new PRenderer());
        table2.getTableHeader().setResizingAllowed(false);
        table2.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table2.getColumnModel().getColumn(2).setPreferredWidth(35);
        table2.getColumnModel().getColumn(2).setMaxWidth(35);

        table2.setValueAt("Начальная координата X", 0, 0);
        table2.setValueAt(object.getCenter0().getX(), 0, 1);

        table2.setValueAt("Начальная координата Y", 1, 0);
        table2.setValueAt(object.getCenter0().getY(), 1, 1);

        table2.setValueAt("Начальная координата Z", 2, 0);
        table2.setValueAt(object.getCenter0().getZ(), 2, 1);

        table2.setValueAt("Начальная скорость V", 3, 0);
        table2.setValueAt(object.getVelocity0().getValue(), 3, 1);
        table2.setValueAt(" м/с", 3, 2);

        table2.setValueAt("Угол начальной скорости к горизонту", 4, 0);
        table2.setValueAt(object.getVelocity0().getAzimuthAngle(), 4, 1);
        table2.setValueAt(" °", 4, 2);

        table2.setValueAt("Начальная скорость Vx", 5, 0);
        table2.setValueAt(object.getVelocity0().getX(), 5, 1);
        table2.setValueAt(" м/с", 5, 2);

        table2.setValueAt("Начальная скорость Vy", 6, 0);
        table2.setValueAt(object.getVelocity0().getY(), 6, 1);
        table2.setValueAt(" м/с", 6, 2);

        // Таблица с параметрами текущего состояния объекта

        table3 = new PTable(false);
        DefaultTableModel table3Model = new DefaultTableModel(18, 3);
        table3.setModel(table3Model);
        table3.setDefaultRenderer(Object.class, null);
        table3.setDefaultRenderer(Object.class, new PRenderer());
        table3.getTableHeader().setResizingAllowed(false);
        table3.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table3.getColumnModel().getColumn(2).setPreferredWidth(35);
        table3.getColumnModel().getColumn(2).setMaxWidth(35);

        table3.setValueAt("Текущая координата X", 0, 0);
        table3.setValueAt(object.getCenter().getX(), 0, 1);

        table3.setValueAt("Текущая координата Y", 1, 0);
        table3.setValueAt(object.getCenter().getY(), 1, 1);

        table3.setValueAt("Текущая координата Z", 2, 0);
        table3.setValueAt(object.getCenter().getZ(), 2, 1);

        table3.setValueAt("Текущая скорость V", 3, 0);
        table3.setValueAt(object.getVelocity().getAzimuthAngle(), 3, 1);
        table3.setValueAt(" м/с", 3, 2);

        table3.setValueAt("Угол текущей скорости к горизонту", 4, 0);
        table3.setValueAt(object.getVelocity().getAzimuthAngle(), 4, 1);
        table3.setValueAt(" °", 4, 2);

        table3.setValueAt("Текущая скорость Vx", 5, 0);
        table3.setValueAt(object.getVelocity().getX(), 5, 1);
        table3.setValueAt(" м/с", 5, 2);

        table3.setValueAt("Текущая скорость Vy", 6, 0);
        table3.setValueAt(object.getVelocity().getY(), 6, 1);
        table3.setValueAt(" м/с", 6, 2);

        table3.setValueAt("Текущее ускорение a", 7, 0);
        table3.setValueAt(object.getAcceleration().getValue(), 7, 1);
        table3.setValueAt(" м/с2", 7, 2);

        table3.setValueAt("Угол текущего ускорения к горизонту", 8, 0);
        table3.setValueAt(object.getAcceleration().getAzimuthAngle(), 8, 1);
        table3.setValueAt(" °", 8, 2);

        table3.setValueAt("Текущее ускорение ax", 9, 0);
        table3.setValueAt(object.getAcceleration().getX(), 9, 1);
        table3.setValueAt(" м/с2", 9, 2);

        table3.setValueAt("Текущее ускорение ay", 10, 0);
        table3.setValueAt(object.getAcceleration().getY(), 10, 1);
        table3.setValueAt(" м/с2", 10, 2);

        table3.setValueAt("Текущее перемещение S", 11, 0);
        table3.setValueAt(object.getShift().getValue(), 11, 1);
        table3.setValueAt(" м", 11, 2);

        table3.setValueAt("Текущее перемещение Sx", 12, 0);
        table3.setValueAt(object.getShift().getX(), 12, 1);
        table3.setValueAt(" м", 12, 2);

        table3.setValueAt("Текущее перемещение Sy", 13, 0);
        table3.setValueAt(object.getShift().getY(), 13, 1);
        table3.setValueAt(" м", 13, 2);

        table3.setValueAt("Текущая сила сопротивления Fсопр.", 14, 0);
        table3.setValueAt(object.getResistanceForce().getValue(), 14, 1);
        table3.setValueAt(" H", 14, 2);

        table3.setValueAt("Текущая сила сопротивления Fсопр.x", 15, 0);
        table3.setValueAt(object.getResistanceForce().getX(), 15, 1);
        table3.setValueAt(" H", 15, 2);

        table3.setValueAt("Текущая сила сопротивления Fсопр.y", 16, 0);
        table3.setValueAt(object.getResistanceForce().getY(), 16, 1);
        table3.setValueAt(" H", 16, 2);

        table3.setValueAt("Текущая сила тяжести Fтяж.", 17, 0);
        table3.setValueAt(object.getMass() * object.constants.getAccelerationOfGravity(), 17, 1);
        table3.setValueAt(" H", 17, 2);

        timePanel.add(timeTable, BorderLayout.CENTER);
        table1Panel.add(table1, BorderLayout.CENTER);
        table2Panel.add(table2, BorderLayout.CENTER);
        table3Panel.add(table3, BorderLayout.CENTER);

        mobileLabel.setFont(FONT);
        collisionLabel.setFont(FONT);
        colorLabel.setFont(FONT);
        textureLabel.setFont(FONT);
        mobileComboBox.setFont(FONT);
        collisionComboBox.setFont(FONT);
        saveButton.setFont(FONT);
        cancelButton.setFont(FONT);

        mobileComboBox.addItem("Да");
        mobileComboBox.addItem("Нет");

        isMobile = object.isMobile();
        if (isMobile)
            mobileComboBox.setSelectedIndex(0);
        else mobileComboBox.setSelectedIndex(1);

        collisionComboBox.addItem("Упругое");
        collisionComboBox.addItem("Неупругое");

        isElasticCollision = object.isElasticCollision();
        if (isElasticCollision)
            collisionComboBox.setSelectedIndex(0);
        else collisionComboBox.setSelectedIndex(1);

        color = object.getColor();
        colorButton.setBackground(color);

        mobileComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                if (box.getSelectedIndex() == 0)
                    isMobile = true;
                else isMobile = false;
            }
        });

        collisionComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                if (box.getSelectedIndex() == 0)
                    isElasticCollision = true;
                else isElasticCollision = false;
            }
        });

        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = JColorChooser.showDialog(new Frame(), "Выбор цвета", color);
                colorButton.setBackground(color);
            }
        });

        textureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog(new Frame(), "Открыть файл с текстурой", FileDialog.LOAD);
                fileDialog.setFile("*.jpg; *jpeg; *png");
                fileDialog.setVisible(true);

                String fileName = fileDialog.getDirectory() + fileDialog.getFile();
                try {
                    originalImage = ImageIO.read(new File(fileName));
                    int width = 150;
                    int height = 100;
                    image = originalImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
                    BufferedImage changedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = changedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
                    textureButton.setIcon(new ImageIcon(changedImage));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                object.setCenter0(Double.parseDouble(table2.getValueAt(0, 1).toString()), Double.parseDouble(table2.getValueAt(1, 1).toString()), Double.parseDouble(table2.getValueAt(2, 1).toString()));
                object.setVelocity0(new PVector3D(Double.parseDouble(table2.getValueAt(3, 1).toString()), Double.parseDouble(table2.getValueAt(4, 1).toString()), 0, true));

                object.startRecord();
                object.dataBase.velocities.put(0, object.getVelocity());
                object.setMobile(isMobile);
                object.setElasticCollision(isElasticCollision);
                object.setColor(color);
                if (originalImage != null) {
                    object.setAppearance(originalImage);
                }
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        ImageIcon icon = new ImageIcon("src/main/resources/properties_icon.png");
        setIconImage(icon.getImage());
        setTitle("Свойства объекта");
        setVisible(true);
    }

    /*
        Класс таблицы с запретом изменения некоторых ячеек
     */
    class PTable extends JTable {

        private boolean isColumnValuesEditable = true;

        public PTable(boolean isColumnValuesEditable) {
            this.isColumnValuesEditable = isColumnValuesEditable;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 1)
                return isColumnValuesEditable;
            else return false;
        }
    }

    class PRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
        {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            this.setHorizontalAlignment(JLabel.LEFT);
            Font font = new Font("Calibri", Font.PLAIN, 13);
            comp.setFont(font);
            return comp;
        }

    }

}