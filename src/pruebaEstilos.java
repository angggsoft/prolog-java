import java.util.stream.Collectors;

public class pruebaEstilos {
    public static void main(String[] args) {

        System.out.println(vocales1("programación"));
        System.out.println(vocales2("programacion"));
        System.out.println(vocales3("programacion"));
        System.out.println(vocales4("programacion"));
    }

    public static String vocales1(String text) {
        String vocales = "aeiouAEIOUó";
        String resultado = "";

        for (char caracter : text.toCharArray()) {
            if(vocales.indexOf(caracter) != -1) {
                resultado += caracter;
            }
        }

        return resultado;
    }

    public static String vocales2(String text) {
        String vocales = "aeiouAEIOU";

        if(text.equals("")) {
            return "";
        }

        char head = text.charAt(0);
        String tail = text.substring(1);

        if(vocales.indexOf(head) != -1) {
            return head+vocales2(tail);
        } else {
            return vocales2(tail);
        }
    }

    public static String vocales3(String text) {
        String vocales = "aeiouAEIOU";

        return text.chars().filter(c->vocales.indexOf(c)!=-1).mapToObj(Character::toString).toString();
    }

    public static String vocales4(String text) {
        String vocales = "aeiouAEIOU";

        return text.chars().mapToObj(Character::toString).reduce("",(acum,c)->vocales.contains(c)?acum+c:acum);
    }
}
