package org.krisbox.ihub.examples.junit;

import org.junit.Test;
import org.krisbox.ihub.examples.BirtEngineSmartGrid;

import static org.junit.Assert.assertEquals;
import static org.krisbox.ihub.examples.BirtEngineSmartGrid.REPORT_ITEM_TYPE.IMAGE;
import static org.krisbox.ihub.examples.BirtEngineSmartGrid.REPORT_ITEM_TYPE.LABEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kclark on 11/7/16.
 */
public class CreateReportDesignTest {
    private static final String REPORT_NAME = new String("mytestreport.rptdesign");
    private static final int    IMAGE_ROW   = 0;
    private static final int    IMAGE_CELL  = 0;
    private static final String MASTER_PAGE_NAME = "MY MASTER PAGE";
    private static final String IMAGE_NAME  = "TEST_IMAGE";
    private static final String IMAGE_URI   = "http://www.eclipse.org/birt/resources/documentation/tutorial/multichip-4.jpg";
    private static final String LABEL_TEXT  = "TEST LABEL";
    private static final String LABEL_NAME  = "TEST LABEL";
    private static final String GRID_NAME   = "TEST GRID";
    private static final String GRID_WIDTH  = "100%";
    private static final int    GRID_COLS   = 2;
    private static final int    GRID_ROWS   = 2;
    private static final int    LABEL_ROW   = 0;
    private static final int    LABEL_CELL  = 1;
    private static final String DATASOURCE_JDBC = "org.eclipse.birt.report.data.oda.jdbc";
    private static final String ODA_DRIVER_CLASS = "org.eclipse.birt.report.data.oda.sampledb.Driver";
    private static final String ODA_URL = "jdbc:classicmodels:sampledb";
    private static final String ODA_USER = "ClassicModels";
    private static final String ODA_PASSWORD = "";
    private static final String DATASOURCE_NAME = "Data Source";
    private static final String DATASET_NAME = "";
    private static final String DATASET_QUERY = "select * from customers";
    private static final String TABLE_WIDTH = "100%";

    @Test
    public void testCreateMasterPage() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();
        assertEquals(true, birt.addMasterPage(MASTER_PAGE_NAME));
    }

    @Test
    public void testAddGrid() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();
        assertEquals(true, birt.addGrid(GRID_NAME, GRID_COLS, GRID_ROWS, GRID_WIDTH));
    }

    @Test
    public void testAddImageGrid() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();
        assertEquals(true, birt.addGrid(GRID_NAME, GRID_COLS, GRID_ROWS, GRID_WIDTH));
        assertEquals(true, birt.addGridItem(GRID_NAME, IMAGE_NAME, IMAGE, IMAGE_ROW, IMAGE_CELL, birt.createImage(IMAGE_URI)));
    }

    @Test
    public void testAddLabelGrid() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();
        assertEquals(true, birt.addGrid(GRID_NAME, GRID_COLS, GRID_ROWS, GRID_WIDTH));
        assertEquals(true, birt.addGridItem(GRID_NAME, LABEL_NAME, LABEL, LABEL_ROW, LABEL_CELL, birt.createLabel(LABEL_TEXT)));
    }

    @Test
    public void testAddDataSource() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();
        Map<String, String> dsProperties1 = new HashMap<String, String>();

        dsProperties1.put("jdbc", "org.eclipse.birt.report.data.oda.jdbc");
        dsProperties1.put("odaDriverClass", "org.eclipse.birt.report.data.oda.sampledb.Driver");
        dsProperties1.put("odaURL", "jdbc:classicmodels:sampledb");
        dsProperties1.put("odaUser", "ClassicModels");
        dsProperties1.put("odaPassword", "");

        birt.createDataSource("Data Source", dsProperties1);
    }

    @Test
    public void testAddDataSet() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();
        Map<String, String> dsProperties1 = new HashMap<String, String>();
        Map<String, String> dsProperties2 = new HashMap<String, String>();

        dsProperties1.put("jdbc", "org.eclipse.birt.report.data.oda.jdbc");
        dsProperties1.put("odaDriverClass", "org.eclipse.birt.report.data.oda.sampledb.Driver");
        dsProperties1.put("odaURL", "jdbc:classicmodels:sampledb");
        dsProperties1.put("odaUser", "ClassicModels");
        dsProperties1.put("odaPassword", "");

        dsProperties2.put("Data Source Name", "Data Source");
        dsProperties2.put("query", "select * from customers");

        assertEquals(true, birt.createDataSource("Data Source", dsProperties1));
        assertEquals(true, birt.createDataSet("Data Set", dsProperties2));
    }

    @Test
    public void testAddTable() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();

        Map<String, String> dsProperties1 = new HashMap<String, String>();
        Map<String, String> dsProperties2 = new HashMap<String, String>();
        Map<String, Object> tableProperties = new HashMap<String, Object>();
        List<String> tableColumns = new ArrayList<String>();


        dsProperties1.put("jdbc", DATASOURCE_JDBC);
        dsProperties1.put("odaDriverClass", ODA_DRIVER_CLASS);
        dsProperties1.put("odaURL", ODA_URL);
        dsProperties1.put("odaUser", ODA_USER);
        dsProperties1.put("odaPassword", ODA_PASSWORD);

        dsProperties2.put("Data Source Name", DATASOURCE_NAME);
        dsProperties2.put("query", DATASET_QUERY);

        tableColumns.add("CUSTOMERNAME");
        tableColumns.add("COUNTRY");

        tableProperties.put("cols", tableColumns);
        tableProperties.put("width", TABLE_WIDTH);
        tableProperties.put("datasetName", DATASET_NAME);

        Map<String, String> smartGridProperties = new HashMap<String, String>();
        smartGridProperties.put("fontFamily", "Ariel");
        smartGridProperties.put("fontSize", "10px");
        smartGridProperties.put("fontWeight", "normal");
        smartGridProperties.put("fontStyle", "normal");
        smartGridProperties.put("color", "black");
        smartGridProperties.put("textLineThrough", "none");
        smartGridProperties.put("textUnderline", "none");
        smartGridProperties.put("marginTop", "15px");
        smartGridProperties.put("marginBottom", "0px");
        smartGridProperties.put("textAlign", "center");
        smartGridProperties.put("overflow", "auto");
        smartGridProperties.put("refTemplateParameter", "newTemplate");

        tableProperties.put("smartGridProperties", smartGridProperties);

        assertEquals(true, birt.createDataSource("Data Source", dsProperties1));
        assertEquals(true, birt.createDataSet("Data Set", dsProperties2));
        assertEquals(true, birt.createTable("myTable", tableProperties));
    }

    @Test
    public void testLifecycle() {
        BirtEngineSmartGrid birt = new BirtEngineSmartGrid();

        Map<String, String> dsProperties1 = new HashMap<String, String>();
        Map<String, String> dsProperties2 = new HashMap<String, String>();
        Map<String, Object> tableProperties = new HashMap<String, Object>();
        List<String> tableColumns = new ArrayList<String>();


        dsProperties1.put("jdbc", DATASOURCE_JDBC);
        dsProperties1.put("odaDriverClass", ODA_DRIVER_CLASS);
        dsProperties1.put("odaURL", ODA_URL);
        dsProperties1.put("odaUser", ODA_USER);
        dsProperties1.put("odaPassword", ODA_PASSWORD);

        dsProperties2.put("Data Source Name", DATASOURCE_NAME);
        dsProperties2.put("query", DATASET_QUERY);

        tableColumns.add("CUSTOMERNAME");
        tableColumns.add("COUNTRY");

        tableProperties.put("cols", tableColumns);
        tableProperties.put("width", TABLE_WIDTH);
        tableProperties.put("datasetName", DATASET_NAME);

        Map<String, String> smartGridProperties = new HashMap<String, String>();
        smartGridProperties.put("fontFamily", "Ariel");
        smartGridProperties.put("fontSize", "10px");
        smartGridProperties.put("fontWeight", "normal");
        smartGridProperties.put("fontStyle", "normal");
        smartGridProperties.put("color", "black");
        smartGridProperties.put("textLineThrough", "none");
        smartGridProperties.put("textUnderline", "none");
        smartGridProperties.put("marginTop", "15px");
        smartGridProperties.put("marginBottom", "0px");
        smartGridProperties.put("textAlign", "center");
        smartGridProperties.put("overflow", "auto");
        smartGridProperties.put("refTemplateParameter", "newTemplate");

        tableProperties.put("smartGridProperties", smartGridProperties);

        assertEquals(true, birt.addMasterPage(MASTER_PAGE_NAME));
        assertEquals(true, birt.addGrid(GRID_NAME, GRID_COLS, GRID_ROWS, GRID_WIDTH));
        assertEquals(true, birt.addGridItem(GRID_NAME, IMAGE_NAME, IMAGE, IMAGE_ROW, IMAGE_CELL, birt.createImage(IMAGE_URI)));
        assertEquals(true, birt.addGridItem(GRID_NAME, LABEL_NAME, LABEL, LABEL_ROW, LABEL_CELL, birt.createLabel(LABEL_TEXT)));
        assertEquals(true, birt.createDataSource("Data Source", dsProperties1));
        assertEquals(true, birt.createDataSet("Data Set", dsProperties2));
        assertEquals(true, birt.createTable("myTable", tableProperties));
        assertEquals(true, birt.saveDesign(REPORT_NAME));
        assertEquals(true, birt.cleanup());
    }
}
