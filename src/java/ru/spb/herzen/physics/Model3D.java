package ru.spb.herzen.physics;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Класс для описания 3D-модели визуализации сцены
 */
public class Model3D extends GenericModel {
    private Canvas3D canvas3D;                      //Canvas для отображения объекта
    private GraphicsContext3D graphicsContext3D;    //Графический контекст
    private JPanel contentPanel;                    //Панель Swing для отображения сцены
    private BranchGroup contentBranch;              //Ветка графа с объектами

    public Model3D() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        setMeasurement(3);
        buildScene(contentPanel);
    }

    public JPanel getContentPane() {
        return contentPanel;
    }

    public JPanel getDataPane() {
        return contentPanel;
    }

    public void reset() {
    }

    @Override
    public CoordinateConverter getCoordinateConverter() {
        return new CoordinateConverter3D();
    }

    @Override
    public void setCoordinateConverter(CoordinateConverter coordinateConverter) {
    }

    @Override
    public void drawAll(Graphics g) {
    }

    public BranchGroup getContentBranch() {
        return contentBranch;
    }

    //Создание 3D-стрелки между указанными точками
    private TransformGroup createArrow(float ArrowDia, Vector3d stPt, Vector3d endPt) {
        double arrowDir = ((endPt.y - stPt.y) / Math.abs(endPt.y - stPt.y));
        double lenSq = (stPt.x - endPt.x) * (stPt.x - endPt.x) +
                (stPt.y - endPt.y) * (stPt.y - endPt.y) +
                (stPt.z - endPt.z) * (stPt.z - endPt.z);
        float ArrowLen = (float) Math.sqrt(lenSq);
        float ArrowHeadLen = 5.0f * ArrowDia;
        float ArrowHeadDia = 2.0f * ArrowDia;
        float ArrowTailDia = ArrowDia;
        float AroowTailLen = 2.5f * ArrowDia;
        float CylinderLen = ArrowLen - ArrowHeadLen - AroowTailLen;

        //Матрица поворота для стрелки
        Matrix4d RotMat = new Matrix4d();
        double CosTheta = (endPt.x - stPt.x) / ArrowLen;
        double SinTheta = (endPt.y - stPt.y) / ArrowLen;

        RotMat.m00 = SinTheta;
        RotMat.m01 = CosTheta;
        RotMat.m02 = 0.0f;
        RotMat.m03 = stPt.x + (endPt.x - stPt.x) / 2.0f -
                arrowDir * (ArrowHeadLen - AroowTailLen) / 2.0f;

        RotMat.m10 = -CosTheta;
        RotMat.m11 = SinTheta;
        RotMat.m12 = 0.0f;
        RotMat.m13 = stPt.y + (endPt.y - stPt.y) / 2.0f -
                arrowDir * (ArrowHeadLen - AroowTailLen) / 2.0f;

        RotMat.m20 = 0.0f;
        RotMat.m21 = 0.0f;
        RotMat.m22 = 1.0f;
        RotMat.m23 = stPt.z + (endPt.z - stPt.z) / 2.0f -
                arrowDir * (ArrowHeadLen - AroowTailLen) / 2.0f;

        RotMat.m30 = 0.0f;
        RotMat.m31 = 0.0f;
        RotMat.m32 = 0.0f;
        RotMat.m33 = 1.0f;


        //Отображение стрелки
        Appearance caAppearance = new Appearance();
        ColoringAttributes caColor;
        caColor = new ColoringAttributes();
        caColor.setColor(0.5f, 0.5f, 1f);
        caAppearance.setColoringAttributes(caColor);

        Transform3D caTransform = new Transform3D();
        caTransform.set(RotMat);
        TransformGroup caTransformGroup = new TransformGroup(caTransform);

        Node cArrowCylinder = new Cylinder(ArrowDia, CylinderLen, caAppearance);

        Transform3D arrowHeadTransform = new Transform3D();
        arrowHeadTransform.set(new
                Vector3f(0.0f, CylinderLen / 2.0f + 0.5f * ArrowHeadLen, 0.0f));
        TransformGroup arrowHeadTransformGroup = new TransformGroup(arrowHeadTransform);

        Transform3D arrowTailTransform = new Transform3D();
        arrowTailTransform.set(new
                Vector3f(0.0f, -CylinderLen / 2.0f - 0.5f * AroowTailLen, 0.0f));
        Transform3D arrowTailTransformTmp = new Transform3D();
        arrowTailTransformTmp.rotZ(Math.PI);
        arrowTailTransform.mul(arrowTailTransformTmp);
        TransformGroup arrowTailTransformGroup = new TransformGroup(arrowTailTransform);

        Node ArrowHeadCone = new Cone(ArrowHeadDia, ArrowHeadLen, 1, caAppearance);
        Node ArrowTailCone = new Cone(ArrowTailDia, AroowTailLen, 1, caAppearance);
        arrowHeadTransformGroup.addChild(ArrowHeadCone);
        arrowTailTransformGroup.addChild(ArrowTailCone);

        caTransformGroup.addChild(arrowHeadTransformGroup);
        caTransformGroup.addChild(arrowTailTransformGroup);
        caTransformGroup.addChild(cArrowCylinder);

        return caTransformGroup;
    }

    private void buildScene(JPanel panel1) {

        BorderLayout borderLayout = (BorderLayout) panel1.getLayout();
        if (borderLayout.getLayoutComponent(BorderLayout.CENTER) != null) {
            panel1.remove(borderLayout.getLayoutComponent(BorderLayout.CENTER));
        }
        //Сконструировать каркас для отображения сцены
        //Universe
        VirtualUniverse virtualUniverse = new VirtualUniverse();
        Locale locale = new Locale(virtualUniverse);
        GraphicsConfigTemplate3D graphicsConfigTemplate3D = new GraphicsConfigTemplate3D();
        graphicsConfigTemplate3D.setDoubleBuffer(GraphicsConfigTemplate.REQUIRED);
        graphicsConfigTemplate3D.setSceneAntialiasing(GraphicsConfigTemplate.REQUIRED);
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(graphicsConfigTemplate3D);
        canvas3D = new Canvas3D(graphicsConfiguration);
        graphicsContext3D = canvas3D.getGraphicsContext3D();
        //требовать двойную буферизацию
        graphicsContext3D.setFrontBufferRendering(false);
        canvas3D.setDoubleBufferEnable(true);

        this.getContentPane().add(canvas3D, BorderLayout.CENTER);
        //Камера
        locale.addBranchGraph(this.buildCamera(canvas3D));

        //Сцена
        BranchGroup sceneBranchGroup = new BranchGroup();
        TransformGroup sceneTransform = new TransformGroup();
        sceneTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        sceneTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        MouseWheelZoom mouseWheelZoom = new MouseWheelZoom();
        mouseWheelZoom.setTransformGroup(sceneTransform);
        mouseWheelZoom.setSchedulingBounds(new BoundingSphere());

        MouseRotate mouseRotate = new MouseRotate();
        mouseRotate.setTransformGroup(sceneTransform);
        mouseRotate.setSchedulingBounds(new BoundingSphere());

        sceneTransform.addChild(mouseRotate);         //поворот мышью
        sceneTransform.addChild(mouseWheelZoom);      //масштабирование мышью
        // Формирование сцены и поведения
        sceneBranchGroup.addChild(sceneTransform);

        contentBranch = new BranchGroup();
        contentBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        contentBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        contentBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        MaterialPoint mp = new MaterialPoint(new Point3d(0, 0, 0), 100, this);
        try {
            BufferedImage texture = ImageIO.read(new File("resources/wood-texture.jpg"));
            Appearance appearance = new Appearance();
            TextureLoader textureLoader = new TextureLoader(texture);
            Texture texture1 = textureLoader.getTexture();
            texture1.setBoundaryModeS(Texture.WRAP);
            texture1.setBoundaryModeT(Texture.WRAP);
            texture1.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            Material material = new Material();
            material.setSpecularColor(new Color3f(Color.WHITE));
            material.setDiffuseColor(new Color3f(Color.WHITE));
            appearance.setMaterial(material);
            appearance.setTexture(texture1);
            appearance.setTextureAttributes(texAttr);
            contentBranch.addChild(new Sphere(0.15f, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, 200, appearance));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;

        contentBranch.addChild(createArrow(0.02f, new Vector3d(0, 0, 0), new Vector3d(0, -0.8, 0)));

        contentBranch.compile();
        sceneBranchGroup.addChild(contentBranch);

        //Освещение
        sceneBranchGroup.addChild(this.getLights());
        sceneTransform.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        sceneTransform.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        sceneTransform.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        sceneTransform.setCapability(BranchGroup.ALLOW_DETACH);

        //Компиляция сцены и отображение
        sceneBranchGroup.compile();
        locale.addBranchGraph(sceneBranchGroup);
        this.update();
    }

    /**
     * Построить вид камеры
     *
     * @param canvas3D холст JFrame для рисования
     * @return представление камеры
     */
    private BranchGroup buildCamera(Canvas3D canvas3D) {
        PhysicalBody physicalBody = new PhysicalBody();
        PhysicalEnvironment physicalEnvironment = new PhysicalEnvironment();

        BranchGroup viewBranchGroup = new BranchGroup();
        View view = new View();
        view.setBackClipDistance(100000000);
        view.setFrontClipDistance(0.001);
        view.setPhysicalBody(physicalBody);
        view.setPhysicalEnvironment(physicalEnvironment);
        view.addCanvas3D(canvas3D);
        ViewPlatform viewPlatform = new ViewPlatform();
        view.attachViewPlatform(viewPlatform);

        //Смещение
        Transform3D viewTranslateTransform = new Transform3D();
        viewTranslateTransform.lookAt(new Point3d(1, 1, 0.5), new Point3d(0, 0, 0), new Vector3d(0, 0, 1));
        viewTranslateTransform.invert();
        TransformGroup cameraTranslateGroup = new TransformGroup(viewTranslateTransform);
        cameraTranslateGroup.addChild(viewPlatform);
        cameraTranslateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        viewBranchGroup.addChild(cameraTranslateGroup);
        viewBranchGroup.compile();

        return viewBranchGroup;
    }

    /**
     * Построить освещение
     *
     * @return представление освещения
     */
    public BranchGroup getLights() {

        BranchGroup lights = new BranchGroup();
        Bounds bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);

        LinearFog fog = new LinearFog();
        fog.setColor(new Color3f(0.3f, 0.3f, 0.3f));
        fog.setFrontDistance(0.05f);
        fog.setBackDistance(5f);
        fog.setCapability(Fog.ALLOW_COLOR_WRITE);
        fog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
        fog.setInfluencingBounds(bounds);
        lights.addChild(fog);

        Background background = new Background();
        background.setColor(new Color3f(0.3f, 0.3f, 0.3f));
        background.setApplicationBounds(bounds);
        background.setCapability(Background.ALLOW_COLOR_WRITE);
        lights.addChild(background);

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(new Color3f(0.9f, 0.9f, 0.9f));
        directionalLight.setDirection(4, -7, -12);
        directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.MAX_VALUE));
        lights.addChild(directionalLight);
        DirectionalLight directionalLight2 = new DirectionalLight();
        directionalLight2.setColor(new Color3f(0.6f, 0.6f, 0.6f));
        directionalLight2.setDirection(-6, 9, 12);
        directionalLight2.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.MAX_VALUE));
        lights.addChild(directionalLight2);
        return lights;

    }

    //Обновление сцены (для поддержки двойной буферизации)
    private void update() {
        graphicsContext3D.flush(true);
    }

}

class CoordinateConverter3D implements CoordinateConverter {

    @Override
    public Point toScreen(Point3d point) {
        return null;
    }

    @Override
    public Point3d toPoint3d(Point point) {
        return null;
    }

    @Override
    public int scaleSize(double size) {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}