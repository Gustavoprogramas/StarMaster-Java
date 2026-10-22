package starmaster.gerenciadores;

import java.util.Random;

import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import starmaster.interfaces.Danificavel;


public class ComponenteMeteoro extends Component implements Danificavel {

    private static final Random SORTEIO = new Random();
    private static final Color[] CORES_ROCHA = {
        Color.web("#6b5a4a"), Color.web("#85715d"), Color.web("#4f4338"), Color.web("#9a8a78")
    };

    private final double raio;
    private final Point3D velocidade;
    private final Group rocha = new Group();
    private final Rotate giro;
    private final double velocidadeGiro;
    private int vida;

    public ComponenteMeteoro(double raio, Point3D velocidade) {
        this.raio = raio;
        this.velocidade = velocidade;
        this.vida = (int) Math.ceil(raio / 6.0) * 10;  
        this.velocidadeGiro = 20 + SORTEIO.nextDouble() * 60;
        this.giro = new Rotate(0, eixoAleatorio());
        montarRocha();
    }


    private void montarRocha() {
        Box nucleo = caixa(raio * 1.3);
        rocha.getChildren().add(nucleo);
        for (int i = 0; i < 4; i++) {
            Box pedaco = caixa(raio * (0.6 + SORTEIO.nextDouble() * 0.4));
            Point3D deslocamento = eixoAleatorio().multiply(raio * 0.45);
            pedaco.setTranslateX(deslocamento.getX());
            pedaco.setTranslateY(deslocamento.getY());
            pedaco.setTranslateZ(deslocamento.getZ());
            rocha.getChildren().add(pedaco);
        }
        rocha.getTransforms().add(giro);
    }

    private Box caixa(double lado) {
        Box b = new Box(lado, lado * (0.7 + SORTEIO.nextDouble() * 0.5), lado * (0.7 + SORTEIO.nextDouble() * 0.5));
        b.setMaterial(new PhongMaterial(CORES_ROCHA[SORTEIO.nextInt(CORES_ROCHA.length)]));
        b.getTransforms().add(new Rotate(SORTEIO.nextDouble() * 360, eixoAleatorio()));
        return b;
    }

    static Point3D eixoAleatorio() {
        Point3D p;
        do {
            p = new Point3D(SORTEIO.nextDouble() * 2 - 1, SORTEIO.nextDouble() * 2 - 1, SORTEIO.nextDouble() * 2 - 1);
        } while (p.magnitude() < 0.1);
        return p.normalize();
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translate3D(velocidade.multiply(tpf));
        giro.setAngle(giro.getAngle() + velocidadeGiro * tpf);
    }

    @Override
    public void receberDano(int quantidade) {
        vida -= quantidade;
    }

    @Override
    public boolean estaDestruido() {
        return vida <= 0;
    }


    public int getDano() {
        return (int) (raio * 2);
    }

    public double getRaio() {
        return raio;
    }

   
    public Group getRocha() {
        return rocha;
    }
}
