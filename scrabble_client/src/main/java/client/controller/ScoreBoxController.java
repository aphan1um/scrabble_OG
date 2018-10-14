package client.controller;

import core.game.Agent;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

public class ScoreBoxController implements Initializable {
    @FXML
    private TableView tblScores;

    public ObservableMap<Agent, Integer> scores;

    public ScoreBoxController(Map<Agent, Integer> scores) {
        super();
        this.scores = FXCollections.observableMap(scores);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Thanks to https://stackoverflow.com/a/18620705 for the help
        // TODO: This table does not count removal/additions of values
        // load and bind the scores to the table
        TableColumn<Agent, String> colNames =
                new TableColumn<>("Player");
        TableColumn<Agent, Integer> colPoints =
                new TableColumn<>("Points");

        colNames.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getName());
        });

        colPoints.setCellValueFactory((param) -> {
            return new SimpleIntegerProperty(scores.get(param.getValue())).asObject();
        });

        colNames.prefWidthProperty().bind(tblScores.widthProperty().multiply(0.6));
        colPoints.prefWidthProperty().bind(tblScores.widthProperty().multiply(0.4));

        // TODO: Thanks to https://stackoverflow.com/a/37172900 for the help
        ObservableList<Agent> lst = FXCollections.observableArrayList(scores.keySet());
        scores.addListener(new MapChangeListener<Agent, Integer>() {
            @Override
            public void onChanged(Change<? extends Agent, ? extends Integer> change) {
                // this is to account adding a key/value pair in a HashMap can trigger
                // both a deletion and insertion (in that case, a replacement)
                if (change.wasRemoved() != change.wasAdded()) {
                    if (change.wasRemoved())
                        lst.remove(change.getKey());
                    else
                        lst.add(change.getKey());
                } else {
                    // update scores
                    tblScores.refresh();
                }
            }
        });

        tblScores.setItems(lst);
        tblScores.getColumns().setAll(colNames, colPoints);

        System.out.println(lst.size());
    }

    public void updateScore(Agent player, int newScore) {
        scores.put(player, newScore);
    }

    public String getWinner() {
        List<Map.Entry<Agent, Integer>> list = new ArrayList(scores.entrySet());
        Collections.sort(list, (o1, o2) -> (o1.getValue() - o2.getValue()));
        String result = "";
        for(int i = 1; i <= list.size(); i ++) {
            if(i == 1) {
                result += list.get(list.size() - i).getKey().getName();
            }
            else {
                if (list.get(list.size() - i).getValue() == list.get(list.size() - i + 1).getValue()) {
                    result += " & " + list.get(list.size() - i).getKey().getName();
                }
                else {
                    break;
                }
            }
        }

        return result;
    }
}
