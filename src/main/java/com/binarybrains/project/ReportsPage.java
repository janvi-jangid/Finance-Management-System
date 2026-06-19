package com.binarybrains.project;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Desktop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.File;
import java.io.FileOutputStream;

public class ReportsPage extends JFrame {

    private int userId;
    private DefaultPieDataset dataset;

    public ReportsPage(int userId){

        this.userId = userId;

        setTitle("Reports");
        setSize(600,450);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(245,245,245));

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        title.setBounds(240,20,200,40);
        add(title);

        dataset = new DefaultPieDataset();

        JFreeChart chart = ChartFactory.createPieChart("Expenses by Category", dataset, true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} {2}"));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(70,100,450,250);
        add(chartPanel);

        JButton generateBtn = new JButton("Generate");
        generateBtn.setBounds(420,360,120,35);
        add(generateBtn);

        generateBtn.addActionListener(e -> generateExcelReport());

        loadChartData();

        setVisible(true);
    }

    private void loadChartData(){

        try(Connection con = DBConnection.getConnection()){

            dataset.clear();

            String[] categories = {"Food","Rent","Transport","Others"};

            for(String cat : categories){

                String query =
                        "SELECT SUM(amount) FROM transactions WHERE user_id=? AND type='Expense' AND category=?";

                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1,userId);
                ps.setString(2,cat);

                ResultSet rs = ps.executeQuery();

                double value = 0;

                if(rs.next()){
                    value = rs.getDouble(1);
                }

                dataset.setValue(cat,value);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void generateExcelReport(){

        try(Connection con = DBConnection.getConnection()){

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Expenses");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();

            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short)12);

            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Row header = sheet.createRow(0);

            Cell h1 = header.createCell(0);
            h1.setCellValue("Category");
            h1.setCellStyle(headerStyle);

            Cell h2 = header.createCell(1);
            h2.setCellValue("Amount");
            h2.setCellStyle(headerStyle);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            String[] categories = {"Food","Rent","Transport","Others"};

            int rowIndex = 1;

            for(String cat : categories){

                String query =
                        "SELECT SUM(amount) FROM transactions WHERE user_id=? AND type='Expense' AND category=?";

                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1,userId);
                ps.setString(2,cat);

                ResultSet rs = ps.executeQuery();

                double value = 0;

                if(rs.next()){
                    value = rs.getDouble(1);
                }

                Row row = sheet.createRow(rowIndex++);

                Cell c1 = row.createCell(0);
                c1.setCellValue(cat);
                c1.setCellStyle(dataStyle);

                Cell c2 = row.createCell(1);
                c2.setCellValue(value);
                c2.setCellStyle(dataStyle);
            }

            int lastRow = rowIndex - 1;

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            XSSFDrawing drawing = sheet.createDrawingPatriarch();

            // ---------- BAR CHART ----------

            XSSFClientAnchor barAnchor = drawing.createAnchor(0,0,0,0,3,2,12,16);

            XSSFChart barChart = drawing.createChart(barAnchor);
            barChart.setTitleText("Monthly Expense Distribution");

            XDDFCategoryAxis bottomAxis = barChart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis leftAxis = barChart.createValueAxis(AxisPosition.LEFT);

            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            XDDFDataSource<String> categoriesData =
                    XDDFDataSourcesFactory.fromStringCellRange(sheet,new CellRangeAddress(1,lastRow,0,0));

            XDDFNumericalDataSource<Double> valuesData =
                    XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1,lastRow,1,1));

            XDDFBarChartData barData =
                    (XDDFBarChartData) barChart.createData(ChartTypes.BAR,bottomAxis,leftAxis);

            barData.setBarDirection(BarDirection.COL);

            XDDFBarChartData.Series barSeries =
                    (XDDFBarChartData.Series) barData.addSeries(categoriesData,valuesData);

            barSeries.setTitle("Expenses", null);

            barChart.plot(barData);

            // ---------- PIE CHART ----------

            XSSFClientAnchor pieAnchor = drawing.createAnchor(0,0,0,0,3,18,12,34);

            XSSFChart pieChart = drawing.createChart(pieAnchor);

            pieChart.setTitleText("Expense Category Share");

            XDDFChartLegend legend = pieChart.getOrAddLegend();
            legend.setPosition(LegendPosition.RIGHT);

            XDDFDataSource<String> catData =
                    XDDFDataSourcesFactory.fromStringCellRange(sheet,
                            new CellRangeAddress(1,lastRow,0,0));

            XDDFNumericalDataSource<Double> valData =
                    XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                            new CellRangeAddress(1,lastRow,1,1));

            XDDFPieChartData pieData =
                    (XDDFPieChartData) pieChart.createData(ChartTypes.PIE,null,null);

            XDDFPieChartData.Series pieSeries =
                    (XDDFPieChartData.Series) pieData.addSeries(catData,valData);

            pieSeries.setTitle("Expenses", null);

            pieChart.plot(pieData);

            // ---------- SAVE FILE ----------

            File file = new File("Expense_Report_" + System.currentTimeMillis() + ".xlsx");

            workbook.setForceFormulaRecalculation(true);

            try(FileOutputStream fos = new FileOutputStream(file)){
                workbook.write(fos);
            }

            workbook.close();

            JOptionPane.showMessageDialog(this,"Excel Report Generated!");

            Desktop.getDesktop().open(file);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}