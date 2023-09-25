package objects;

public class Test {

    public static void main(String[] args) {

        float t = 0f;
        float deltaT = 0.01f; // [s]
        int tf = 5; // [s]

        int maxStep = (int) (tf / deltaT);

        System.out.println(maxStep);

    }
}
