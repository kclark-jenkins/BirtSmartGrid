An example project showing how to modify an element in a BIRT report to be contained in a smart grid which is needed for 
editability in BIRT Report Studio.  This example was build using open source so I don't get access to commercial BIRT features.

Example usage

```
    private final String REPORT_NAME      = new String("mytestreport.rptdesign");
    private final int    IMAGE_ROW        = 0;
    private final int    IMAGE_CELL       = 0;
    private final String MASTER_PAGE_NAME = "MY MASTER PAGE";
    private final String IMAGE_NAME       = "TEST_IMAGE";
    private final String IMAGE_URI        = "http://www.eclipse.org/birt/resources/documentation/tutorial/multichip-4.jpg";
    private final String LABEL_TEXT       = "TEST LABEL";
    private final String LABEL_NAME       = "TEST LABEL";
    private final String GRID_NAME        = "TEST GRID";
    private final String GRID_WIDTH       = "100%";
    private final int    GRID_COLS        = 2;
    private final int    GRID_ROWS        = 2;
    private final int    LABEL_ROW        = 0;
    private final int    LABEL_CELL       = 1;
    private final String DATASOURCE_JDBC  = "org.eclipse.birt.report.data.oda.jdbc";
    private final String ODA_DRIVER_CLASS = "org.eclipse.birt.report.data.oda.sampledb.Driver";
    private final String ODA_URL          = "jdbc:classicmodels:sampledb";
    private final String ODA_USER         = "ClassicModels";
    private final String ODA_PASSWORD     = "";
    private final String DATASOURCE_NAME  = "Data Source";
    private final String DATASET_NAME     = "";
    private final String DATASET_QUERY    = "select * from customers";
    private final String TABLE_WIDTH      = "100%";

    BirtEngineSmartGrid birt = new BirtEngineSmartGrid();

    Map<String, String> dsourceProperties = new HashMap<String, String>();
    Map<String, String> dsetProperties    = new HashMap<String, String>();
    Map<String, Object> tableProperties   = new HashMap<String, Object>();
    List<String> tableColumns             = new ArrayList<String>();


    dsourceProperties.put("jdbc", DATASOURCE_JDBC);
    dsourceProperties.put("odaDriverClass", ODA_DRIVER_CLASS);
    dsourceProperties.put("odaURL", ODA_URL);
    dsourceProperties.put("odaUser", ODA_USER);
    dsourceProperties.put("odaPassword", ODA_PASSWORD);

    dsetProperties.put("Data Source Name", DATASOURCE_NAME);
    dsetProperties.put("query", DATASET_QUERY);

    tableColumns.add("CUSTOMERNAME");
    tableColumns.add("COUNTRY");

    tableProperties.put("cols", tableColumns);
    tableProperties.put("width", TABLE_WIDTH);
    tableProperties.put("datasetName", DATASET_NAME);

    Map<String, String> smartGridProperties = new HashMap<String, String>();
    smartGridProperties.put("fontFamily",           "Ariel");
    smartGridProperties.put("fontSize",             "10px");
    smartGridProperties.put("fontWeight",           "normal");
    smartGridProperties.put("fontStyle",            "normal");
    smartGridProperties.put("color",                "black");
    smartGridProperties.put("textLineThrough",      "none");
    smartGridProperties.put("textUnderline",        "none");
    smartGridProperties.put("marginTop",            "15px");
    smartGridProperties.put("marginBottom",         "0px");
    smartGridProperties.put("textAlign",            "center");
    smartGridProperties.put("overflow",             "auto");
    smartGridProperties.put("refTemplateParameter", "newTemplate");

    tableProperties.put("smartGridProperties",      smartGridProperties);

    birt.addMasterPage(MASTER_PAGE_NAME);
    birt.addGrid(GRID_NAME,              GRID_COLS,  GRID_ROWS, GRID_WIDTH);
    birt.addGridItem(GRID_NAME,          IMAGE_NAME, IMAGE,     IMAGE_ROW, IMAGE_CELL, birt.createImage(IMAGE_URI));
    birt.addGridItem(GRID_NAME,          LABEL_NAME, LABEL,     LABEL_ROW, LABEL_CELL, birt.createLabel(LABEL_TEXT));
    birt.createDataSource("Data Source", dsourceProperties);
    birt.createDataSet("Data Set",       dsetProperties);
    birt.createTable("myTable",          tableProperties);
    birt.saveDesign(REPORT_NAME);
    birt.cleanup();
```

Example output
```
        <table name="myTable" id="17">
            <list-property name="userProperties">
                <structure>
                    <property name="name">smartLayout</property>
                    <property name="type">string</property>
                </structure>
            </list-property>
            <property name="smartLayout">true</property>
            <property name="fontFamily">"Ariel"</property>
            <property name="fontSize">10px</property>
            <property name="fontWeight">normal</property>
            <property name="fontStyle">normal</property>
            <property name="color">black</property>
            <property name="textLineThrough">none</property>
            <property name="textUnderline">none</property>
            <property name="marginTop">15px</property>
            <property name="marginBottom">0px</property>
            <property name="textAlign">center</property>
            <property name="overflow">auto</property>
            <property name="width">100%</property>
            <column id="31"/>
            <column id="32"/>
            <header>
                <row id="18">
                    <cell id="19">
                        <label name="CUSTOMERNAME" id="20">
                            <text-property name="text">CUSTOMERNAME</text-property>
                        </label>
                    </cell>
                    <cell id="21">
                        <label name="COUNTRY" id="22">
                            <text-property name="text">COUNTRY</text-property>
                        </label>
                    </cell>
                </row>
            </header>
            <detail>
                <row id="23">
                    <cell id="24">
                        <data name="data_CUSTOMERNAME" id="25">
                            <property name="resultSetColumn">CUSTOMERNAME</property>
                        </data>
                    </cell>
                    <cell id="26">
                        <data name="data_COUNTRY" id="27">
                            <property name="resultSetColumn">COUNTRY</property>
                        </data>
                    </cell>
                </row>
            </detail>
            <footer>
                <row id="28">
                    <cell id="29"/>
                    <cell id="30"/>
                </row>
            </footer>
        </table>
```
