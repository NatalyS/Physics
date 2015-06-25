package ru.spb.herzen.physics;

import javax.vecmath.Point3d;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by user0 on 24.06.2015.
 */
public class Helper3d {
    public static HashMap<String, Object> getStatePoint3d(Point3d point) {
        HashMap<String, Object> state = new HashMap<>();
        state.put("x", String.valueOf(point.getX()));
        state.put("y", String.valueOf(point.getY()));
        state.put("z", String.valueOf(point.getZ()));

        return state;
    }

    public static Point3d createPoint3dFromState(HashMap<String, Object> state) {
        double x = Double.parseDouble(String.valueOf(state.get("x")));
        double y = Double.parseDouble(String.valueOf(state.get("y")));
        double z = Double.parseDouble(String.valueOf(state.get("z")));
        return new Point3d(x, y, z);
    }

    public static HashMap<String, Object> getStateColor(Color color) {
        HashMap<String, Object> state = new HashMap<>();
        state.put("R", String.valueOf(color.getRed()));
        state.put("G", String.valueOf(color.getGreen()));
        state.put("B", String.valueOf(color.getBlue()));

        return state;
    }

    public static Color createColorFromState(HashMap<String, Object> state) {
        int R = Integer.parseInt(String.valueOf(state.get("R")));
        int G = Integer.parseInt(String.valueOf(state.get("G")));
        int B = Integer.parseInt(String.valueOf(state.get("B")));
        return new Color(R, G, B);
    }

}
