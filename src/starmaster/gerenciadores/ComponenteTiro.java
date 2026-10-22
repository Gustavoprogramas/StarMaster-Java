package starmaster.gerenciadores;

import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point3D;


public class ComponenteTiro extends Component {

    private final Point3D velocidade;
    private double tempoRestante;

    public ComponenteTiro(Point3D velocidade, double duracao) {
        this.velocidade = velocidade;
        this.tempoRestante = duracao;
    }
    public Point3D getVelocidade() {
        return velocidade;
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translate3D(velocidade.multiply(tpf));
        tempoRestante -= tpf;
        if (tempoRestante <= 0) {
            entity.removeFromWorld();
        }
    }
}
