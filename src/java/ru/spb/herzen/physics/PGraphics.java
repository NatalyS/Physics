package ru.spb.herzen.physics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

/**
    Класс получения графика по заданным данным
 */

public class PGraphics {
    private Font FONT_TITLE = new Font("Calibri", Font.BOLD, 15); // Шрифт заголовка

    /**
     * Получение графика
     *
     * @param series Серия данных
     * @param title Заголовок графика
     * @param nameOX Подпись оси OX
     * @param nameOY Подпись оси OY
     * @return График
     */
    public JFreeChart getChart(XYSeries series, String title, String nameOX, String nameOY) {
        XYDataset xyDataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory
                .createXYLineChart(title, nameOX, nameOY,
                        xyDataset,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        TextTitle textTitle = new TextTitle(title, FONT_TITLE);
        chart.setTitle(textTitle);
        chart.getXYPlot().setDomainGridlinePaint(Color.gray);
        chart.getXYPlot().setRangeGridlinePaint(Color.gray);
        chart.getXYPlot().setBackgroundPaint(Color.white);
        chart.getXYPlot().setAxisOffset(new RectangleInsets(0D, 0D, 0D, 0D));
        chart.getXYPlot().setOutlinePaint(null);
        chart.getXYPlot().getDomainAxis().setPositiveArrowVisible(true);
        chart.getXYPlot().getRangeAxis().setPositiveArrowVisible(true);
        return chart;
    }
}
