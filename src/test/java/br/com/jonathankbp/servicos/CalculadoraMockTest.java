package br.com.jonathankbp.servicos;

import br.com.jonathankbp.entidades.Locacao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();
        Mockito.when(calcSpy.somar(1, 3)).thenReturn(8);
        Mockito.doNothing().when(calcMock).imprime();

        System.out.println("Mock:" + calcMock.somar(1,2));
        System.out.println("Spy:" + calcSpy.somar(1,2));

        System.out.println("Mock");
        calcMock.imprime();
        System.out.println("Spy");
        calcSpy.imprime();
    }

    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(cal.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);


        Assert.assertEquals(5, cal.somar(1, 152));
        System.out.println(argCapt.getAllValues());
    }

}
