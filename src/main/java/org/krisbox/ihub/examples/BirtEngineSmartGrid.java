package org.krisbox.ihub.examples;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.ContentException;
import org.eclipse.birt.report.model.api.command.NameException;
import org.eclipse.birt.report.model.api.core.UserPropertyDefn;
import org.eclipse.birt.report.model.metadata.PropertyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BirtEngineSmartGrid {
    private final static Logger LOGGER = Logger.getLogger(BirtEngineSmartGrid.class);

    private SessionHandle       session;
    private ReportDesignHandle  design;
    private ElementFactory      factory;
    private Map<String, Object> elements;

    public BirtEngineSmartGrid() {
        printMessage(1, "BirtEngineSmartGrid()");
        elements = new HashMap<String,Object>();
        session  = DesignEngine.newSession( null );
        design   = session.createDesign( );
        factory  = design.getElementFactory( );

        design.getElementFactory( );
    }

    public boolean saveDesign(String reportDesignName) {
        printMessage(1, "saveDesign("+reportDesignName+")");
        try{
            design.saveAs(reportDesignName);
            return true;
        }catch(IOException ex){
            printMessage(4, ex);
            return false;
        }
    }

    public boolean cleanup() {
        printMessage(1, "cleanup()");
        try {
            design.close();
            return true;
        }catch(Exception ex){
            printMessage(4, ex);
            return false;
        }
    }

    private void addElementTracking(String name, Object element) {
        printMessage(1, "addElementTracking("+name+")");
        elements.put(name, element);
    }

    public boolean addMasterPage(String masterPageName) {
        printMessage(1, "addMasterPage(" + masterPageName + ")");
        try {
            design.getMasterPages( ).add(      factory.newSimpleMasterPage(masterPageName) );
            addElementTracking(masterPageName, factory.newSimpleMasterPage(masterPageName) );

            return true;
        }catch(NameException ex){
            printMessage(4, ex);
            return false;
        }catch(ContentException ex){
            printMessage(4, ex);
            return false;
        }
    }

    public boolean addGrid(String gridName, int columns, int rows, String width) {
        printMessage(1, "addGrid("+gridName+","+columns+","+rows+","+width+")");
        try{
            GridHandle grid = factory.newGridItem( null, columns, rows );
                       grid.setWidth( width );

            design.getBody( ).add( factory.newGridItem( null, columns, rows ) );

            addElementTracking(gridName, grid);

            return true;
        }catch(NameException ex){
            printMessage(4, ex);
            return false;
        }catch(ContentException ex){
            printMessage(4, ex);
            return false;
        }catch(SemanticException ex){
            printMessage(4, ex);
            return false;
        }
    }

    public ImageHandle createImage(String url) {
        printMessage(1, "createImage("+url+")");
        try {
            ImageHandle image = factory.newImage( null );
            image.setURL( url );
            return image;
        }catch(SemanticException ex){
            printMessage(4, ex);
        }

        return null;
    }

    public LabelHandle createLabel(String labelText) {
        printMessage(1, "createLabel("+labelText+")");
        LabelHandle label;
        try {
            label = factory.newLabel( null );
            label.setText(labelText);
        }catch(SemanticException ex){
            printMessage(4, ex);
        }

        return null;
    }

    public boolean createDataSource(String dsName, Map<String, String> dsProperties) {
        printMessage(1, "createDataSource("+dsName+","+dsProperties.getClass()+")");

        try {
            OdaDataSourceHandle dsHandle = factory.newOdaDataSource( dsName, dsProperties.get("jdbc") );

            dsHandle.setProperty( "odaDriverClass", dsProperties.get("odaDriverClass"));
            dsHandle.setProperty( "odaURL",         dsProperties.get("odaURL") );
            dsHandle.setProperty( "odaUser",        dsProperties.get("odaUser") );
            dsHandle.setProperty( "odaPassword",    dsProperties.get("odaPassword") );

            design.getDataSources( ).add( dsHandle );

            addElementTracking(dsName, dsHandle);

            return true;
        }catch(SemanticException ex){
            printMessage(4, ex);
            return false;
        }
    }

    public boolean createDataSet(String dsName, Map<String, String> dsProperties) {
        printMessage(1, "createDataSet("+dsName+","+dsProperties.getClass()+")");
        try {
            OdaDataSetHandle dsHandle = factory.newOdaDataSet( dsName, dsProperties.get("jdbc"));

            dsHandle.setDataSource(    dsProperties.get("Data Source Name") );
            dsHandle.setQueryText(     dsProperties.get("query") );

            design.getDataSets( ).add( dsHandle );

            addElementTracking(dsName, dsHandle);

            return true;
        }catch(SemanticException ex){
            LOGGER.fatal(ex);
            return false;
        }
    }


    public boolean createTable(String tableName, Map<String, Object> tableProperties) {
        printMessage(1, "createTable("+tableName+","+tableProperties.getClass());
        try {
            List<?> cols = (ArrayList<?>) tableProperties.get("cols");

            TableHandle table = factory.newTableItem( tableName, cols.size() );
            table.setWidth( (String) tableProperties.get("width") );
            table.setDataSet( design.findDataSet( (String) tableProperties.get("datasetName") ) );

            RowHandle tableheader = (RowHandle) table.getHeader( ).get( 0 );

            for( int i=0; i < cols.size(); i++ ) {
                LabelHandle label1 = factory.newLabel( (String)cols.get(i) );
                label1.setText((String)cols.get(i));
                CellHandle cell = (CellHandle) tableheader.getCells( ).get( i );
                cell.getContent( ).add( label1 );
            }

            RowHandle tabledetail = (RowHandle) table.getDetail( ).get( 0 );

            for( int i=0; i < cols.size(); i++ ) {
                CellHandle cell = (CellHandle) tabledetail.getCells( ).get( i );
                DataItemHandle data = factory.newDataItem( "data_"+cols.get(i) );
                data.setResultSetColumn( (String)cols.get(i));
                cell.getContent( ).add( data );
            }

            UserPropertyDefn userProp = new UserPropertyDefn();

            userProp.setName("smartLayout");
            userProp.setReturnType(PropertyType.BOOLEAN_TYPE_NAME);

            table.addUserPropertyDefn(userProp);

            table.setProperty("smartLayout", true);

            for (Map.Entry<?, ?> entry : ((Map<?, ?>) tableProperties.get("smartGridProperties")).entrySet()) {
                if(entry.getKey() != null) {
                    table.setProperty((String)entry.getKey(), entry.getValue());
                }
            }

            design.getBody( ).add( table );

            addElementTracking(tableName, table);

            return true;
        }catch(SemanticException ex){
            printMessage(4, ex);
            return false;
        }
    }

    public boolean addGridItem(String gridName, String itemName, REPORT_ITEM_TYPE itemType, int rowNum, int cellNum, Object item) {
        printMessage(1, "addGridItem("+gridName+","+itemName+","+itemType+","+rowNum+","+")");
        try {
            GridHandle grid = (GridHandle)getSingleElementTracker(gridName);
            RowHandle row = (RowHandle) grid.getRows( ).get( rowNum );
            CellHandle cell = (CellHandle) row.getCells( ).get( cellNum );

            switch(itemType) {
                case IMAGE:
                    cell.getContent( ).add( (ImageHandle) item );
                    addElementTracking(itemName, item);
                    break;
                case LABEL:
                    cell.getContent( ).add( (LabelHandle) item );
                    addElementTracking(itemName, item);
                    break;

            }

            addElementTracking(gridName, grid);
            return true;
        }catch(NameException ex){
            printMessage(4, ex);
            return false;
        }catch(ContentException ex){
            printMessage(4, ex);
            return false;
        }
    }

    private Object getSingleElementTracker(String name) { printMessage(1, "getSingleElement("+name+")"); return elements.get(name); }
    public  Map<String,Object> getElementsTracker()     { printMessage(1, "getElementTracker()");        return elements; }
    public  SessionHandle getSessionHandle()            { printMessage(1, "getSessionHandle()");         return session;  }
    public  ReportDesignHandle getReportDesignHandle()  { printMessage(1, "getReportDesignHandle()");    return design;   }
    public  ElementFactory getElementFactory()          { printMessage(1, "getElementFactory()");        return factory;  }
    public  enum REPORT_ITEM_TYPE                       {   IMAGE, LABEL   }

    private void printMessage(int level, Object message) {
        switch(level) {
            case 0:
                LOGGER.trace(message);
                break;
            case 1:
                LOGGER.debug(message);
                break;
            case 2:
                LOGGER.info(message);
                break;
            case 3:
                LOGGER.error(message);
                break;
            case 4:
                LOGGER.fatal(message);
                break;
        }
    }
}
