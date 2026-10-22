package starmaster.gerenciadores;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import starmaster.entidades.NaveJogador;
import javafx.scene.PerspectiveCamera;

public class HudTesteDeVoo {

    private static final double LARGURA_BARRA = 220;
    private static final Color AMARELO = Color.web("#e1ff00");
    private static final Color AZUL = Color.web("#33e6ff");

    private static final double PAINEL_LARGURA = 540;
    private static final double PAINEL_ALTURA = 80;
    private static final Color COR_PAINEL = Color.rgb(0, 9, 255, 1.00);
    private static final Color COR_BORDA = Color.web("#eeff33", 0.4);

    private final Rectangle barraEnergia = new Rectangle(LARGURA_BARRA, 12, AMARELO);
    private final Rectangle barraEscudo = new Rectangle(LARGURA_BARRA, 12, AZUL);
    private final Text valorEnergia = texto("1000", Color.WHITE, 16);
    private final Text valorEscudo = texto("100", Color.WHITE, 16);
    private final Text valorVelocidade = texto("0", Color.WHITE, 22);
    private final Text aviso = texto("SEM ENERGIA", Color.web("#ff3b4e"), 28);
    private final Rectangle clarao = new Rectangle(getAppWidth(), getAppHeight(), Color.RED);
    private double tempo;

    public void adicionar() {
        double largura = getAppWidth();
        double altura = getAppHeight();
        double cx = largura / 2;
        double cy = altura / 2;

        PerspectiveCamera cam = getGameScene().getCamera3D().getPerspectiveCamera();
        double meioFov = Math.toRadians(cam.getFieldOfView() / 2);
        double escalaProjecao = (cam.isVerticalFieldOfView() ? cy : cx) / Math.tan(meioFov);

        double offsetX = ModoTesteDeVoo.CANHAO_LADO / ModoTesteDeVoo.CANHAO_FRENTE * escalaProjecao;
        double offsetY = ModoTesteDeVoo.CANHAO_ABAIXO / ModoTesteDeVoo.CANHAO_FRENTE * escalaProjecao;

        double raioBolinha = 10.0;
        Line[] mira = {
                new Line(cx - 13, cy - 13, cx + 13, cy + 13),
                new Line(cx + 13, cy - 13, cx - 13, cy + 13)
        };
        for (Line c : mira) {
            c.setFill(Color.TRANSPARENT);
            c.setStroke(AZUL);
            c.setStrokeWidth(2);
        }
        double px = (largura - PAINEL_LARGURA) / 2;
        double py = altura - PAINEL_ALTURA - 10;
        Rectangle painel = new Rectangle(PAINEL_LARGURA, PAINEL_ALTURA, COR_PAINEL);
        painel.setStroke(COR_BORDA);
        posicionar(painel, px, py);
        Rectangle fundoEnergia = new Rectangle(LARGURA_BARRA, 12, Color.color(1, 1, 1, 0.1));
        Rectangle fundoEscudo = new Rectangle(LARGURA_BARRA, 12, Color.color(1, 1, 1, 0.1));
        Text rotuloEnergia = texto("ENERGIA", Color.LIGHTGRAY, 16);
        Text rotuloEscudo = texto("ESCUDO", Color.LIGHTGRAY, 16);
        Text rotuloVelocidade = texto("VELOCIDADE", Color.LIGHTGRAY, 16);
        // SETAS, ESPAÇO atira, H simula dano R recarrega ESC menu

        posicionar(rotuloEnergia, px + 20, py + 30);
        posicionar(fundoEnergia, px + 110, py + 18);
        posicionar(barraEnergia, px + 110, py + 18);
        posicionar(valorEnergia, px + 340, py + 30);
        posicionar(rotuloEscudo, px + 20, py + 60);
        posicionar(fundoEscudo, px + 110, py + 48);
        posicionar(barraEscudo, px + 110, py + 48);
        posicionar(valorEscudo, px + 340, py + 60);
        posicionar(rotuloVelocidade, px + 420, py + 30);
        posicionar(valorVelocidade, px + 420, py + 62);

        posicionar(aviso, cx - 90, cy - 60);
        aviso.setVisible(false);
        clarao.setOpacity(0);
        clarao.setMouseTransparent(true);

        getGameScene().addUINodes(mira);
        getGameScene().addUINodes(painel, fundoEnergia, barraEnergia, valorEnergia, rotuloEnergia,
                fundoEscudo, barraEscudo, valorEscudo, rotuloEscudo, rotuloVelocidade, valorVelocidade,
                aviso, clarao);
    }

    public void atualizar(NaveJogador nave, double tpf) {
        tempo += tpf;
        double energia = nave.getEnergia() / (double) nave.getEnergiaMaxima();
        barraEnergia.setWidth(LARGURA_BARRA * energia);
        barraEnergia.setFill(energia > 0.5 ? AMARELO : (energia > 0.25 ? Color.web("#ffd23f") : Color.web("#ff3b4e")));
        barraEscudo.setWidth(LARGURA_BARRA * nave.getEscudo() / nave.getEscudoMaximo());
        valorEnergia.setText(String.valueOf(nave.getEnergia()));
        valorEscudo.setText(nave.getEscudo() + "%");
        valorVelocidade.setText(String.format("%.0f", nave.getVelocidade()));
        aviso.setVisible(nave.semEnergia() && ((int) (tempo * 3)) % 2 == 0);
        clarao.setOpacity(Math.max(0, clarao.getOpacity() - tpf * 1.5));
    }

    public void piscarDano() {
        clarao.setOpacity(0.45);
    }

    private static Text texto(String conteudo, Color cor, double tamanho) {
        return getUIFactoryService().newText(conteudo, cor, tamanho);
    }

    private static void posicionar(javafx.scene.Node no, double x, double y) {
        no.setTranslateX(x);
        no.setTranslateY(y);
    }
}
