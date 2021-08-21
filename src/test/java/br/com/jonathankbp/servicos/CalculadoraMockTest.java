package br.com.jonathankbp.servicos;

import br.com.jonathankbp.entidades.Locacao;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(cal.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);


        Assert.assertEquals(5, cal.somar(1, 152));
        System.out.println(argCapt.getAllValues());
    }

}
