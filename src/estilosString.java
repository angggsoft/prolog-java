import java.util.stream.Collectors;

public class estilosString {
    public static void main(String[] args) {
        System.out.println(vocalesImperativo("pepepecas"));
        System.out.println(vocalesRecursivo("pepepecas"));
    }

    /* ==========================================
     * 1. ESTILO IMPERATIVO
     * Enfoque tradicional. Le decimos a la máquina exactamente CÓMO hacer cada paso.
     * Es altamente eficiente en rendimiento pero más propenso a errores humanos por el manejo del estado.
     * ========================================== */
    public static String vocalesImperativo(String text){
        // StringBuilder es crucial aquí. A diferencia de concatenar Strings con '+',
        // StringBuilder no crea un nuevo objeto en memoria en cada iteración, optimizando el rendimiento.
        StringBuilder result = new StringBuilder();
        // Definimos nuestro universo de búsqueda (minúsculas y mayúsculas).
        String vocales = "aeiouAEIOU";

        // text.toCharArray() convierte el String en un primitivo char[] para poder iterarlo secuencialmente.
        for(char c : text.toCharArray()){
            // indexOf busca el caracter actual en nuestra cadena de vocales.
            // Si devuelve algo distinto a -1, significa que encontró una coincidencia (es una vocal).
            if(vocales.indexOf(c) != -1){
                // Mutamos el estado de nuestro objeto result, agregando la vocal encontrada.
                result.append(c);
            }
        }
        // Transformamos nuestro constructor de cadenas mutable de vuelta a un String inmutable.
        return result.toString();
    }

    /* ==========================================
     * 2. ESTILO RECURSIVO
     * Enfoque matemático/divide y vencerás. Una función se llama a sí misma hasta cumplir una condición.
     * ========================================== */
    public static String vocalesRecursivo (String text){
        // CASO BASE: Condición de corte obligatoria para evitar un StackOverflowError (bucle infinito).
        if(text.isEmpty()){
            return "";
        }

        // EXTRACCIÓN: Tomamos únicamente el primer caracter del String actual.
        char cabeza = text.charAt(0);
        // REDUCCIÓN DEL PROBLEMA: Creamos un nuevo String excluyendo el primer caracter.
        String cola = text.substring(1);
        String vocales = "aeiouAEIOU";

        // LÓGICA DE DECISIÓN: Evaluamos si el caracter extraído (cabeza) es una vocal.
        if(vocales.indexOf(cabeza) != -1){
            // Si es vocal, la conservamos y la concatenamos con el resultado de procesar el resto de la cadena.
            return cabeza + vocalesRecursivo(cola);
        } else {
            // Si es consonante, la ignoramos y simplemente pasamos a procesar el resto de la cadena.
            return vocalesRecursivo(cola);
        }
    }

    /* ==========================================
     * 3. ESTILO DECLARATIVO (STREAMS)
     * Introducido en Java 8. Declaramos QUÉ queremos obtener, creando un pipeline (tubería) de operaciones.
     * ========================================== */
    public String vocalesStream (String text){
        String vocales = "aeiouAEIOU";

        // text.chars() genera un IntStream (flujo de enteros que representan los valores ASCII/Unicode de los caracteres).
        return text.chars()
                // FILTER: Operación intermedia. Deja pasar SOLO los caracteres que cumplen la condición (ser vocal).
                .filter(c -> vocales.indexOf(c) != -1)
                // MAP: Operación intermedia. Transforma el valor. Castea el entero (IntStream) a char, y luego a String.
                .mapToObj(c -> String.valueOf((char) c))
                // COLLECT: Operación terminal. Consume el flujo de Strings y los une en uno solo sin separadores.
                .collect(Collectors.joining());
    }

    /* ==========================================
     * 4. ESTILO FUNCIONAL (REDUCE)
     * Utiliza el concepto matemático de "reducción" o "plegado".
     * Transforma una colección de elementos en un único resultado aplicando una función acumulativa.
     * ========================================== */
    public String extraerVocalesReduce(String texto) {
        String vocales = "aeiouAEIOU";

        return texto.chars()
                // Convertimos nuevamente el IntStream a un Stream de Strings.
                .mapToObj(c -> String.valueOf((char) c))
                // REDUCE: Toma dos parámetros.
                // 1. Valor inicial del acumulador (un String vacío "").
                // 2. Una función BiFunction (lambda) que dicta cómo combinar el acumulador con el siguiente elemento.
                .reduce("", (acumulador, caracter) ->
                        // Operador ternario (Condición ? Si_Es_Verdad : Si_Es_Falso)
                        // Si las vocales contienen el caracter actual, lo concatenamos al acumulador.
                        // Si no, devolvemos el acumulador intacto para el siguiente ciclo.
                        vocales.contains(caracter) ? acumulador + caracter : acumulador
                );
    }
}