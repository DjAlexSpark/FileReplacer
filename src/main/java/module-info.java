module io.github.djalexspark.filereplacer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens io.github.djalexspark.filereplacer to javafx.fxml;
    exports io.github.djalexspark.filereplacer;
}