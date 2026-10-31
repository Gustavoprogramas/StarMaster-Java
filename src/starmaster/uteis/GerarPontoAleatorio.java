package starmaster.uteis;

import javafx.geometry.Point3D;
import java.util.Random;

public class GerarPontoAleatorio {
    public static Random random = new Random();

    public static Point3D gerarPonto(){
        return new Point3D(random.nextDouble()*200-100,random.nextDouble()*200-100,random.nextDouble()*200-100);
    }
}
