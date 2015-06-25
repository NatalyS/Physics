package ru.spb.herzen.physics;

import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Model2D extends GenericModel {
    private JPanel contentPanel;
    private JPanel dataPanel;
    private Ruler horizontalRuler;
    private Ruler verticalRuler;
    private int dpi;
    private double w, h;
    private CoordinateConverter coordinateConverter;

    public Model2D() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        setMeasurement(2);

        //Панель для горизонтальной линейки
        JPanel horizontalRulerPanel = new JPanel();
        horizontalRulerPanel.setLayout(new BorderLayout());

        //Заполнитель
        JLabel span = new JLabel();
        span.setPreferredSize(new Dimension(30, 30));       //fixme
        horizontalRulerPanel.add(span, BorderLayout.WEST);

        //Горизонтальная линейка
        horizontalRuler = new Ruler();
        horizontalRuler.setSize(new Dimension(100, 30));
        horizontalRuler.setMax(10);
        horizontalRuler.setMin(0);
        horizontalRuler.setMinorTicks(5);
        horizontalRuler.unitsPerPixel = 1.0 / 100;
        horizontalRulerPanel.add(horizontalRuler, BorderLayout.CENTER);

        contentPanel.add(horizontalRulerPanel, BorderLayout.NORTH);

        //Вертикальная линейка
        verticalRuler = new Ruler();
        verticalRuler.setSize(new Dimension(30, 100)); //(int)contentPanel.getPreferredSize().getHeight()));
        verticalRuler.setPreferredSize(new Dimension(30, 100));
        verticalRuler.setPosition(Ruler.LEFT);
        verticalRuler.setMin(0);
        verticalRuler.setMax(20);
        verticalRuler.setMinorTicks(5);
        verticalRuler.unitsPerPixel = 1.0 / 100;
        contentPanel.add(verticalRuler, BorderLayout.WEST);

        dataPanel = new Panel2D(this);
        contentPanel.putClientProperty("data", this);
        dataPanel.setLayout(new GridLayout(0, 1));
        contentPanel.add(dataPanel, BorderLayout.CENTER);

    }

    public Ruler getHorizontalRuler() {
        return horizontalRuler;
    }

    public Ruler getVerticalRuler() {
        return verticalRuler;
    }

    public JPanel getContentPane() {
        return contentPanel;
    }

    public JPanel getDataPane() {
        return dataPanel;
    }

    public void reset() {
        this.getDataPane().removeAll();
    }

    public CoordinateConverter getCoordinateConverter() {
        return coordinateConverter;
    }

    @Override
    public void setCoordinateConverter(CoordinateConverter coordinateConverter) {
        this.coordinateConverter = coordinateConverter;
    }

    public void drawAll(Graphics g) {
        ArrayList<PObject> objects = this.getObjects();
        for (PObject object : objects) {
            object.draw(g, getTimestamp());
        }
    }

    @Override
    public HashMap<String, Object> getState() {
        HashMap<String, Object> state = super.getState();
        state.put("scale", "1");
        return state;
    }

    @Override
    public void setState(HashMap<String, Object> state) {
        super.setState(state);
        //additional
    }
}

class Panel2D extends JPanel {
    private Model model;

    Panel2D(Model model) {
        super();
        this.model = model;
    }

    public void paint(Graphics g) {
        super.paint(g);

        int width = this.getWidth();
        int height = this.getHeight();

        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();

        double w = ((double) width / dpi * 25.4);      //in mm
        double h = ((double) height / dpi * 25.4);

        ((Model2D) model).getHorizontalRuler().setMin(0);
        ((Model2D) model).getVerticalRuler().setMin(0);
        ((Model2D) model).getHorizontalRuler().setMax(w / 10);     //Перевести в см
        ((Model2D) model).getVerticalRuler().setMax(h / 10);
        this.model.setCoordinateConverter(new CoordinateConverter2D(w, h, this.getWidth(), this.getHeight()));

        Dimension clipRect = this.getSize();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, clipRect.width - 1, clipRect.height - 1);
        model.drawAll(g);
    }
}

class CoordinateConverter2D implements CoordinateConverter {

    private double w_mm;
    private double h_mm;
    private int width;
    private int height;

    public CoordinateConverter2D(double w_mm, double h_mm, int width, int height) {
        this.w_mm = w_mm;
        this.h_mm = h_mm;
        this.width = width;
        this.height = height;
    }

    @Override
    public Point toScreen(Point3d point) {
        return new Point((int) (width * point.getX() / w_mm), (int) (height * (1 - point.getY() / h_mm)));
    }

    @Override
    public Point3d toPoint3d(Point point) {
        return new Point3d((int) (point.getX() * w_mm / width), (int) ((h_mm * (1 - point.getY() / height))), 0);
    }

    @Override
    public int scaleSize(double size) {
        return (int) (width * size / w_mm);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}