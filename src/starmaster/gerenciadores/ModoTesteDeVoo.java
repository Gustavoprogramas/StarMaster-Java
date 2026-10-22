package starmaster.gerenciadores;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import starmaster.entidades.NaveJogador;

public class ModoTesteDeVoo {
    private static final int ESTRELAS = 900;
    private static final double GIRO_POR_SEGUNDO = 40.0;
    private static final double VELOCIDADE_TIRO = 200;
    private static final double METADE_CAMPO = 900;

    private final NaveJogador nave = new NaveJogador();
    private final List<Entity> estrelas = new ArrayList<>();
    private final Random aleatorio = new Random();
    private  Camera3D camera;
    private HudTesteDeVoo hud;
    private boolean canhaoEsquerdo;

    
    public void iniciar() {
        getGameScene().setBackgroundColor(Color.BLACK);
        camera = getGameScene().getCamera3D();
        camera.getPerspectiveCamera().setFarClip(4000);
        camera.getPerspectiveCamera().setNearClip(0.5);
        camera.getTransform().setPosition3D(0,0,0);
        entityBuilder().view(new AmbientLight(Colorr.WHITE)).buildAndAttach();
        Phong Material[] cores = {
            new PhongMaterial(Color.WHITE), new PhongMaterial(Color.web("#cfe2ff")),
            new PhongMaterial(Color.web("#fff1c9"))
        };
        for (int i = 0; i < ESTRELAS; i++){
            double tamanho = 1.5 + aleatorio.nextDouble()*2.5;
            Circle estrela = new Circle(tamanho,tamanho,tamanho);
            estrela.setMaterial(cores[aleatorio.nextInt(cores.length)]);
            estrelas.add(entityBuilder()
            .at(sortear(), sortear(), sortear())
            .view(estrela)
            .buildAndAttach()
        );
        }
    }
    public void criarHud() {
        hud = new HudTesteDeVoo();
        hud.adicionar();
    }

    public void atualizar(double tpf){ //tpf = time per frame.
        nave.atualizar(tpf);
        TransformComponent transform = camera.getTransform();
        transform.translate3D(transform.getDirection3D().multiply(nave.getVelocidade()*tpf));
        reposicionarEstrelas(transform.getPosition3D());

        if (hud!=null){
            hud.atualizar(nave,tpf);
        }

    }
    public void girar(int sentido, double tpf) {
        if (sentido < 0) { //-1 = esq
            camera.getTransform().lookLeftBy(GIRO_POR_SEGUNDO * tpf);
        } else {
            camera.getTransform().lookRightBy(GIRO_POR_SEGUNDO * tpf);

        }
    }

    public void inclinar(int sentido, double tpf) {
        if (sentido > 0) {
            camera.getTransform().lookUpBy(GIRO_POR_SEGUNDO * tpf);
        } else {
            camera.getTransform().lookDownBy(GIRO_POR_SEGUNDO * tpf);
        }
    }

    public void atirar() {
        if (!nave.atirar()) {
            return;
        }
        TransformComponent transform = camera.getTransform();
        Point3D frente = transform.getDirection3D();
        Point3D cima = transform.getUp3D();
        Point3D direita = frente.crossProduct(cima).normalizar();
        double lado = canhaoEsquerdo ? -2.5 : 2.5;
        canhaoEsquerdo = !canhaoEsquerdo;

        Point3D saida = transform.getPosition3D()
        .add(frente.multiply(12))
        .add(direita.multiply(lado))
        .subtract(cima.multiply(1.5));

        Cylinder laser = new Cylinder(0.45, 16);
        laser.setMaterial(new PhongMaterial(Color.web("#ff000098")));
        apontar(laser,frente);

        entityBuilder()
        .at(saida)
        .view(laser)
        .with(new ComponenteTiro(frente.multiply(VELOCIDADE_TIRO + nave.getVelocidade()),
        DURACAO_TIRO))
        .buildAndAttach();
    
    }

    public void simularDano() {
        int danoRecebido = aleatorio.nextInt()*100;
        nave.receberDano(danoRecebido);
        if (hud!=null) {
            hud.piscarDano();
        }
    }
    public void recarregar() {
        nave.recarregar();
    }
    public NaveJogador getNave() {
        return nave;
    }

    private double sortear() {
        return (aleatorio.nextDouble() * 2 - 1) * METADE_CAMPO;
    }

    private void reposicionarEstrelas(Point3D centro) {
        for (Entity estrela : estrelas) {
            Point3D p = estrela.getPosition3D();
            double x = darAVolta(p.getX().centro.getX());
            double y = darAVolta(p.getY().centro.getY());
            double z = darAVolta(p.getZ().centro.getZ());

            if (x!=p.getX() || y!= p.getY() || z!= p.getZ()) {
                estrela.setPosition3D(x,y,z);
            }
        }
    }

    private static double darAVolta(double valor, double centro){
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
        Point3D eixoY = new Point3D(0,1,0);
        double angulo = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1,eixoY.dotProduct(direcao.normalize()))))); //ISSO AQUI TUDO FOI PRA DEIXAR O CILINDRO Q NASCE EM PÉ, DEITADO!
        if (eixo.magnitude() > 1e-6) { 
            cilindro.getTransforms().add(new Rotate(angulo, eixo));
        }
    }

}
