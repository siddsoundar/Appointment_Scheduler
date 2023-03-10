package Controller;

import Main.JDBC;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static Controller.MainController.returnToMain;

public class ReportsController {

    public RadioButton type;
    public String q;
    public ObservableList info;
    public TableView reports;
    public RadioButton byContact;
    public RadioButton byUser;


    public void initialize() {
        selection();
    }

    public void selection() {
        if (type.isSelected()) {
            q = ("SELECT Type, MONTHNAME(Start) as Month, count(*) AS Count FROM appointments GROUP BY Type, MONTHNAME(start) ORDER BY Type;");
        }
        else if (byContact.isSelected()) {
            q = ("SELECT c.Contact_Name, a.Appointment_ID, a.Title, a.Type, a.Description, CONVERT_TZ(start, '+00:00', 'system') AS Start, CONVERT_TZ(end, '+00:00', 'system') AS End, a.Customer_ID FROM appointments a, contacts c WHERE a.Contact_ID = c.Contact_ID order by Contact_Name");
        }
        else if (byUser.isSelected()) {
            q = ("SELECT User_ID,  count(*) AS Count FROM appointments GROUP BY User_ID");
        }
        tables();
    }


    public void tables() {
        info = FXCollections.observableArrayList();
        reports.getColumns().clear();
        try {
            Connection connection = JDBC.getConnection();
            String sql = q;
            ResultSet result = connection.createStatement().executeQuery(sql);

                for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                    int j = i;
                    TableColumn column = new TableColumn(result.getMetaData().getColumnName(i + 1));

                    column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    reports.getColumns().addAll(column);
                }
                while (result.next()) {
                    ObservableList<String> entry = FXCollections.observableArrayList();
                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                        entry.add(result.getString(i));
                    }
                    info.add(entry);
                }
                reports.setItems(info);
        }catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /**
     * Function to return to main menu when Back button is clicked
     * @param event back button clicked
     * @throws IOException
     */
    public void ClickBack(ActionEvent event) throws IOException {
        returnToMain(event);
    }
}
