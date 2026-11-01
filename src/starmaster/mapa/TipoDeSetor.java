package starmaster.mapa;

public enum TipoDeSetor {
    VAZIO ('.', 1),
    METEOROS('M', 3),
    BASE ('B', 1),
    INIMIGO  ('X', -1); 

    private final char simbolo;
    private final int custo;
    TipoDeSetor(char simbolo, int custo) {
        this.simbolo = simbolo;
        this.custo = custo;
    }
    public int getCusto() {
        return custo;
    }
    public boolean isBloqueado () {
        return custo<0;

    }

    public char getSimbolo ()  {
        return simbolo;
    }

    public static TipoDeSetor doSimbolo(char c){
        for (TipoDeSetor tipo:values()){
            if (tipo.simbolo == c)  {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de setor esquisito" + c);
        
    }
    
}
