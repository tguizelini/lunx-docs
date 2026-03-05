public class PrimitiveTypes {
    public static void main(String[] args) {
        /**
         * Dados primitivos - int, double, float, char, boolean, short.
         * Objetivo da aula: Criar um ninja - Naruto
         */

        int idade  = 16;
        double altura = 1.65;
        char inicial = 'N';
        boolean vivoOuMorto = true;

        //int saldoBancario = 999999L; // valor máximo: 2 BI 147 M
        Long saldoBancario = 2147483648L; // valor máximo: 9 TRI 223 BI

        System.out.println("DadosPrimitivos::saldoBancario: " + saldoBancario);
    }
}
