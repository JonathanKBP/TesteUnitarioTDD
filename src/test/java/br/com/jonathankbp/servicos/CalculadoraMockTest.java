package br.com.jonathankbp.servicos;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);
        Mockito.when(cal.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

        Assert.assertEquals(5, cal.somar(1, 152));
    }

}
