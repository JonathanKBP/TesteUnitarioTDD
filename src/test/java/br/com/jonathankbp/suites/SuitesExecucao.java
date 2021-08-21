package br.com.jonathankbp.suites;

import br.com.jonathankbp.servicos.Calculadora;
import br.com.jonathankbp.servicos.CalculadoraTest;
import br.com.jonathankbp.servicos.CalculoValorLocacaoTest;
import br.com.jonathankbp.servicos.LocacaoServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//@RunWith(Suite.class)
@Suite.SuiteClasses({
//        CalculadoraTest.class,
        CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuitesExecucao {
}
