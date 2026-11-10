public class Contador {
    public static void main(String[] args) {
        System.out.println(vocales("programación"));
    }

    public static long vocales(String text) {
        String vocales = "aeiouAEIOUó";
        return text.chars().filter(c->vocales.indexOf(c)!=-1).count();
    }
}
