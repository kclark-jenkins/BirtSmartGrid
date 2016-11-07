package org.krisbox.ihub.examples;

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
    private SessionHandle session;
    private ReportDesignHandle design;
    private ElementFactory factory;
    private Map<String, Object> elements;

    public BirtEngineSmartGrid() {
        elements = new HashMap<String,Object>();
        session  = DesignEngine.newSession( null );
        design   = session.createDesign( );
        factory  = design.getElementFactory( );
        design.getElementFactory( );
    }

    public boolean saveDesign(String reportDesignName) {
        try{
            design.saveAs(reportDesignName);
            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean cleanup() {
        try {
            design.close();
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void addElementTracking(String name, Object element) {
        elements.put(name, element);
    }

    public boolean addMasterPage(String masterPageName) {
        try {
            DesignElementHandle element = factory.newSimpleMasterPage(masterPageName);
            design.getMasterPages( ).add( element );
            addElementTracking(masterPageName, element);
            return true;
        }catch(NameException ex){
            ex.printStackTrace();
            return false;
        }catch(ContentException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean addGrid(String gridName, int columns, int rows, String width) {
        try{
            GridHandle grid = factory.newGridItem( null, columns, rows );
            design.getBody( ).add( grid );
            grid.setWidth( width );
            addElementTracking(gridName, grid);
            return true;
        }catch(NameException ex){
            ex.printStackTrace();
            return false;
        }catch(ContentException ex){
            ex.printStackTrace();
            return false;
        }catch(SemanticException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public ImageHandle createImage(String uri) {
        try {
            ImageHandle image = factory.newImage( null );
            image.setURI( "http://www.eclipse.org/birt/resources/documentation/tutorial/multichip-4.jpg");
            return image;
        }catch(SemanticException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public LabelHandle createLabel(String labelText) {
        LabelHandle label = null;
        try {
            label = factory.newLabel( null );
            label.setText(labelText);
        }catch(SemanticException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public boolean createDataSource(String dsName, Map<String, String> dsProperties) {

        try {
            OdaDataSourceHandle dsHandle = factory.newOdaDataSource( dsName, dsProperties.get("jdbc") );
            dsHandle.setProperty( "odaDriverClass", dsProperties.get("odaDriverClass"));
            dsHandle.setProperty( "odaURL", dsProperties.get("odaURL") );
            dsHandle.setProperty( "odaUser", dsProperties.get("odaUser") );
            dsHandle.setProperty( "odaPassword", dsProperties.get("odaPassword") );
            design.getDataSources( ).add( dsHandle );

            addElementTracking(dsName, dsHandle);

            return true;
        }catch(SemanticException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean createDataSet(String dsName, Map<String, String> dsProperties) {
        try {
            //"org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet"
            OdaDataSetHandle dsHandle = factory.newOdaDataSet( dsName, dsProperties.get("jdbc"));
            dsHandle.setDataSource( dsProperties.get("Data Source Name") );
            dsHandle.setQueryText( dsProperties.get("query") );
            design.getDataSets( ).add( dsHandle );

            addElementTracking(dsName, dsHandle);

            return true;
        }catch(SemanticException ex){
            ex.printStackTrace();
            return false;
        }
    }


    public boolean createTable(String tableName, Map<String, Object> tableProperties) {
        try {
            List<String> cols = (ArrayList<String>) tableProperties.get("cols");

            TableHandle table = factory.newTableItem( tableName, cols.size() );
            table.setWidth( (String) tableProperties.get("width") );
            table.setDataSet( design.findDataSet( (String) tableProperties.get("datasetName") ) );

            PropertyHandle computedSet = table.getColumnBindings( );

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
                DataItemHandle data = factory.newDataItem( "data_"+(String)cols.get(i) );
                data.setResultSetColumn( (String)cols.get(i));
                cell.getContent( ).add( data );
            }

            UserPropertyDefn userProp = new UserPropertyDefn();

            userProp.setName("smartLayout");
            userProp.setReturnType(PropertyType.BOOLEAN_TYPE_NAME);

            table.addUserPropertyDefn(userProp);

            table.setProperty("smartLayout", true);
            //table.setProperty("linkedDataModel", "oda-data-source::New Data Model");

            for (Map.Entry<String, String> entry : ((Map<String, String>) tableProperties.get("smartGridProperties")).entrySet()) {
                if(entry.getKey() != null) {
                    table.setProperty(entry.getKey(), entry.getValue());
                }
            }

            design.getBody( ).add( table );

            addElementTracking(tableName, table);

            return true;
        }catch(SemanticException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean addGridItem(String gridName, String itemName, REPORT_ITEM_TYPE itemType, int rowNum, int cellNum, Object item) {
        try {
            GridHandle grid = (GridHandle)getSingleElementTracker(gridName);
            RowHandle row = (RowHandle) grid.getRows( ).get( rowNum );
            CellHandle cell = (CellHandle) row.getCells( ).get( cellNum );

            switch(itemType) {
                case IMAGE:
                    cell.getContent( ).add( (ImageHandle) item );
                    addElementTracking(itemName, (ImageHandle) item);
                    break;
                case LABEL:
                    cell.getContent( ).add( (LabelHandle) item );
                    addElementTracking(itemName, (LabelHandle) item);
                    break;

            }

            addElementTracking(gridName, grid);
            return true;
        }catch(NameException ex){
            ex.printStackTrace();
            return false;
        }catch(ContentException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public Object getSingleElementTracker(String name) {
        return elements.get(name);
    }

    public Map<String,Object> getElementsTracker() {
        return elements;
    }

    public SessionHandle getSessionHandle() {
        return session;
    }

    public ReportDesignHandle getReportDesignHandle() {
        return design;
    }

    public ElementFactory getElementFactory() {
        return factory;
    }

    public enum REPORT_ITEM_TYPE {
        IMAGE, LABEL
    }
}
