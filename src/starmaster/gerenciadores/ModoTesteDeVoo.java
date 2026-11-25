package starmaster.gerenciadores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.almasb.fxgl.app.scene.Camera3D;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import starmaster.entidades.BaseEspacial;
import starmaster.entidades.NaveInimiga;
import starmaster.entidades.NaveJogador;
import starmaster.mapa.MapaGalactico;
import starmaster.mapa.Setor;
import starmaster.mapa.TipoDeSetor;
import starmaster.uteis.GerarAleatorio;

public class ModoTesteDeVoo {

    private static final int ESTRELAS = 900;
    private static final double METADE_CAMPO = 900;
    private static final double GIRO_POR_SEGUNDO = 50;
    private static final double VELOCIDADE_TIRO = 500;
    private static final double VELOCIDADE_TIRO_INIMIGO = 400;
    private static final double DURACAO_TIRO = 2.0;
    private static final double INCLINACAO_MAXIMA = 45;
    public static final double CANHAO_FRENTE = 12.0;
    public static final double CANHAO_LADO = 2.5;
    public static final double CANHAO_ABAIXO = 1.5;
    private static final double RAIO_NAVE = 4;
    private static final double CHANCE_RUMO_DE_COLISAO = 0.3;
    private static final double MEIO_LASER = 8;
    private static final double RAIO_LASER = 0.45;
    private static final int MAX_METEOROS = 25;
    private static final double FORCA_RECUO = 150;
    private static final double AMORTECIMENTO = 3;
    private static final int INIMIGOS_POR_SETOR = 4;
    private static final double ESPALHAR_INIMIGOS = 250;
    private static final double RAIO_INIMIGO = 8;
    private static final int DANO_TIRO_INIMIGO = 10;



    private final NaveJogador nave = new NaveJogador();
    private final List<Entity> estrelas = new ArrayList<>();
    private final Random aleatorio = new Random();
    private final List<BaseEspacial> bases = new ArrayList<>();
    private final List<Entity> naveInimigas = new ArrayList<>();
    private final List<NaveInimiga> inimigos = new ArrayList<>();
    private final Map<Setor, Integer> inimigosRestantes = new HashMap<>(); // Criando a hash map para contar quantos inimigos restaram no setor ao sair do mesmo.
    private Camera3D camera;
    private HudTesteDeVoo hud;
    private boolean canhaoEsquerdo;
    private GerenciadorMapa gerenciadorMapa;
    private boolean setorDeMeteoros;
    private Point3D recuo = Point3D.ZERO;
    private double tempoProximoMeteoro = 1.0;
    private boolean atracando;
    private double progressoAtracagem;
    private int abates;
    private double tempoDeJogo;
    private boolean fimDeJogo;

    public void iniciar() {
        gerenciadorMapa = new GerenciadorMapa(nave, this::entrarNoSetor);
        getGameScene().setBackgroundColor(Color.BLACK);
        camera = getGameScene().getCamera3D();
        camera.getPerspectiveCamera().setFarClip(4000);
        camera.getPerspectiveCamera().setNearClip(0.5);

        Setor inicio = gerenciadorMapa.getSetorAtual();
        camera.getTransform().setPosition3D(
                inicio.getLinha() * GerenciadorMapa.TAMANHO_SETOR + 500, 0,
                inicio.getColuna() * GerenciadorMapa.TAMANHO_SETOR + 100);

        MapaGalactico mapa = gerenciadorMapa.getMapa();
        for (int i = 0; i < mapa.getLinhas(); i++) {
            for (int j = 0; j < mapa.getColunas(); j++) {
                if (mapa.getSetor(i, j).getTipo() == TipoDeSetor.BASE) {
                    BaseEspacial base = new BaseEspacial();
                    base.setPosicao(gerenciadorMapa.centroDoSetor(i, j));
                    bases.add(base);
                    entityBuilder().at(base.getPosicao()).view(base.getModelo()).buildAndAttach();
                }
            }
        }

        entityBuilder().view(new AmbientLight(Color.WHITE)).buildAndAttach();

        PhongMaterial[] cores = {
            new PhongMaterial(Color.WHITE), new PhongMaterial(Color.web("#cfe2ff")), new PhongMaterial(Color.web("#fff1c9"))
        };
        Point3D centro = camera.getTransform().getPosition3D();
        for (int i = 0; i < ESTRELAS; i++) {
            double tamanho = 1.5 + GerarAleatorio.gerarReal() * 2.5;
            Sphere estrela = new Sphere(tamanho / 2, 8);
            estrela.setMaterial(cores[GerarAleatorio.gerarInteiro(cores.length)]);
            estrelas.add(entityBuilder()
                    .at(centro.getX() + sortear(), centro.getY() + sortear(), centro.getZ() + sortear())
                    .view(estrela)
                    .buildAndAttach());
        }
    }

    public void criarHud() {
        hud = new HudTesteDeVoo();
        hud.adicionar();
        gerenciadorMapa.adicionarNaTela();
    }

    public void atualizar(double tpf) {
        if (fimDeJogo) {
            return;
        }
        tempoDeJogo += tpf;

        atualizarAtracagem(tpf);
        nave.atualizar(tpf, camera.getTransform().getPosition3D());
        gerenciadorMapa.atualizarMiniMapa();

        TransformComponent transform = camera.getTransform();
        transform.translate3D(transform.getDirection3D().multiply(nave.getVelocidade() * tpf));
        aplicarRecuo(tpf);
        reposicionarEstrelas(transform.getPosition3D());

        for (BaseEspacial base : bases) {
            base.atualizar(tpf, null);
        }
        atualizarInimigos(tpf);
        atualizarMeteoros(tpf);
        atualizarColisoesInimigos(tpf);
        atualizarColisaoJogador(tpf);
        atualizarColisaoBases(tpf);
        gerenciadorMapa.atualizar(tpf);

        if (hud != null) {
            hud.atualizar(nave, tpf);
            hud.atualizarStatus(gerenciadorMapa.getSetorAtual(), inimigos.size(), abates,
                    gerenciadorMapa.contarSetoresInimigos(), tempoDeJogo);
            hud.atualizarRadar(posicoesInimigos(), posicoesBases(), transform.getPosition3D(), transform.getDirection3D());
            hud.mostrarAtracagem(baseProxima() != null, progressoAtracagem / BaseEspacial.TEMPO_ATRACAGEM);
        }

        verificarFimDeJogo();
    }

    private void verificarFimDeJogo() {
        if (nave.estaDestruido()) {
            terminar(false);
        } else if (gerenciadorMapa.contarSetoresInimigos() == 0) {
            terminar(true);
        }
    }

    private void terminar(boolean vitoria) {
        fimDeJogo = true;
        gerenciadorMapa.fecharMapa();
        if (hud != null) {
            hud.mostrarFim(vitoria, abates, tempoDeJogo);
        }
    }

    public void alternarMapa() {
        if (!fimDeJogo) {
            gerenciadorMapa.alternarMapa();
        }
    }

    public void ajustarVelocidade(int sentido, double tpf) {
        if (atracando && sentido > 0) {
            cancelarAtracagem();
        }
        nave.ajustarVelocidade(sentido, tpf);
    }

    private void entrarNoSetor(Setor setor) {
        Point3D pos = camera.getTransform().getPosition3D();
        int linhaNave = (int) Math.floor(pos.getX() / GerenciadorMapa.TAMANHO_SETOR);
        int colunaNave = (int) Math.floor(pos.getZ() / GerenciadorMapa.TAMANHO_SETOR);
        
        boolean teleporte = linhaNave != setor.getLinha() || colunaNave != setor.getColuna();

        cancelarAtracagem();
        removerInimigos();
        

        if (teleporte) {
            camera.getTransform().setPosition3D(
                    setor.getLinha() * GerenciadorMapa.TAMANHO_SETOR + 500, 0,
                    setor.getColuna() * GerenciadorMapa.TAMANHO_SETOR + 100);
            camera.getTransform().setRotationY(0);
            camera.getTransform().setRotationX(0);
            recuo = Point3D.ZERO;
            for (Entity meteoro : getGameWorld().getEntitiesByType(TipoEntidade.METEORO)) {
                meteoro.removeFromWorld();
            }
            Point3D centro = camera.getTransform().getPosition3D();
            for (Entity estrela : estrelas) {
                estrela.setPosition3D(centro.getX() + sortear(), centro.getY() + sortear(), centro.getZ() + sortear());
            }
        }

        setorDeMeteoros = setor.getTipo() == TipoDeSetor.METEOROS;
        tempoProximoMeteoro = 1.0;

        if (setor.getTipo() == TipoDeSetor.INIMIGO) {
            
            criarInimigos(setor);
        }
    }

    private void criarInimigos(Setor setor) {

        Point3D centro = gerenciadorMapa.centroDoSetor(setor.getLinha(), setor.getColuna());
        int quantidade = inimigosRestantes.getOrDefault(setor, INIMIGOS_POR_SETOR); //getOrDefault devolve quantos inimigos sobraram no setor
        for (int i = 0; i < quantidade; i++) {
            Point3D posicao = centro.add(
                    entre(-ESPALHAR_INIMIGOS, ESPALHAR_INIMIGOS),
                    entre(-60, 60),
                    entre(-ESPALHAR_INIMIGOS, ESPALHAR_INIMIGOS));
            NaveInimiga inimigo = new NaveInimiga(posicao);
            inimigos.add(inimigo);
            naveInimigas.add(entityBuilder().at(posicao).view(inimigo.getModelo()).buildAndAttach());
        }
    }

    private void removerInimigos() {
        for (Entity naveInimiga : naveInimigas) {
            naveInimiga.removeFromWorld();
        }
        naveInimigas.clear();
        inimigos.clear();
        for (Entity tiro : getGameWorld().getEntitiesByType(TipoEntidade.TIRO)) {
            if (!tiro.getComponent(ComponenteTiro.class).isTiroJogador()) {
                tiro.removeFromWorld();
            }
        }
    }

    private void atualizarRotacaoInimigo(NaveInimiga inimigo, Entity naveInimiga) {
        Point3D direcao = inimigo.getDirecao().normalize();
        double yaw = Math.toDegrees(Math.atan2(direcao.getX(), direcao.getZ()));
        double pitch = Math.toDegrees(Math.asin(Math.max(-1, Math.min(1, direcao.getY()))));
        TransformComponent transform = naveInimiga.getTransformComponent();
        transform.setRotationY(yaw);
        transform.setRotationX(-pitch);
    }

    public void girar(int sentido, double tpf) {
        if (sentido < 0) {
            camera.getTransform().lookLeftBy(GIRO_POR_SEGUNDO * tpf);
        } else {
            camera.getTransform().lookRightBy(GIRO_POR_SEGUNDO * tpf);
        }
    }

    public void inclinar(int sentido, double tpf) {
        double novo = camera.getTransform().getRotationX() + sentido * GIRO_POR_SEGUNDO * tpf;
        novo = Math.max(-INCLINACAO_MAXIMA, Math.min(INCLINACAO_MAXIMA, novo));
        camera.getTransform().setRotationX(novo);
    }

    public void atirar() {
        if (fimDeJogo || !nave.atirar()) {
            return;
        }
        TransformComponent transform = camera.getTransform();
        Point3D frente = transform.getDirection3D();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalize();
        double lado = canhaoEsquerdo ? -CANHAO_LADO : CANHAO_LADO;
        canhaoEsquerdo = !canhaoEsquerdo;

        Point3D saida = transform.getPosition3D()
                .add(frente.multiply(CANHAO_FRENTE))
                .add(direita.multiply(lado))
                .subtract(cima.multiply(CANHAO_ABAIXO));

        Cylinder laser = new Cylinder(0.45, 16);
        laser.setMaterial(new PhongMaterial(Color.web("#ff9a1f")));
        apontar(laser, frente);

        entityBuilder()
                .type(TipoEntidade.TIRO)
                .at(saida)
                .view(laser)
                .with(new ComponenteTiro(frente.multiply(VELOCIDADE_TIRO + nave.getVelocidade()), DURACAO_TIRO, true))
                .buildAndAttach();
    }

    private void atirar(NaveInimiga inimigo, Entity naveInimigo) {
        if (!inimigo.atirar()) {
            return;
        }
        TransformComponent transform = naveInimigo.getTransformComponent();
        Point3D frente = inimigo.getDirecao();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalize();
        double lado = inimigo.isCanhaoEsquerdo() ? -CANHAO_LADO : CANHAO_LADO;
        inimigo.trocarCanhao();

        Point3D saida = transform.getPosition3D()
                .add(frente.multiply(CANHAO_FRENTE))
                .add(direita.multiply(lado))
                .subtract(cima.multiply(CANHAO_ABAIXO));

        Cylinder laser = new Cylinder(0.45, 16);
        laser.setMaterial(new PhongMaterial(Color.web("#3dff5a")));
        apontar(laser, frente);

        entityBuilder()
                .type(TipoEntidade.TIRO)
                .at(saida)
                .view(laser)
                .with(new ComponenteTiro(frente.multiply(VELOCIDADE_TIRO_INIMIGO + inimigo.getVelocidade()), DURACAO_TIRO, false))
                .buildAndAttach();
    }

    public NaveJogador getNave() {
        return nave;
    }

    public void alternarAtracagem() {
        if (fimDeJogo) {
            return;
        }
        if (atracando) {
            cancelarAtracagem();
        } else if (baseProxima() != null) {
            atracando = true;
            progressoAtracagem = 0;
            nave.setAtracando(true);
        }
    }

    private void cancelarAtracagem() {
        atracando = false;
        progressoAtracagem = 0;
        nave.setAtracando(false);
    }

    private void atualizarAtracagem(double tpf) {
        if (!atracando) {
            return;
        }
        BaseEspacial base = baseProxima();
        if (base == null) {
            cancelarAtracagem();
            return;
        }
        progressoAtracagem += tpf;
        if (progressoAtracagem >= BaseEspacial.TEMPO_ATRACAGEM) {
            base.abastecer(nave);
            cancelarAtracagem();
            if (hud != null) {
                hud.mostrarMensagem("REABASTECIDO!", 2.0);
            }
        }
    }

    private BaseEspacial baseProxima() {
        Point3D posicao = camera.getTransform().getPosition3D();
        for (BaseEspacial base : bases) {
            if (!base.estaDestruido() && base.estaAoAlcance(posicao)) {
                return base;
            }
        }
        return null;
    }

    private List<Point3D> posicoesInimigos() {
        List<Point3D> posicoes = new ArrayList<>();
        for (NaveInimiga inimigo : inimigos) {
            posicoes.add(inimigo.getPosicao());
        }
        return posicoes;
    }

    private List<Point3D> posicoesBases() {
        List<Point3D> posicoes = new ArrayList<>();
        for (BaseEspacial base : bases) {
            if (!base.estaDestruido()) {
                posicoes.add(base.getPosicao());
            }
        }
        return posicoes;
    }

    private double entre(double minimo, double maximo) {
        return minimo + aleatorio.nextDouble() * (maximo - minimo);
    }

    private void atualizarInimigos(double tpf) {
        for (int i = 0; i < inimigos.size(); i++) {
            NaveInimiga inimigo = inimigos.get(i);
            Entity naveInimiga = naveInimigas.get(i);

            inimigo.atualizar(tpf, nave.getPosicao());
            naveInimiga.getTransformComponent().setPosition3D(inimigo.getPosicao());
            atualizarRotacaoInimigo(inimigo, naveInimiga);

            if (inimigo.getEstado() == EstadoInimigo.ATAQUE) {
                atirar(inimigo, naveInimiga);
            }
        }
    }

    private void atualizarMeteoros(double tpf) {
        TransformComponent transform = camera.getTransform();
        Point3D posicaoNave = transform.getPosition3D();
        Point3D frente = transform.getDirection3D();

        tempoProximoMeteoro -= tpf;
        if (tempoProximoMeteoro <= 0) {
            tempoProximoMeteoro = setorDeMeteoros ? entre(0.1, 0.3) : entre(0.8, 2.0);
            if (getGameWorld().getEntitiesByType(TipoEntidade.METEORO).size() < MAX_METEOROS) {
                criarMeteoro(transform);
            }
        }

        for (Entity meteoro : getGameWorld().getEntitiesByType(TipoEntidade.METEORO)) {
            Point3D ateMeteoro = meteoro.getPosition3D().subtract(posicaoNave);
            if (ateMeteoro.dotProduct(frente) < -60 || ateMeteoro.magnitude() > 1600) {
                meteoro.removeFromWorld();
            }
        }

        List<Entity> meteoros = getGameWorld().getEntitiesByType(TipoEntidade.METEORO);
        for (Entity tiro : getGameWorld().getEntitiesByType(TipoEntidade.TIRO)) {
            ComponenteTiro dadosTiro = tiro.getComponent(ComponenteTiro.class);
            if (!dadosTiro.isTiroJogador()) {
                continue;
            }
            Point3D[] laser = segmentoDoLaser(tiro, dadosTiro, tpf);
            for (Entity meteoro : meteoros) {
                ComponenteMeteoro dados = meteoro.getComponent(ComponenteMeteoro.class);
                if (dados.estaDestruido()) {
                    continue;
                }
                if (distanciaAteSegmento(meteoro.getPosition3D(), laser[0], laser[1]) < dados.getRaio() + RAIO_LASER) {
                    Point3D pontoDoAcerto = tiro.getPosition3D();
                    tiro.removeFromWorld();
                    dados.receberDano(nave.getPoderDeFogo());
                    if (dados.estaDestruido()) {
                        Explosao.criar(meteoro.getPosition3D(), dados.getRaio());
                        meteoro.removeFromWorld();
                    } else {
                        Explosao.criar(pontoDoAcerto, 1.5);
                    }
                    break;
                }
            }
        }

        for (Entity meteoro : meteoros) {
            ComponenteMeteoro dados = meteoro.getComponent(ComponenteMeteoro.class);
            if (dados.estaDestruido()) {
                continue;
            }
            if (meteoro.getPosition3D().distance(posicaoNave) < dados.getRaio() + RAIO_NAVE) {
                nave.receberDano(dados.getDano());
                nave.sofrerImpacto();
                empurrarParaLonge(meteoro.getPosition3D(), posicaoNave);
                if (hud != null) {
                    hud.piscarDano();
                }
                Explosao.criar(meteoro.getPosition3D(), dados.getRaio());
                meteoro.removeFromWorld();
            }
        }
    }

    private void atualizarColisoesInimigos(double tpf) {
        for (Entity tiro : getGameWorld().getEntitiesByType(TipoEntidade.TIRO)) {
            ComponenteTiro dadosTiro = tiro.getComponent(ComponenteTiro.class);
            if (!dadosTiro.isTiroJogador()) {
                continue;
            }
            Point3D[] laser = segmentoDoLaser(tiro, dadosTiro, tpf);
            for (int i = 0; i < inimigos.size(); i++) {
                NaveInimiga inimigo = inimigos.get(i);
                if (distanciaAteSegmento(inimigo.getPosicao(), laser[0], laser[1]) < RAIO_INIMIGO + RAIO_LASER) {
                    Point3D pontoDoAcerto = tiro.getPosition3D();
                    tiro.removeFromWorld();
                    inimigo.receberDano(nave.getPoderDeFogo());
                    if (inimigo.estaDestruido()) {
                        Explosao.criar(inimigo.getPosicao(), 10);
                        naveInimigas.get(i).removeFromWorld();
                        naveInimigas.remove(i);
                        inimigos.remove(i);
                        abates++;
                        inimigosRestantes.put(gerenciadorMapa.getSetorAtual(), inimigos.size()); //toda vez que um inimigo morre, isso anota quantos
                        //sobraram neste setor
                    } else {
                        Explosao.criar(pontoDoAcerto, 1.5);
                    }
                    break;
                }
            }
        }

        if (inimigos.isEmpty() && gerenciadorMapa.getSetorAtual().getTipo() == TipoDeSetor.INIMIGO) {
            gerenciadorMapa.liberarSetorAtual();
        }
    }

    private void atualizarColisaoJogador(double tpf) {
        Point3D posicaoNave = camera.getTransform().getPosition3D();
        for (Entity tiro : getGameWorld().getEntitiesByType(TipoEntidade.TIRO)) {
            ComponenteTiro dadosTiro = tiro.getComponent(ComponenteTiro.class);
            if (dadosTiro.isTiroJogador()) {
                continue;
            }
            Point3D[] laser = segmentoDoLaser(tiro, dadosTiro, tpf);
            if (distanciaAteSegmento(posicaoNave, laser[0], laser[1]) < RAIO_NAVE + RAIO_LASER) {
                Point3D pontoDoAcerto = tiro.getPosition3D();
                tiro.removeFromWorld();
                nave.receberDano(DANO_TIRO_INIMIGO);
                if (hud != null) {
                    hud.piscarDano();
                }
                Explosao.criar(pontoDoAcerto, 1.5);
            }
        }
    }

    private void atualizarColisaoBases(double tpf) {
        for (Entity tiro : getGameWorld().getEntitiesByType(TipoEntidade.TIRO)) {
            ComponenteTiro dadosTiro = tiro.getComponent(ComponenteTiro.class);
            if (dadosTiro.isTiroJogador()) {
                continue;
            }
            Point3D[] laser = segmentoDoLaser(tiro, dadosTiro, tpf);
            for (BaseEspacial base : bases) {
                if (distanciaAteSegmento(base.getPosicao(), laser[0], laser[1]) < base.getRaio() + RAIO_LASER) {
                    Point3D pontoDoAcerto = tiro.getPosition3D();
                    tiro.removeFromWorld();
                    base.receberDano(DANO_TIRO_INIMIGO);
                    Explosao.criar(pontoDoAcerto, 1.5);
                    break;
                }
            }
        }
    }

    private Point3D[] segmentoDoLaser(Entity tiro, ComponenteTiro dadosTiro, double tpf) {
        Point3D velocidade = dadosTiro.getVelocidade();
        Point3D direcao = velocidade.normalize();
        Point3D pontaTras = tiro.getPosition3D().subtract(velocidade.multiply(tpf)).subtract(direcao.multiply(MEIO_LASER));
        Point3D pontaFrente = tiro.getPosition3D().add(direcao.multiply(MEIO_LASER));
        return new Point3D[] { pontaTras, pontaFrente };
    }

    private void empurrarParaLonge(Point3D posicaoMeteoro, Point3D posicaoNave) {
        Point3D direcao = posicaoNave.subtract(posicaoMeteoro);
        if (direcao.magnitude() < 1e-6) {
            direcao = camera.getTransform().getDirection3D().multiply(-1);
        }
        recuo = direcao.normalize().multiply(FORCA_RECUO);
    }

    private void aplicarRecuo(double tpf) {
        if (recuo.magnitude() < 0.5) {
            recuo = Point3D.ZERO;
            return;
        }
        camera.getTransform().translate3D(recuo.multiply(tpf));
        recuo = recuo.multiply(Math.exp(-AMORTECIMENTO * tpf));
    }

    private void criarMeteoro(TransformComponent transform) {
        Point3D frente = transform.getDirection3D();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalize();

        boolean emRumoDeColisao = aleatorio.nextDouble() < CHANCE_RUMO_DE_COLISAO;
        double espalhar = emRumoDeColisao ? 25 : 300;

        Point3D posicao = transform.getPosition3D()
                .add(frente.multiply(entre(700, 1000)))
                .add(direita.multiply(entre(-espalhar, espalhar)))
                .add(cima.multiply(entre(-espalhar * 0.7, espalhar * 0.7)));

        Point3D velocidade = emRumoDeColisao
                ? frente.multiply(-entre(20, 60))
                : ComponenteMeteoro.eixoAleatorio().multiply(entre(0, 25));

        ComponenteMeteoro meteoro = new ComponenteMeteoro(entre(6, 16), velocidade);
        entityBuilder()
                .type(TipoEntidade.METEORO)
                .at(posicao)
                .view(meteoro.getRocha())
                .with(meteoro)
                .buildAndAttach();
    }

    private static double distanciaAteSegmento(Point3D ponto, Point3D a, Point3D b) {
        Point3D ab = b.subtract(a);
        double comprimento2 = ab.dotProduct(ab);
        if (comprimento2 == 0) {
            return ponto.distance(a);
        }
        double t = Math.max(0, Math.min(1, ponto.subtract(a).dotProduct(ab) / comprimento2));
        return ponto.distance(a.add(ab.multiply(t)));
    }

    private double sortear() {
        return (aleatorio.nextDouble() * 2 - 1) * METADE_CAMPO;
    }

    private void reposicionarEstrelas(Point3D centro) {
        for (Entity estrela : estrelas) {
            Point3D p = estrela.getPosition3D();
            double x = darAVolta(p.getX(), centro.getX());
            double y = darAVolta(p.getY(), centro.getY());
            double z = darAVolta(p.getZ(), centro.getZ());
            if (x != p.getX() || y != p.getY() || z != p.getZ()) {
                estrela.setPosition3D(x, y, z);
            }
        }
    }

    private static double darAVolta(double valor, double centro) {
        double distancia = valor - centro;
        if (distancia > METADE_CAMPO) {
            return valor - 2 * METADE_CAMPO;
        }
        if (distancia < -METADE_CAMPO) {
            return valor + 2 * METADE_CAMPO;
        }
        return valor;
    }

    private static void apontar(Cylinder cilindro, Point3D direcao) {
        Point3D eixoY = new Point3D(0, 1, 0);
        Point3D eixo = eixoY.crossProduct(direcao);
        double angulo = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, eixoY.dotProduct(direcao.normalize())))));
        if (eixo.magnitude() > 1e-6) {
            cilindro.getTransforms().add(new Rotate(angulo, eixo));
        }
    }
}
