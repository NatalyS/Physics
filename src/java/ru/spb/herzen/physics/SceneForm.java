package ru.spb.herzen.physics;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Timer;

public class SceneForm extends JFrame {
    private final JCheckBoxMenuItem mPoint = new JCheckBoxMenuItem("Точка");
    private final JCheckBoxMenuItem mPlane = new JCheckBoxMenuItem("Плоскость");
    private Color COLOR = new Color(0xE6E6FA);
    private Font FONT = new Font("Calibri", Font.PLAIN, 13);
    private JPanel panelMain;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel scenePropertiesPanel;
    private JPanel timeSliderPanel;
    private int dividerTime = 1000;
    private int stepTimeInMilliSeconds = 100;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu mFile = new JMenu("Файл");
    private JMenuItem create = new JMenuItem("Создать");
    private JMenuItem open = new JMenuItem("Открыть");
    private JMenuItem saveAs = new JMenuItem("Сохранить как");
    private JMenuItem clear = new JMenuItem("Очистить");
    private JMenuItem exit = new JMenuItem("Выход");
    private JMenu mView = new JMenu("Вид");
    private JCheckBoxMenuItem mToolBar = new JCheckBoxMenuItem("Панель инструментов", true);
    private JMenu mObjects = new JMenu("Объекты");
    private JMenu mRun = new JMenu("Движение");
    private JMenuItem run = new JMenuItem("Запуск");
    private JMenuItem stop = new JMenuItem("Остановка");
    private JMenuItem resetTimer = new JMenuItem("Сброс таймера");
    private JMenuItem analysis = new JMenuItem("Анализ");
    private JMenu mWindow = new JMenu("Окно");
    private JMenuItem screen = new JMenuItem("Снимок экрана");
    private JMenu mHelp = new JMenu("Справка");

    private JToolBar toolBar = new JToolBar();
    private JButton openButton = new JButton(new ImageIcon("resources/open_icon.png"));
    private JButton saveButton = new JButton(new ImageIcon("resources/save_icon.png"));
    private JButton screenButton = new JButton(new ImageIcon("resources/screen_icon.png"));
    private JButton runButton = new JButton(new ImageIcon("resources/play_icon.png"));
    private JButton resetTimerButton = new JButton(new ImageIcon("resources/timer_icon.png"));

    private JComboBox<String> measurementsComboBox = new JComboBox<String>();
    private JComboBox<String> environmentComboBox = new JComboBox<String>();
    private JLabel viscosityLabel = new JLabel();
    private JCheckBox resistanceForceCheckBox = new JCheckBox("Сила сопротивления", true);
    private JTextField accelerationTextField = new JTextField();
    private JSpinner decelerationSpinner = new JSpinner();
    private JLabel timeLabel = new JLabel("Общее время: 0 с");
    private JLabel currTimeLabel = new JLabel("Текущее время: 0 с");

    private JSlider timeSlider = new JSlider(JSlider.HORIZONTAL);

    private ArrayList<Model> scenePanels = new ArrayList<Model>();
    private Model currentModel;

    private ArrayList<PObject> objects = new ArrayList<PObject>();
    private Timer timer = new java.util.Timer();

    private Canvas3D canvas3D;
    private GraphicsContext3D graphicsContext3D;
    private TransformGroup transformGroup = new TransformGroup();

    public SceneForm() {
        super();
        setContentPane(panelMain);
        setSize(500, 400);
        setExtendedState(NORMAL | MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Меню
        setJMenuBar(menuBar);
        panelMain.add(toolBar, BorderLayout.NORTH);
        setMenuBar();
        setToolBar();
        scenePropertiesPanel = new JPanel();
        timeSliderPanel = new JPanel();

        measurementsComboBox.setToolTipText("Количество измерений");
        environmentComboBox.setToolTipText("Окружающая среда");
        decelerationSpinner.setToolTipText("Коэффициент замедления");
        resistanceForceCheckBox.setToolTipText("Учет силы сопротивления");

        measurementsComboBox.setBackground(Color.white);
        environmentComboBox.setBackground(Color.white);
        viscosityLabel.setBackground(Color.white);
        decelerationSpinner.setBackground(Color.white);
        resistanceForceCheckBox.setBackground(Color.white);
        accelerationTextField.setBackground(Color.white);

        timeLabel.setFont(FONT);
        viscosityLabel.setFont(FONT);
        currTimeLabel.setFont(FONT);
        measurementsComboBox.setFont(FONT);
        environmentComboBox.setFont(FONT);
        decelerationSpinner.setFont(FONT);
        resistanceForceCheckBox.setFont(FONT);
        accelerationTextField.setFont(FONT);

        measurementsComboBox.addItem("2D");
        measurementsComboBox.addItem("3D");

        environmentComboBox.addItem("Воздух");
        environmentComboBox.addItem("Вода");
        environmentComboBox.addItem("Глицерин");

        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 20, 1);
        decelerationSpinner.setModel(spinnerModel);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) decelerationSpinner.getEditor();
        UIManager.put("FormattedTextField.inactiveBackground", Color.WHITE);
        editor.getTextField().setEditable(false);
        decelerationSpinner.setEditor(editor);

        timeSlider.setEnabled(false);
        timeSlider.getModel().setValue(0);
        timeSlider.getModel().setMinimum(0);

        timeSlider.getModel().setMaximum(0);
        timeSlider.setMajorTickSpacing(stepTimeInMilliSeconds * 10);
        timeSlider.setMinorTickSpacing(stepTimeInMilliSeconds);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timeSlider.setSnapToTicks(true);

        JPanel scenePropertiesSubPanel = new JPanel();
        scenePropertiesSubPanel.setMaximumSize(new Dimension(300, 0));
        scenePropertiesPanel.add(scenePropertiesSubPanel);
        scenePropertiesSubPanel.setLayout(new GridLayout(0, 1));
        GridBagConstraints vertical = new GridBagConstraints();
        vertical.fill = GridBagConstraints.VERTICAL;

        timeSliderPanel.setLayout(new BoxLayout(timeSliderPanel, BoxLayout.X_AXIS));

        measurementsComboBox.setAlignmentY(JComponent.TOP_ALIGNMENT);
        measurementsComboBox.setMaximumSize(new Dimension(300, 30));
        environmentComboBox.setMaximumSize(new Dimension(300, 30));
        viscosityLabel.setMaximumSize(new Dimension(300, 60));
        resistanceForceCheckBox.setMaximumSize(new Dimension(300, 30));
        accelerationTextField.setMaximumSize(new Dimension(300, 30));
        decelerationSpinner.setMaximumSize(new Dimension(300, 30));
        timeLabel.setMaximumSize(new Dimension(300, 30));
        currTimeLabel.setMaximumSize(new Dimension(300, 30));

        scenePropertiesSubPanel.setBackground(Color.white);
        timeSliderPanel.setBackground(Color.white);
        decelerationSpinner.setBackground(Color.white);

        JLabel label = new JLabel("<html>Ускорение свободного падения, м/с<sup>2</sup>:<html>");
        label.setFont(FONT);
        label.setMaximumSize(new Dimension(300, 30));

        scenePropertiesSubPanel.add(measurementsComboBox, vertical);
        scenePropertiesSubPanel.add(environmentComboBox, vertical);
        scenePropertiesSubPanel.add(viscosityLabel, vertical);
        scenePropertiesSubPanel.add(resistanceForceCheckBox, vertical);
        scenePropertiesSubPanel.add(label, vertical);
        scenePropertiesSubPanel.add(accelerationTextField, vertical);
        scenePropertiesSubPanel.add(decelerationSpinner, vertical);
        scenePropertiesSubPanel.add(timeLabel, vertical);
        scenePropertiesSubPanel.add(currTimeLabel, vertical);

        timeSliderPanel.add(timeSlider);

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(FONT);
        tabbedPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                if (tabbedPane.getTabCount() != 0) {
                    if (!pane.getTitleAt(pane.getSelectedIndex()).equals("Графики")) {

                        scenePropertiesPanel.setVisible(true);
                        timeSliderPanel.setVisible(true);

                        JPanel selected = (JPanel) pane.getSelectedComponent();
                        currentModel = (Model) selected.getClientProperty("data");

                        if (currentModel.getMeasurement() == 2) {
                            measurementsComboBox.setSelectedIndex(0);
                        } else {
                            measurementsComboBox.setSelectedIndex(1);
                        }

                        decelerationSpinner.setValue(currentModel.getDeceleration());
                        timeLabel.setText("Общее время: " + (double) currentModel.getTotalTime() / dividerTime + " с");
                        currTimeLabel.setText("Текущее время: " + (double) currentModel.getTimestamp() / dividerTime + " с");
                        environmentComboBox.setSelectedIndex(currentModel.getEnvironment());
                        resistanceForceCheckBox.setSelected(currentModel.isHaveResistanceForce());
                        viscosityLabel.setText("<html>Динамическая вязкость среды: <br/>" + currentModel.getConstants().getDynamicViscosityOfTheEnvironment() + " Н&middot;с/м<sup>2</sup></html>");
                        accelerationTextField.setText(String.valueOf(currentModel.getConstants().getAccelerationOfGravity()));
                        timeSlider.setValue(currentModel.getTimestamp());
                        timeSlider.setMaximum(currentModel.getTotalTime());
                    } else {
                        scenePropertiesPanel.setVisible(false);
                        timeSliderPanel.setVisible(false);
                    }
                }
            }
        });
        panelMain.add(tabbedPane, BorderLayout.CENTER);
        panelMain.add(scenePropertiesPanel, BorderLayout.EAST);
        scenePropertiesPanel.setVisible(false);
        panelMain.add(timeSliderPanel, BorderLayout.SOUTH);
        timeSliderPanel.setVisible(false);

        create.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JPanel newPanel = new JPanel();
                currentModel = new Model2D();

                int countScene = 0;
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    Object obj = ((JPanel) tabbedPane.getComponentAt(i)).getClientProperty("count");
                    if (obj != null) {
                        String count = String.valueOf(obj);
                        if (Integer.parseInt(count) > countScene) countScene = Integer.parseInt(count);
                    }
                }
                countScene++;

                tabbedPane.add("Сцена " + countScene, currentModel.getContentPane());
                currentModel.getContentPane().putClientProperty("count", String.valueOf(countScene));
                tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));

                PMouseListener listener = new PMouseListener(currentModel, stepTimeInMilliSeconds);
                currentModel.getDataPane().addMouseListener(listener);
                currentModel.getDataPane().addMouseMotionListener(listener);

                scenePanels.add(currentModel);
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        });

        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });
        openButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                open();
            }
        });

        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        screen.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                screenShot();
            }
        });
        screenButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                screenShot();
            }
        });

        accelerationTextField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentModel.getConstants().setAccelerationOfGravity(Double.parseDouble(accelerationTextField.getText()));
            }
        });

        environmentComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();

                int index = box.getSelectedIndex();
                currentModel.setEnvironment(index);
                currentModel.getConstants().setDynamicViscosityOfTheEnvironment(index);

                viscosityLabel.setText("<html>Динамическая вязкость среды: <br/>" + currentModel.getConstants().getDynamicViscosityOfTheEnvironment() + " Н&middot;с/м<sup>2</sup></html>");
                scenePropertiesPanel.revalidate();
            }
        });

        measurementsComboBox.addActionListener(new ActionListener() {

            /**
             * Переключение двухмерного и трехмерного режима отображения
             *
             * @param e Событие
             */
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                if (box.getSelectedItem().equals("2D")) {
                    if (!(currentModel instanceof Model2D)) {
                        currentModel = new Model2D();
                        tabbedPane.setComponentAt(tabbedPane.getSelectedIndex(), currentModel.getContentPane());

                    } else {
                        currentModel.reset();
                    }
                } else {
                    if (!(currentModel instanceof Model3D)) {
                        currentModel = new Model3D();
                        tabbedPane.setComponentAt(tabbedPane.getSelectedIndex(), currentModel.getContentPane());
                    } else {
                            currentModel.reset();
                        }
                }
            }
        });

        resistanceForceCheckBox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                currentModel.setHaveResistanceForce(e.getStateChange()==1);
            }
        });

        decelerationSpinner.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                currentModel.setDeceleration((Integer) spinner.getValue());
            }
        });

        clear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                while (!objects.isEmpty()) {
                    objects.remove(objects.size() - 1);
                    currentModel.getDataPane().invalidate();
                    currentModel.getDataPane().repaint();
                }
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mToolBar.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    setToolBar();
                    panelMain.add(toolBar, BorderLayout.NORTH);
                    panelMain.repaint();
                }
                if (e.getStateChange() == 2) {
                    panelMain.remove(toolBar);
                    panelMain.repaint();
                }
            }
        });

        mPoint.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    currentModel.setNumCurrObject(1);
                    mPlane.setState(false);
                }
                if (e.getStateChange() == 2) {
                    currentModel.setNumCurrObject(0);
                }
            }
        });

        mPlane.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    currentModel.setNumCurrObject(2);
                    mPoint.setState(false);
                }
                if (e.getStateChange() == 2) {
                    currentModel.setNumCurrObject(0);
                }
            }
        });

        runButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!currentModel.isRunning()) {
                    runModelling();
                    runButton.setIcon(new ImageIcon("resources/pause_icon.png"));
                    runButton.setToolTipText("Остановка");
                } else {
                    stopModelling();
                    runButton.setIcon(new ImageIcon("resources/play_icon.png"));
                    runButton.setToolTipText("Запуск");
                }
            }
        });
        run.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                runModelling();
            }
        });
        stop.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stopModelling();
            }
        });

        resetTimer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!currentModel.isRunning())
                    resettingTimer();
            }
        });

        resetTimerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!currentModel.isRunning())
                    resettingTimer();
            }
        });

        analysis.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphicsPanel graphicsPanel = new GraphicsPanel(currentModel.getCurrObject(), currentModel.getTotalTime(), dividerTime, currentModel.getObjects().get(0));
                graphicsPanel.setBackground(Color.white);
                tabbedPane.add("Графики", new JScrollPane(graphicsPanel));
                tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
                panelMain.remove(scenePropertiesPanel);
                panelMain.remove(timeSliderPanel);
            }
        });

        timeSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (!currentModel.isRunning()) {
                    if (isTimeRecorded(timeSlider.getValue())) {
                        System.out.println("--- TIME SLIDER ---");
                        System.out.println("curr time: " + currentModel.getTimestamp());
                        System.out.println("time in slider: " + timeSlider.getValue());
                        currentModel.setTimestamp(timeSlider.getValue());
                        currTimeLabel.setText("Текущее время: " + (double) currentModel.getTimestamp() / dividerTime + " с");
                        currentModel.getDataPane().invalidate();
                        currentModel.getDataPane().repaint();
                    }
                }
            }
        });

        ImageIcon icon = new ImageIcon("resources/icon.png");
        setIconImage(icon.getImage());

        setTitle("Физические задачи");
        setVisible(true);
    }

    public static void main(String[] args) {
        SceneForm form = new SceneForm();
    }

    private boolean isTimeRecorded(int timeInMilliSeconds) {
        ArrayList<Integer> times = currentModel.getObjects().get(1).dataBase.times;
        for (int i = 0; i < times.size(); i++) {
            if (timeInMilliSeconds == times.get(i))
                return true;
        }
        return false;
    }

    private void setMenuBar() {
        menuBar.setBackground(COLOR);

        mFile.setFont(FONT);
        create.setFont(FONT);
        open.setFont(FONT);
        saveAs.setFont(FONT);
        clear.setFont(FONT);
        exit.setFont(FONT);
        mView.setFont(FONT);
        mToolBar.setFont(FONT);
        mObjects.setFont(FONT);
        mPoint.setFont(FONT);
        mPlane.setFont(FONT);
        mRun.setFont(FONT);
        run.setFont(FONT);
        stop.setFont(FONT);
        resetTimer.setFont(FONT);
        analysis.setFont(FONT);
        mWindow.setFont(FONT);
        screen.setFont(FONT);
        mHelp.setFont(FONT);

        create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        screen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        mFile.add(create);
        mFile.add(open);
        mFile.add(saveAs);
        mFile.addSeparator();
        mFile.add(clear);
        mFile.addSeparator();
        mFile.add(exit);

        mView.add(mToolBar);

        mObjects.add(mPoint);
        mObjects.add(mPlane);
        //   mObjects.add(mSpring);

        mRun.add(run);
        mRun.add(stop);
        mRun.addSeparator();
        mRun.add(resetTimer);
        mRun.addSeparator();
        mRun.add(analysis);

        mWindow.add(screen);

        menuBar.add(mFile);
        menuBar.add(mView);
        menuBar.add(mObjects);
        menuBar.add(mRun);
        menuBar.add(mWindow);
        menuBar.add(mHelp);
    }

    private void setToolBar() {
        toolBar.setBackground(COLOR);

        openButton.setToolTipText("Открыть");
        saveButton.setToolTipText("Сохранить");
        screenButton.setToolTipText("Сделать снимок экрана");
        runButton.setToolTipText("Запуск");
        resetTimerButton.setToolTipText("Сброс таймера");

        openButton.setBorderPainted(false);
        saveButton.setBorderPainted(false);
        screenButton.setBorderPainted(false);
        runButton.setBorderPainted(false);
        resetTimerButton.setBorderPainted(false);

        openButton.setBackground(COLOR);
        saveButton.setBackground(COLOR);
        screenButton.setBackground(COLOR);
        runButton.setBackground(COLOR);
        resetTimerButton.setBackground(COLOR);

        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(screenButton);
        toolBar.addSeparator();
        toolBar.add(runButton);
        toolBar.addSeparator();
        toolBar.add(resetTimerButton);
        toolBar.addSeparator();

        toolBar.setFloatable(false);
        toolBar.setBorder(new LineBorder(Color.gray));
    }

    /**
     * Загрузка сцены
     */
    private void open() {
        FileDialog fileDialog = new FileDialog(new Frame(), "Открыть", FileDialog.LOAD);
        fileDialog.setFile("*.phy");
        fileDialog.setVisible(true);
        String fileName = fileDialog.getDirectory() + fileDialog.getFile();

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(fileName));
            HashMap<String, Object> state = (HashMap<String, Object>) obj;
            currentModel = new Model2D();
            currentModel.setState(state);
            String title = fileDialog.getFile();
            tabbedPane.add(title, currentModel.getContentPane());
            tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void save() {

        HashMap<String, Object> state = currentModel.getState();
        JSONObject obj = new JSONObject(state);
        try {

            FileDialog fileDialog = new FileDialog(new Frame(), "Сохранить", FileDialog.SAVE);
            fileDialog.setFile("*.phy");
            fileDialog.setVisible(true);
            String fileName = fileDialog.getDirectory() + fileDialog.getFile();

            FileWriter fw = new FileWriter(fileName);
            obj.write(fw);
            fw.flush();
            fw.close();

            PrintWriter pw = new PrintWriter(System.out);
            obj.write(new PrintWriter(pw));
            pw.flush();
            pw.close();
            currentModel.getContentPane().putClientProperty("count", null);
            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fileDialog.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Оценка времени до столкновения
     *
     * @param object1 Объект 1
     * @param object2 Объект 2
     * @return Время в секундах
     */
    private double getTimeConvergence(PObject object1, PObject object2) {
        double time = 0;
        double v2 = object2.getVelocity().getY();
        int y1 = (int) (object1.getCenter().getY() + object2.getHeight() / 2);
        int y2 = (int) object2.getCenter().getY();
        System.out.println("y1 : " + y1);
        System.out.println("y2 : " + y2);
        // y1 = y2 + v2 * t - g * t^2 / 2
        // (g / 2) * t^2 - v2 * t + (y1 - y2) = 0
        double a = currentModel.getConstants().getAccelerationOfGravity() / 2;
        double b = -v2;
        double c = y1 - y2;
        time = Math.max((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a),
                (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
        return time;
    }

    /**
     * Основной цикл моделирования
     */
    private void runModelling() {
        currentModel.setRunning(true);
        System.out.println("--- RUNNING ---");
        timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                double t = 0;
                double S = 0;
                Point3d centerI;
                for (PObject object : currentModel.getObjects()) {
                    centerI = object.getCenterInTime();
                    System.out.println("centerI : " + centerI);
                    if (object.isMobile() && currentModel.getObjects().size() > 1) {
                        S = Math.sqrt(Math.pow(centerI.getX() - object.getCenter().getX(), 2)
                                + Math.pow(centerI.getY() - object.getCenter().getY(), 2) +
                                Math.pow(centerI.getZ() - object.getCenter().getZ(), 2));

                        boolean collided = false;
                        for (PObject compare : currentModel.getObjects()) {
                            if (compare != object) {
                                t = 0;
                                System.out.println("Distance : " + compare.getDistance(new Point3d((int) object.getCenter().getX(), (int) object.getCenter().getY(), 0)));
                                System.out.println("Object Height : " + object.getHeight() / 2);
                                System.out.println("S : " + S);

                                System.out.println("______   " + compare.getDistance(new Point3d((int) object.getCenter().getX(), (int) object.getCenter().getY(), 0)) + " ? " + compare.getDistance(new Point3d((int) centerI.getX(), (int) centerI.getY(), 0)));
                                System.out.println("______   " + S + " ? " + (object.getDistance(new Point3d((int) object.getCenter().getX(), (int) object.getCenter().getY(), 0)) - object.getHeight() / 2));
                                if (compare.getDistance(new Point3d((int) object.getCenter().getX(), (int) object.getCenter().getY(), 0)) > compare.getDistance(new Point3d((int) centerI.getX(), (int) centerI.getY(), 0))
                                        && S >= compare.getDistance(new Point3d((int) object.getCenter().getX(), (int) object.getCenter().getY(), 0)) - object.getHeight() / 2) {
                                    System.out.println(S + " >= " + (compare.getDistance(new Point3d((int) object.getCenter().getX(), (int) object.getCenter().getY(), 0)) - object.getHeight() / 2));
                                    // Вычисление времени сближения объектов
                                    t = getTimeConvergence(compare, object);
                                    System.out.println("t = " + t);
                                    // Фиксация столкновения
                                    object.move((int) (t * dividerTime), dividerTime, currentModel.getDeceleration());
                                    object.addCollidingObject(compare);
                                    System.out.println(object.collidingObjects);
                                    if (!object.isElasticCollision()) { // Если столкновение неупругое
                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                        object.setMobile(false); // Обездвиживание объекта
                                    }
                                }
                                collided = true;
                                System.out.println(object.getCurrCollidingObjects());
                                object.move(stepTimeInMilliSeconds - (int) (t * dividerTime), dividerTime, currentModel.getDeceleration());
                                currentModel.getDataPane().invalidate();
                                currentModel.getDataPane().repaint();
                            }
                        }
                        if (!collided) {
                            object.move(stepTimeInMilliSeconds, dividerTime, currentModel.getDeceleration());
                            currentModel.getDataPane().invalidate();
                            currentModel.getDataPane().repaint();
                        }
                    } else {
                        object.move(stepTimeInMilliSeconds, dividerTime, currentModel.getDeceleration());
                        currentModel.getDataPane().invalidate();
                        currentModel.getDataPane().repaint();
                    }
                }
                int newTimes = currentModel.getTotalTime() + stepTimeInMilliSeconds;
                currentModel.setTotalTime(newTimes);
                currentModel.setTimestamp(newTimes);
                timeLabel.setText("Общее время: " + (double) currentModel.getTotalTime() / dividerTime + " с");
                currTimeLabel.setText("Текущее время: " + (double) currentModel.getTimestamp() / dividerTime + " с");
            }
        }, 0, stepTimeInMilliSeconds);

        timeSlider.setEnabled(false);
        measurementsComboBox.setEnabled(false);
        decelerationSpinner.setEnabled(false);
    }

    /**
     * Остановка таймера
     */
    private void stopModelling() {
        timer.cancel();

        System.out.println("--- STOPPING ---");
        currentModel.setRunning(false);

        Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();
        for (int i = 0; i <= currentModel.getTotalTime(); i = i + stepTimeInMilliSeconds * 10) {
            labelTable.put(i, new JLabel(String.valueOf(i / dividerTime)));
        }

        timeSlider.setLabelTable(labelTable);

        System.out.println("==== set maximum ====");
        timeSlider.setMaximum(currentModel.getTotalTime());
        System.out.println("==== set value ====");
        timeSlider.setValue(currentModel.getTotalTime());

        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timeSlider.setSnapToTicks(true);

        timeSlider.setEnabled(true);
        measurementsComboBox.setEnabled(true);
        decelerationSpinner.setEnabled(true);
    }

    /**
     * Сброс таймера
     */
    private void resettingTimer() {
        currentModel.setTimestamp(0);
        currentModel.setTotalTime(0);
        for (PObject object : currentModel.getObjects()) {
            object.goToStartPosition();
        }
        timeLabel.setText("Общее время: 0 с");
        currTimeLabel.setText("Текущее время: 0 с");
        timeSlider.setValue(0);
        timeSlider.setMaximum(0);
        timeSlider.setEnabled(false);
    }

    /**
     * Создание скриншота экрана и запись в jpg
     */
    private void screenShot() {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        FileDialog fileDialog = new FileDialog(new Frame(), "Сохранить снимок экрана", FileDialog.SAVE);
        fileDialog.setFile("*.jpg; *.jpeg");
        fileDialog.setVisible(true);
        String fileName = fileDialog.getDirectory() + fileDialog.getFile();
        try {
            ImageIO.write(screenShot, "JPG", new File(fileName));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}

