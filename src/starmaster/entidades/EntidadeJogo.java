package starmaster.entidades;
import javafx.geometry.Point3D;

public abstract class EntidadeJogo {

    protected boolean ativo = true;

  
    public abstract void atualizar(double tpf, Point3D novaPosicao);

    public void desativar() {
        ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
