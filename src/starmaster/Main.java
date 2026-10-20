package starmaster;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage palco) {
    Label titulo = new Label("StarMaster");
    titulo.setTextFill(Color.WHITE);
    titulo.setFont(Font.font(30)); 

    StackPane raiz = new StackPane(titulo);
    raiz.setStyle("-fx-background-color: black;");
    palco.setTitle("StarMaster");
    palco.setScene(new Scene(raiz, PIXEIS_X, PIXEIS_Y)); //pixeis x e y sao a resolução vamos discutir isso depois.
    palco.setResizable(false); // auto-explicativo
    palco.show();
    // gerenciador jogo jogo = new gerenciadorjogo();
    //jogo.inicarJogo();
  }
  public static void main(String[] args) {
    launch(args);
  }
}
