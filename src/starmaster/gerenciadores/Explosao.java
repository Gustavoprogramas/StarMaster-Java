package starmaster.gerenciadores;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

import java.util.Random;

import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;


public final class Explosao {

    private static final Random SORTEIO = new Random();
    private static final Color[] CORES_PEDACOS = {
        Color.web("#ffd56b"), Color.web("#ffb347"), Color.web("#ff6a00"),
        Color.web("#6b5a4a"), Color.web("#85715d")
    };

    private Explosao() {
    }

    /**
     * @param centro  
     * @param tamanho
     */
    public static void criar(Point3D centro, double tamanho) {
        Sphere bola = new Sphere(1, 16);
        PhongMaterial fogo = new PhongMaterial(Color.web("#fff6b0"));
        bola.setMaterial(fogo);
        entityBuilder().at(centro).view(bola).with(new BolaDeFogo(bola, fogo, tamanho)).buildAndAttach();

        int quantidade = (int) (6 + tamanho * 1.2);
        for (int i = 0; i < quantidade; i++) {
            double lado = tamanho * (0.15 + SORTEIO.nextDouble() * 0.2);
            Box pedaco = new Box(lado, lado, lado);
            pedaco.setMaterial(new PhongMaterial(CORES_PEDACOS[SORTEIO.nextInt(CORES_PEDACOS.length)]));
            Point3D velocidade = ComponenteMeteoro.eixoAleatorio().multiply(tamanho * (3 + SORTEIO.nextDouble() * 5));
            double duracao = 0.6 + SORTEIO.nextDouble() * 0.5;
            entityBuilder().at(centro).view(pedaco).with(new Pedaco(pedaco, velocidade, duracao)).buildAndAttach();
        }
    }

   
    private static class BolaDeFogo extends Component {
        private static final double DURACAO = 0.45;
        private static final Color INICIO = Color.web("#fff6b0");
        private static final Color FIM = Color.web("#ff3a00");

        private final Sphere bola;
        private final PhongMaterial material;
        private final double tamanho;
        private double tempo;

        BolaDeFogo(Sphere bola, PhongMaterial material, double tamanho) {
            this.bola = bola;
            this.material = material;
            this.tamanho = tamanho;
            escalar(0.01);
        }

        @Override
        public void onUpdate(double tpf) {
            tempo += tpf;
            double t = Math.min(1, tempo / DURACAO);     
            escalar(tamanho * (0.2 + 1.6 * Math.sin(Math.PI * t)));
            material.setDiffuseColor(INICIO.interpolate(FIM, t));
            if (t >= 1) {
                entity.removeFromWorld();
            }
        }

        private void escalar(double e) {
            bola.setScaleX(e);
            bola.setScaleY(e);
            bola.setScaleZ(e);
        }
    }

  
    private static class Pedaco extends Component {
        private final Box caixa;
        private final Point3D velocidade;
        private final double duracao;
        private final Rotate giro = new Rotate(0, ComponenteMeteoro.eixoAleatorio());
        private double tempo;

        Pedaco(Box caixa, Point3D velocidade, double duracao) {
            this.caixa = caixa;
            this.velocidade = velocidade;
            this.duracao = duracao;
            caixa.getTransforms().add(giro);
        }

        @Override
        public void onUpdate(double tpf) {
            tempo += tpf;
            entity.translate3D(velocidade.multiply(tpf));
            giro.setAngle(giro.getAngle() + 400 * tpf);
            double restante = Math.max(0, 1 - tempo / duracao);
            caixa.setScaleX(restante);
            caixa.setScaleY(restante);
            caixa.setScaleZ(restante);
            if (tempo >= duracao) {
                entity.removeFromWorld();
            }
        }
    }
}
