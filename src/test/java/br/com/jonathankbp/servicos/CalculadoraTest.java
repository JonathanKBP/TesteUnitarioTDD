package br.com.jonathankbp.servicos;

import br.com.jonathankbp.Exception.NaoPodeDividirPorZeroException;
import br.com.jonathankbp.runners.ParallelRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ParallelRunner.class)
public class CalculadoraTest {

    private Calculadora calc;

    @Before
    public void setup(){
        calc = new Calculadora();
    }

    @Test
    public void deveSomarDoisValores(){
        //Cenario
        int a = 5;
        int b = 3;

        //ação
        int resultado = calc.somar(a, b);

        //Verificação
        Assert.assertEquals(8, resultado);
    }

    @Test
    public void daveSubtrairDoisValores() {
        // Cenario
        int a = 8;
        int b = 5;

        //ação
        int resultado = calc.subtrair(a, b);

        //Verificação
        Assert.assertEquals(3, resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException{
        //cenario
        int a = 6;
        int b = 3;

        //acao
        int resultado = calc.divide(a, b);

        //verificacao
        Assert.assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException{
        int a = 10;
        int b = 0;

        calc.divide(a, b);
    }
}
