package starmaster;
import java.fx.naosei;
//application seria a janela do jogo
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
