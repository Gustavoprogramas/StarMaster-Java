package starmaster.mapa;

public class Setor {
    private final int linha;
    private final int coluna;
    private TipoDeSetor tipo;

    public Setor(int linha, int coluna, TipoDeSetor tipo) {
        this.linha = linha;
        this.coluna = coluna;
        this.tipo = tipo;
    }

    public int getCusto () {
        return tipo.getCusto();
    }
    public boolean isBloqueado() {
        return tipo.isBloqueado();
    }
    public int distanciaManhattan (Setor outro) {
        return Math.abs(linha - outro.linha) + Math.abs(coluna - outro.coluna);

    }
    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
    public TipoDeSetor getTipo(){
        return tipo;
    }
    public void setTipo(TipoDeSetor tipo) {
        this.tipo = tipo;
    }
    @Override 
    public String toString() {
        return " " + linha + " " + coluna + " ";
    }
    
}
