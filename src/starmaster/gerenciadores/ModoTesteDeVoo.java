package starmaster.gerenciadores;

import java.util.ArrayList;
import java.util.List;

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
import starmaster.uteis.GerarAleatorio;


public class ModoTesteDeVoo {

    private static final int ESTRELAS = 900;
    private static final double METADE_CAMPO = 900;        
    private static final double GIRO_POR_SEGUNDO = 50;      
    private static final double VELOCIDADE_TIRO = 200;
    private static final double DURACAO_TIRO = 10.0;
    private static final double INCLINACAO_MAXIMA = 180;
    public static final double CANHAO_FRENTE = 12.0;
    public static final double CANHAO_LADO = 2.5;
    public static final double CANHAO_ABAIXO = 1.5;
    private static final double RAIO_NAVE = 4;
    private static final double CHANCE_RUMO_DE_COLISAO = 0.3;
    private static final double MEIO_LASER = 8;
    private static final double RAIO_LASER = 0.45;
    private static final int MAX_METEOROS = 25;
    private static final int MAX_BASES = 1;
    private static final int MAX_INIMIGOS = 5;
    private double tempoProximoMeteoro = 1.0;

    private final NaveJogador nave = new NaveJogador();
    private final List<NaveInimiga> inimigos = new ArrayList<>();
    private final List<Entity> estrelas = new ArrayList<>();
    private final List<BaseEspacial> bases = new ArrayList<>();
    private final List<Entity> naveInimigas = new ArrayList<>();
    private Camera3D camera;
    private HudTesteDeVoo hud;

    public void iniciar() {
        getGameScene().setBackgroundColor(Color.BLACK);
        camera = getGameScene().getCamera3D();
        camera.getPerspectiveCamera().setFarClip(4000);
        camera.getPerspectiveCamera().setNearClip(0.5);
        camera.getTransform().setPosition3D(0, 0, 0);

        for (int i = 0; i < MAX_INIMIGOS; i++) {
            inimigos.add(new NaveInimiga());
        }
        criarNaveInimiga();

        for (int i = 0; i < MAX_BASES; i++) {
            BaseEspacial base = new BaseEspacial();
            bases.add(base);
            nave.setPosicaoBase(base.getPosicao());
            for (NaveInimiga inimigo : inimigos) {
                inimigo.setPosicaoBase(base.getPosicao());
            }
            criarBase(base);
        }


        entityBuilder().view(new AmbientLight(Color.WHITE)).buildAndAttach();

        PhongMaterial[] cores = {
            new PhongMaterial(Color.WHITE), new PhongMaterial(Color.web("#cfe2ff")), new PhongMaterial(Color.web("#fff1c9"))
        };
        for (int i = 0; i < ESTRELAS; i++) {
            double tamanho = 1.5 + GerarAleatorio.gerarReal() * 2.5;
            Sphere estrela = new Sphere(tamanho / 2, 8);   // raio, divisões
            estrela.setMaterial(cores[GerarAleatorio.gerarInteiro(cores.length)]);
            estrelas.add(entityBuilder()
                    .at(sortear(), sortear(), sortear())
                    .view(estrela)
                    .buildAndAttach());
        }
    }

    public void criarHud() {
        hud = new HudTesteDeVoo();
        hud.adicionar();
    }

    private void criarBase(BaseEspacial base) {
        entityBuilder()
                .at(base.getPosicao().getX(),
                    base.getPosicao().getY(),
                    base.getPosicao().getZ())
                .view(base.getModelo())
                .buildAndAttach();
    }

    private void criarNaveInimiga() {
        for (NaveInimiga inimigo : inimigos) {
            Entity naveInimiga = entityBuilder()
                    .at(inimigo.getPosicao().getX(),
                        inimigo.getPosicao().getY(),
                        inimigo.getPosicao().getZ())
                    .view(inimigo.getModelo())
                    .buildAndAttach();
            naveInimigas.add(naveInimiga);
        }
    }

    private void atualizarRotacaoInimigo(NaveInimiga inimigo, Entity naveInimiga) {
        Point3D direcao = inimigo.getDirecao().normalize();

        double yaw = Math.toDegrees(
            Math.atan2(direcao.getX(), direcao.getZ())
        );

        double pitch = Math.toDegrees(
            Math.asin(direcao.getY())
        );

        TransformComponent transform =
                naveInimiga.getTransformComponent();

        transform.setRotationY(yaw);
        transform.setRotationX(-pitch);
    }

 
    public void atualizar(double tpf) {
        nave.atualizar(tpf, camera.getTransform().getPosition3D());
        
        TransformComponent transform = camera.getTransform();
        transform.translate3D(transform.getDirection3D().multiply(nave.getVelocidade() * tpf));
        reposicionarEstrelas(transform.getPosition3D());

        atualizarInimigos(tpf);
        atualizarMeteoros(tpf);
        if (hud != null) {
            hud.atualizar(nave, tpf);
        }
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
        if (!nave.atirar()) {
            return;
        }
        TransformComponent transform = camera.getTransform();
        Point3D frente = transform.getDirection3D();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalize();
        double lado = nave.isCanhaoEsquerdo() ? -CANHAO_LADO : CANHAO_LADO; // condição valor se verdadeiro ? valor se falso
        nave.trocarCanhao();

    
        Point3D saida = transform.getPosition3D()
                .add(frente.multiply(CANHAO_FRENTE))
                .add(direita.multiply(lado))
                .subtract(cima.multiply(CANHAO_ABAIXO));

        Cylinder laser = new Cylinder(0.45, 16);
        laser.setMaterial(new PhongMaterial(Color.web("#ff0000")));
        apontar(laser, frente);

        entityBuilder()
                .type(TipoEntidade.TIRO)
                .at(saida)
                .view(laser)
                .with(new ComponenteTiro(frente.multiply(VELOCIDADE_TIRO + nave.getVelocidade()), DURACAO_TIRO))
                .buildAndAttach();
    }

    public void atirar(NaveInimiga inimigo, Entity naveInimigo){
        /*System.out.println("Direção lógica: " + inimigo.getDirecao());
        System.out.println("Direção da entidade: " +
        naveInimigo.getTransformComponent().getDirection3D()); */

        if(!inimigo.atirar()){
            return;
        }
        TransformComponent transform = naveInimigo.getTransformComponent();
        Point3D frente = inimigo.getDirecao();//transform.getDirection3D();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalize();
        double lado = inimigo.isCanhaoEsquerdo()? -CANHAO_LADO : CANHAO_LADO; // condição valor se verdadeiro ? valor se falso
        inimigo.trocarCanhao();

    
        Point3D saida = transform.getPosition3D()
                .add(frente.multiply(CANHAO_FRENTE))
                .add(direita.multiply(lado))
                .subtract(cima.multiply(CANHAO_ABAIXO));

        Cylinder laser = new Cylinder(0.45, 16);
        laser.setMaterial(new PhongMaterial(Color.web("#00ff22")));
        apontar(laser, frente);

        entityBuilder()
                .type(TipoEntidade.TIRO)
                .at(saida)
                .view(laser)
                .with(new ComponenteTiro(frente.multiply(VELOCIDADE_TIRO + inimigo.getVelocidade()), DURACAO_TIRO))
                .buildAndAttach();
    }

   
    public void simularDano() {
        int dano = GerarAleatorio.gerarInteiro(151);
        nave.receberDano(dano);
        if (hud != null) {
            hud.piscarDano();
        }
    }

    public void recarregar() {
        nave.recarregar();
    }

    public NaveJogador getNave() {
        return nave;
    }

    private double entre(double minimo, double maximo){
        return minimo+GerarAleatorio.gerarReal() * (maximo - minimo);
    }

    private void atualizarInimigos(double tpf){
        for (int i = 0; i < inimigos.size(); i++){
            NaveInimiga inimigo = inimigos.get(i);
            Entity naveInimiga = naveInimigas.get(i);

            inimigo.atualizar(tpf, nave.getPosicao());

            naveInimiga.getTransformComponent().setPosition3D(
                inimigo.getPosicao().getX(),
                inimigo.getPosicao().getY(),
                inimigo.getPosicao().getZ()
            );

            atualizarRotacaoInimigo(inimigo, naveInimiga);

            if (inimigo.getEstado() == EstadoInimigo.ATAQUE) {
                atirar(inimigo, naveInimiga);
            }
        }
    }

    private void atualizarMeteoros(double tpf){
        TransformComponent transform = camera.getTransform();
        Point3D posicaoNave = transform.getPosition3D();
        Point3D frente = transform.getDirection3D();
        tempoProximoMeteoro -= tpf;
        if (tempoProximoMeteoro <=0){
            tempoProximoMeteoro = entre(0.4, 1.2);
            if (getGameWorld().getEntitiesByType(TipoEntidade.METEORO).size() < MAX_METEOROS){
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
            Point3D velocidade = dadosTiro.getVelocidade();
            Point3D direcao = velocidade.normalize();
            Point3D pontaTras = tiro.getPosition3D().subtract(velocidade.multiply(tpf)).subtract(direcao.multiply(MEIO_LASER));
            Point3D pontaFrente = tiro.getPosition3D().add(direcao.multiply(MEIO_LASER));
            for (Entity meteoro : meteoros) {
                ComponenteMeteoro dados = meteoro.getComponent(ComponenteMeteoro.class);
                if (dados.estaDestruido()) {
                    continue;   
                }
                double distancia = distanciaAteSegmento(meteoro.getPosition3D(), pontaTras, pontaFrente);
                if (distancia < dados.getRaio() + RAIO_LASER) {
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
                if (hud != null) {
                    hud.piscarDano();
                }
                Explosao.criar(meteoro.getPosition3D(), dados.getRaio());
                meteoro.removeFromWorld();
            }
        }
        
    }

    private void criarMeteoro(TransformComponent transform) {
        Point3D frente = transform.getDirection3D();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalize();

        boolean emRumoDeColisao = GerarAleatorio.gerarReal() < CHANCE_RUMO_DE_COLISAO;
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
        return (GerarAleatorio.gerarReal() * 2 - 1) * METADE_CAMPO;
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
