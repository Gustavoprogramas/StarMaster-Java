package starmaster.gerenciadores;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import starmaster.entidades.NaveJogador;
import starmaster.mapa.MapaGalactico;
import starmaster.mapa.Navegador;
import starmaster.mapa.Setor;
import starmaster.ui.MiniMapa;

public class GerenciadorMapa {

    public static final int ENERGIA_POR_SETOR = 20; 

   
    private static final String[] MAPA_INICIAL = {
        "........",
        "...X....",
        "...X..M.",
        ".MMX....",
        "........",
        "..X..MMB",
        "B.X.....",
        "........",
    };

    private final MapaGalactico mapa = MapaGalactico.deTexto(MAPA_INICIAL);
    private final Navegador navegador = new Navegador(mapa);
    private final NaveJogador nave;
    private final Consumer<Setor> aoEntrarNoSetor;  
    private MiniMapa miniMapa;                       
    private Rectangle clarao;                

    private Setor setorAtual;
    private List<Setor> rota;

    public GerenciadorMapa(NaveJogador nave, Consumer<Setor> aoEntrarNoSetor) {
        this.nave = nave;
        this.aoEntrarNoSetor = aoEntrarNoSetor;
        this.setorAtual = mapa.getSetor(1, 1);
        this.rota = navegador.rotaAteBaseMaisProxima(setorAtual);
    }

   
    public void adicionarNaTela() {
        clarao = new Rectangle(getAppWidth(), getAppHeight(), Color.WHITE);
        clarao.setOpacity(0);
        clarao.setMouseTransparent(true);
        getGameScene().addUINode(clarao);

    
        miniMapa = new MiniMapa(this::teleportarPara, this::descrever);
        miniMapa.adicionar();
        miniMapa.desenho(mapa, setorAtual, rota);
    }

  
    public void atualizar(double tpf) {
        if (clarao != null && clarao.getOpacity() > 0) {
            clarao.setOpacity(Math.max(0, clarao.getOpacity() - tpf * 2));
        }
    }

 
    public void alternarMapa() {
        if (miniMapa != null) {
            miniMapa.alternar();
        }
    }

    public void teleportarPara(Setor destino) {
        if (destino == setorAtual) {
            return;
        }
        if (!nave.gastarEnergia(custoTeleporte(destino))) {
            return;   
        }
        setorAtual = destino;
        rota = navegador.rotaAteBaseMaisProxima(setorAtual);  
        aoEntrarNoSetor.accept(destino);

        if (miniMapa != null) {
            miniMapa.desenho(mapa, setorAtual, rota);   
            miniMapa.fechar();
        }
        if (clarao != null) {
            clarao.setOpacity(1);                   
        }
    }


    public int custoTeleporte(Setor destino) {
        return setorAtual.distanciaManhattan(destino) * ENERGIA_POR_SETOR;
    }

    private String descrever(Setor setor) {
        String nome = "Setor (" + setor.getLinha() + "," + setor.getColuna() + ") · " + setor.getTipo();
        if (setor == setorAtual) {
            return nome + " · vc ta aqui";
        }
        int custo = custoTeleporte(setor);
        if (custo > nave.getEnergia()) {
            return nome + " · ENERGIA INSUFICIENTE (" + custo + ")";
        }
        return nome + " · custa " + custo + " de energia";
    }

    public Setor getSetorAtual() {
        return setorAtual;
    }

    public List<Setor> getRota() {
        return rota;
    }
}