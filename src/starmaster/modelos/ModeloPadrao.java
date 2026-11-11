package starmaster.modelos;

import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.TriangleMesh;

public class ModeloPadrao {
    protected Group modelo;

    public ModeloPadrao() {
        modelo = new Group();
    }

    protected Box criarPecaBox(double largura, double altura, double profundidade, Color cor, double x, double y, double z){
        Box peca = new Box(largura, altura, profundidade);

        peca.setMaterial(new PhongMaterial(cor));

        peca.setTranslateX(x);
        peca.setTranslateY(y);
        peca.setTranslateZ(z);

        return peca;
    }

    protected Sphere criarPecaSphere(double raio, Color cor, double x, double y, double z){
        Sphere peca =  new Sphere(raio);

        peca.setMaterial(new PhongMaterial(cor));

        peca.setTranslateX(x);
        peca.setTranslateY(y);
        peca.setTranslateZ(z);

        return peca;
    }

    protected Cylinder criarPecaCylinder(double raio, double altura, Color cor, double x, double y, double z){
        Cylinder peca = new Cylinder(raio, altura);

        peca.setMaterial(new PhongMaterial(cor));

        peca.setTranslateX(x);
        peca.setTranslateY(y);
        peca.setTranslateZ(z);

        return peca;
    }

    protected MeshView criarPecaMeshView(Mesh mesh, Color cor, double x, double y, double z){
        MeshView peca = new MeshView(mesh);

        peca.setMaterial(new PhongMaterial(cor));

        peca.setTranslateX(x);
        peca.setTranslateY(y);
        peca.setTranslateZ(z);

        return peca;
    }

    protected Mesh criarMesh(float[] vertices, int[] faces){
        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(vertices);

        mesh.getTexCoords().addAll(0, 0);

        mesh.getFaces().addAll(faces);

        return mesh;
    }

    public Group getModelo() {
        return modelo;
    }
}
