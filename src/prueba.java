import java.util.stream.Collectors;

public class prueba {
    public static void main(String[] args) {
        System.out.println(vocales1("pepepecas"));
        System.out.println(vocales2("pepepecas"));
        System.out.println(vocales3("pepepecas"));
        System.out.println(vocales4("pepepecas"));

    }

    public static String vocales1 (String text){
        String vocales = "aeiouAEIOU";
        String resultado = "";
        for(char c: text.toCharArray()){
            if(vocales.indexOf(c)!=-1){
                resultado+=c;
            }
        }
        return resultado;
    }

    public static String vocales2 (String text){
        if(text.equals("")){
            return "";
        }
        String vocales = "aeiouAEIOU";
        char head = text.charAt(0);
        String tail = text.substring(1);

        if(vocales.indexOf(head)!=-1){
            return head+vocales2(tail);
        }else {
            return vocales2(tail);
        }
    }

    public static String vocales3 (String text){
        String vocales = "aeiouAEIOU";
        return text.chars()
                //Se filtran los caracteres que son vocales
                .filter(c->vocales.indexOf(c)!=-1)
                //Se crea un stream de caracteres convertidos a String
                .mapToObj(Character::toString)
                //Se unen los caracteres
                .collect(Collectors.joining());
    }

    public static String vocales4 (String text){
        String vocales = "aeiouAEIOU";
        return text.chars()
                //Se crea un stream de caracteres convertidos a String
                .mapToObj(Character::toString)
                //Comenzamos en vacío, acumularemos los c en acum, si c existe en vocales, acum+c, si no, se queda acum tal y como está
                .reduce("",(acum,c)->vocales.contains(c)?acum+c:acum);
    }
}
