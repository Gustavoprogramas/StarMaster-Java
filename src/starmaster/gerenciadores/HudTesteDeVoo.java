package starmaster.gerenciadores;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import starmaster.entidades.NaveJogador;
import starmaster.mapa.Setor;

public class HudTesteDeVoo {

    private static final double LARGURA_BARRA = 220;
    private static final Color AMARELO = Color.web("#e1ff00");
    private static final Color AZUL = Color.web("#33e6ff");
    private static final Color VERMELHO = Color.web("#ff0019");
    private static final Color VERDE = Color.web("#00ff26");
    private static final Color VERDE_RADAR = Color.web("#33ff66");

    private static final double PAINEL_LARGURA = 540;
    private static final double PAINEL_ALTURA = 80;
    private static final Color COR_PAINEL = Color.rgb(0, 9, 255, 1.00);
    private static final Color COR_BORDA = Color.web("#eeff33", 0.4);

    private static final double RAIO_RADAR = 55;
    private static final double ALCANCE_RADAR = 800;

    private final Rectangle barraEnergia = new Rectangle(LARGURA_BARRA, 12, AMARELO);
    private final Rectangle barraEscudo = new Rectangle(LARGURA_BARRA, 12, AZUL);
    private final Text valorEnergia = texto("1000", Color.WHITE, 16);
    private final Text valorEscudo = texto("100", Color.WHITE, 16);
    private final Text valorVelocidade = texto("0", Color.WHITE, 22);
    private final Text aviso = texto("SEM ENERGIA", VERMELHO, 28);
    private final Text status = texto("", Color.LIGHTGRAY, 14);
    private final Text atracagem = texto("", VERDE_RADAR, 20);
    private final Text mensagem = texto("", VERDE_RADAR, 26);
    private final Line varredura = new Line();
    private final Group radar = new Group();
    private final Group pontosRadar = new Group();
    private final Rectangle clarao = new Rectangle(getAppWidth(), getAppHeight(), Color.RED);
    private double tempo;
    private double radarX;
    private double radarY;
    private double anguloVarredura;
    private double tempoMensagem;

    public void adicionar() {
        double largura = getAppWidth();
        double altura = getAppHeight();
        double cx = largura / 2;
        double cy = altura / 2;

        Line[] mira = {
            new Line(cx - 13, cy - 13, cx + 13, cy + 13),
            new Line(cx + 13, cy - 13, cx - 13, cy + 13)
        };
        for (Line l : mira) {
            l.setStroke(AZUL);
            l.setStrokeWidth(2);
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

        posicionar(status, 16, 24);
        posicionar(aviso, cx - 90, cy - 60);
        aviso.setVisible(false);
        posicionar(atracagem, cx - 110, cy + 70);
        atracagem.setVisible(false);
        posicionar(mensagem, cx - 100, cy - 100);
        mensagem.setVisible(false);

        radarX = largura - RAIO_RADAR - 16;
        radarY = RAIO_RADAR + 16;
        Circle fundoRadar = new Circle(radarX, radarY, RAIO_RADAR, Color.rgb(0, 25, 5, 0.8));
        fundoRadar.setStroke(VERDE_RADAR.deriveColor(0, 1, 1, 0.8));
        fundoRadar.setStrokeWidth(2);
        Circle anelRadar = new Circle(radarX, radarY, RAIO_RADAR / 2, Color.TRANSPARENT);
        anelRadar.setStroke(VERDE_RADAR.deriveColor(0, 1, 1, 0.3));
        Line eixoHorizontal = new Line(radarX - RAIO_RADAR, radarY, radarX + RAIO_RADAR, radarY);
        Line eixoVertical = new Line(radarX, radarY - RAIO_RADAR, radarX, radarY + RAIO_RADAR);
        eixoHorizontal.setStroke(VERDE_RADAR.deriveColor(0, 1, 1, 0.2));
        eixoVertical.setStroke(VERDE_RADAR.deriveColor(0, 1, 1, 0.2));
        varredura.setStartX(radarX);
        varredura.setStartY(radarY);
        varredura.setStroke(VERDE_RADAR.deriveColor(0, 1, 1, 0.6));
        varredura.setStrokeWidth(2);
        Circle naveRadar = new Circle(radarX, radarY, 3, VERDE_RADAR);
        radar.getChildren().addAll(fundoRadar, anelRadar, eixoHorizontal, eixoVertical, varredura, pontosRadar, naveRadar);

        clarao.setOpacity(0);
        clarao.setMouseTransparent(true);

        getGameScene().addUINodes(mira);
        getGameScene().addUINodes(painel, fundoEnergia, barraEnergia, valorEnergia, rotuloEnergia,
                fundoEscudo, barraEscudo, valorEscudo, rotuloEscudo, rotuloVelocidade, valorVelocidade,
                status, aviso, atracagem, mensagem, radar, clarao);
    }

    public void atualizar(NaveJogador nave, double tpf) {
        tempo += tpf;
        double energia = nave.getEnergia() / (double) nave.getEnergiaMaxima();
        barraEnergia.setWidth(LARGURA_BARRA * energia);
        barraEnergia.setFill(energia > 0.5 ? AMARELO : (energia > 0.25 ? Color.web("#ffd23f") : VERMELHO));
        barraEscudo.setWidth(LARGURA_BARRA * nave.getEscudo() / nave.getEscudoMaximo());
        valorEnergia.setText(String.valueOf(nave.getEnergia()));
        valorEscudo.setText(nave.getEscudo() + "%");
        valorVelocidade.setText(String.format("%.0f / %.0f", nave.getVelocidade(), nave.getVelocidadeAlvo()));
        aviso.setVisible(nave.semEnergia() && ((int) (tempo * 3)) % 2 == 0);
        clarao.setOpacity(Math.max(0, clarao.getOpacity() - tpf * 1.5));

        anguloVarredura = (anguloVarredura + 120 * tpf) % 360;
        double radianos = Math.toRadians(anguloVarredura);
        varredura.setEndX(radarX + Math.cos(radianos) * RAIO_RADAR);
        varredura.setEndY(radarY + Math.sin(radianos) * RAIO_RADAR);

        if (tempoMensagem > 0) {
            tempoMensagem -= tpf;
            mensagem.setVisible(tempoMensagem > 0);
        }
    }

    public void mostrarMensagem(String texto, double duracao) {
        mensagem.setText(texto);
        mensagem.setVisible(true);
        tempoMensagem = duracao;
    }

    public void atualizarStatus(Setor setor, int inimigosNoSetor, int abates, int setoresInimigos, double tempoDeJogo) {
        status.setText(String.format("SETOR (%d,%d)   INIMIGOS AQUI: %d   ABATES: %d   SETORES INIMIGOS: %d   TEMPO: %s",
                setor.getLinha(), setor.getColuna(), inimigosNoSetor, abates, setoresInimigos, formatarTempo(tempoDeJogo)));
    }

    public void atualizarRadar(List<Point3D> inimigos, List<Point3D> bases, Point3D posicaoNave, Point3D frente) {
        pontosRadar.getChildren().clear();
        Point3D frenteHorizontal = new Point3D(frente.getX(), 0, frente.getZ());
        if (frenteHorizontal.magnitude() < 1e-6) {
            frenteHorizontal = new Point3D(0, 0, 1);
        }
        frenteHorizontal = frenteHorizontal.normalize();
        Point3D direita = new Point3D(frenteHorizontal.getZ(), 0, -frenteHorizontal.getX());

        for (Point3D base : bases) {
            pontosRadar.getChildren().add(pontoRadar(base, posicaoNave, frenteHorizontal, direita, Color.WHITE, 4));
        }
        for (Point3D inimigo : inimigos) {
            pontosRadar.getChildren().add(pontoRadar(inimigo, posicaoNave, frenteHorizontal, direita, VERMELHO, 3));
        }
    }

    private Circle pontoRadar(Point3D alvo, Point3D posicaoNave, Point3D frente, Point3D direita, Color cor, double raio) {
        Point3D relativo = alvo.subtract(posicaoNave);
        double x = relativo.dotProduct(direita) / ALCANCE_RADAR * RAIO_RADAR;
        double y = -relativo.dotProduct(frente) / ALCANCE_RADAR * RAIO_RADAR;
        double distancia = Math.sqrt(x * x + y * y);
        double limite = RAIO_RADAR - 4;
        if (distancia > limite) {
            x = x / distancia * limite;
            y = y / distancia * limite;
        }
        return new Circle(radarX + x, radarY + y, raio, cor);
    }

    public void mostrarAtracagem(boolean perto, double fracao) {
        if (fracao > 0) {
            atracagem.setText("ATRACANDO " + (int) (fracao * 100) + "%");
        } else if (perto) {
            atracagem.setText("BASE PRÓXIMA: aperte Q");
        }
        atracagem.setVisible(perto || fracao > 0);
    }

    public void mostrarFim(boolean vitoria, int abates, double tempoDeJogo) {
        double largura = getAppWidth();
        double altura = getAppHeight();
        Rectangle fundo = new Rectangle(largura, altura, Color.rgb(0, 0, 0, 0.75));
        Text titulo = texto(vitoria ? "VOCÊ VENCEU!" : "VOCÊ PERDEU", vitoria ? VERDE : VERMELHO, 40);
        Text detalhes = texto("Abates: " + abates + "     Tempo: " + formatarTempo(tempoDeJogo), Color.WHITE, 20);
        Text dica = texto("Aperte ESC para voltar ao menu", Color.LIGHTGRAY, 16);
        posicionar(titulo, largura / 2 - 210, altura / 2 - 20);
        posicionar(detalhes, largura / 2 - 140, altura / 2 + 30);
        posicionar(dica, largura / 2 - 130, altura / 2 + 70);
        getGameScene().addUINodes(fundo, titulo, detalhes, dica);
    }

    public void piscarDano() {
        clarao.setOpacity(0.45);
    }

    private static String formatarTempo(double segundos) {
        int total = (int) segundos;
        return String.format("%d:%02d", total / 60, total % 60);
    }

    private static Text texto(String conteudo, Color cor, double tamanho) {
        return getUIFactoryService().newText(conteudo, cor, tamanho);
    }

    private static void posicionar(Node no, double x, double y) {
        no.setTranslateX(x);
        no.setTranslateY(y);
    }
}
