package View.DefaultUI;

import Model.*;
import Model.Entity.Employment;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A tab for the current staff members
 * Created by Abeer  5/13/2016
 * modified by Simon on 2016/04/28 to make the table get real employment from online DB
 */
@SpringComponent
@UIScope
public class EmploymentsView extends VerticalLayout {
    final private int WIDTH = 11;
    private DeletionLogModel logModel ;
    private TextField searchField ;
    private BeanItemContainer<Employment> container;
    private Collection<Employment>  member = new ArrayList<>();
    private GeneratedPropertyContainer gpc;
    private Grid membersGrid;
    private Button deleteSelected;
    private Label currentDB;
    /**
     *A constructor for the table tree full of the staff members (could be current or deleted members)
     */
    @Autowired
    public EmploymentsView() {

        this.setMargin(true);
        this.setSpacing(true);
        this.setSizeFull();
        try {
            logModel = new DeletionLogModel();
        } catch (IOException e) {
            Notification.show(e.getMessage());

        }

        currentDB = new Label("Current Databse:");
        Connection connect = SQLServerConnection.getInstance();
        EmploymentDAO daoEmployment = new EmploymentDAO(SQLServerConnection.getInstance());
        List<Employment> listEmployments = daoEmployment.getEmployments();
        member = listEmployments;


        container =new BeanItemContainer<Employment>(Employment.class, member);
        gpc = new GeneratedPropertyContainer(container);

        gpc.addGeneratedProperty("Show Information",
                new PropertyValueGenerator<String>() {


                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }

                    @Override
                    public String getValue(Item item, Object itemId,
                                           Object propertyId) {
                        return "Show Info";
                    }
                });

        initGird();
        initFilters();
        this.addComponents( currentDB, membersGrid, deleteSelected);
    }

    private void initGird() {
        membersGrid = new Grid(gpc);
        // Column should fetch the Employment class attribute names
        membersGrid.setColumnOrder("companyID", "personID", "employmentID", "rowID", "firstName", "lastName");
        membersGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        membersGrid.setHeight(300, Unit.PIXELS);
        membersGrid.setWidth(28, Unit.CM);
        //   membersGrid.setSizeFull();
        membersGrid.setImmediate(true);

        membersGrid.getColumn("Show Information")
                .setRenderer(new ButtonRenderer(e ->{ // Java 8
                    Employment emp = (Employment)e.getItemId();
                    UI.getCurrent().addWindow(new EmploymentInfo(emp));
                }
                ));

        //select multiple items
        membersGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        deleteSelected = new Button("Delete Selected", e -> {
            if(membersGrid.getSelectedRows().size() > 0){

                UI.getCurrent().addWindow(  new DeletionConfirmationWindow(logModel,membersGrid)  );
            }
            else

                Notification.show("Nothing selected");
        });

    }

    private void initFilters(){

        // Create a header row to hold column filters
        Grid.HeaderRow filterRow = membersGrid.appendHeaderRow();

        // Set up a filter for all columns
        for (Object pid: membersGrid.getContainerDataSource()
                .getContainerPropertyIds()) {
            Grid.HeaderCell cell = filterRow.getCell(pid);

            // Have an input field to use for filter
            TextField filterField = new TextField();
            filterField.setColumns(8);
            filterField.setInputPrompt("Filter");
            filterField.addStyleName(ValoTheme.TEXTFIELD_TINY);

            // Update filter When the filter input is changed
            filterField.addTextChangeListener(change -> {
                // Can't modify filters so need to replace
                container.removeContainerFilters(pid);

                // (Re)create the filter if necessary
                if (! change.getText().isEmpty())
                    container.addContainerFilter(
                            new SimpleStringFilter(pid,
                                    change.getText(), true, false));
            });
            cell.setComponent(filterField);
            membersGrid.setWidth("100%");
        }
    }
}
