package sample;

import Connection.ConnectionHandler;
import Warehouse.Product;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.security.core.parameters.P;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddProductFromLotController {

    @FXML
    ChoiceBox p_l_name;
    @FXML
    Label p_l_id;
    @FXML
    DatePicker p_l_save_date;
    @FXML
    TextField p_l_amount;

    ObservableList<String> productNamelist;

    Product product;

    @FXML
    public void initialize(){
        Platform.runLater(new Runnable() {
            public void run() {
                ConnectionHandler connectionHandler = new ConnectionHandler();
                Connection connection = connectionHandler.getConnection();
                productNamelist = FXCollections.observableArrayList();

                String sql = "SELECT P_name FROM product";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    ResultSet rec = preparedStatement.executeQuery();
                    while (rec.next()) {
                        String data = rec.getString(1);
                        productNamelist.add(data);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                p_l_name.setItems(productNamelist);
                p_l_name.getSelectionModel().selectedIndexProperty().addListener(
                        (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                            PreparedStatement preparedStatement = null;
                            try {
                                String QueryText = "SELECT * FROM product WHERE P_name = ?;";
                                preparedStatement = connection.prepareStatement(QueryText);
                                preparedStatement.setString(1, productNamelist.get(new_val.intValue()));
                                ResultSet rec = preparedStatement.executeQuery();
                                if (rec.next()) {
                                    p_l_id.setText(rec.getString(1));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
            }
        });
        //ให้รายชื่อสินค้าสามารถแสดงบน ChoiceBox ได้
    }

    public void btnSubmit(ActionEvent actionEvent) throws IOException{
        //ส่งไปบันทึก db

       product = new Product(p_l_id.getText(), p_l_name.getValue().toString(), Double.parseDouble(p_l_amount.getText()),"date");
        Button btnSubToLot = (Button) actionEvent.getSource();
        Stage stage = (Stage) btnSubToLot.getScene().getWindow();
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addLotPage.fxml"));
        fxmlLoader.load();
        AddLotController addLotController = fxmlLoader.getController();
        addLotController.setProduct(product); // เอาไปปรอ้นในเทเบิ้ลของหน้านี้
      stage.close();
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
