package starmaster.entidades;

public abstract class EntidadeJogo {

    protected boolean ativo = true;

  
    public abstract void atualizar(double tpf);

    public void desativar() {
        ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
