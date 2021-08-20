package br.com.jonathankbp.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);
        Mockito.when(cal.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

        System.out.println(cal.somar(1, 152));
    }

}
