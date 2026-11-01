package starmaster.ui;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import starmaster.mapa.MapaGalactico;
import starmaster.mapa.Setor;

public class MiniMapa {

    private static final double CELULA = 40;
    private static final double BORDA = 30;
    private static final Color COR_ROTA = Color.web("#ee0000");
    private static final Color COR_CURSOR = Color.web("#33e6ff");
    private final Group grupo = new Group();
    private final Consumer<Setor> escolhimento;
    private final Function<Setor, String> descircao;
    private final Text info = new Text();

    public MiniMapa (Consumer<Setor> escolhimento, Function<Setor, String> descircao){
        this.escolhimento = escolhimento;
        this.descircao = descircao;
        grupo.setVisible(false);

    }
    public void adicionar() {
        getGameScene().addUINode(grupo);
    }
    public void alternar (){
        grupo.setVisible(!grupo.isVisible());
    }
    public void fechar () {
        grupo.setVisible(false);
    }
    public  boolean taAlberto () {
        return grupo.isVisible();
    }
    public void desenho(MapaGalactico mapa, Setor setorAtual, List<Setor> camihno) {
        grupo.getChildren().clear();

                double larguraGrade = mapa.getColunas() * CELULA;
        double alturaGrade = mapa.getLinhas() * CELULA;
        double larguraPainel = larguraGrade + 2 * BORDA;
        double alturaPainel = alturaGrade + 2 * BORDA + 100;   
        double px = (getAppWidth() - larguraPainel) / 2;         
        double py = (getAppHeight() - alturaPainel) / 2;
        double gx = px + BORDA;                                 
        double gy = py + BORDA + 30;

        Rectangle fundo = new Rectangle (larguraPainel, alturaPainel, Color.rgb(5, 10, 25, 0.80));
        fundo.setArcWidth(16);
        fundo.setArcHeight(16);
        fundo.setStroke(COR_CURSOR.deriveColor(0, 1, 1, 0.5));
        posiciona(fundo, px, py);
        grupo.getChildren().add(fundo);
        Text tirulo  = texto("Mapa", 14, Color.WHITE);
        posiciona (tirulo, gx, py + BORDA + 10);
        grupo.getChildren().add(tirulo);

        for (int l = 0 ; l<mapa.getLinhas(); l++) {
            for (int c= 0; c<mapa.getColunas(); c++){
                grupo.getChildren().add(cricarCelula(mapa.getSetor(l,c), setorAtual, gx, gy));

            }
        }

        for (int i = 1; i<camihno.size(); i++ ){
            Setor s= camihno.get(i);
            Circle ponto = new Circle(4, COR_ROTA);
            ponto.setMouseTransparent(true);
            posiciona(ponto, gx + s.getColuna() * CELULA + CELULA / 2, gy + s.getLinha() * CELULA + CELULA / 2);
            grupo.getChildren().add(ponto);
        }
  
    }
    private Rectangle cricarCelula (Setor setor, Setor setorAtual, double gx, double gy) {
        Rectangle celula = new Rectangle(CELULA - 3, CELULA -3, corDo(setor));
        posiciona(celula, gx + setor.getColuna()*CELULA, gy + setor.getLinha() * CELULA);

        boolean atual = setor == setorAtual;
        if (atual) {
            celula.setStroke(Color.WHITE);
            celula.setStrokeWidth(3);
        }
                celula.setOnMouseEntered(e -> {
            celula.setStroke(COR_CURSOR);
            celula.setStrokeWidth(3);
            info.setText(descircao.apply(setor));
        });
        celula.setOnMouseExited(e -> {
            celula.setStroke(atual ? Color.WHITE : null);
            info.setText("");
        });
       
        celula.setOnMouseClicked(e -> escolhimento.accept(setor));
        return celula;

    }
    private static Color corDo(Setor setor) {
        return Color.web("#0800ff");
    }
    private static Text texto(String conteudo, double tamanho, Color cor){
        Text t = new Text(conteudo);
        t.setFont(Font.font(tamanho));
        t.setFill(cor);
        t.setMouseTransparent(true);
        return t;
    }
    private static void posiciona(javafx.scene.Node no, double x, double y){
        no.setTranslateX(x);
        no.setTranslateY(y);
    }
    
}
