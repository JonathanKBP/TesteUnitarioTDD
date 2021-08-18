package br.com.jonathankbp.servicos;

import br.com.jonathankbp.Exception.FilmeSemEstoqueException;
import br.com.jonathankbp.Exception.LocadoraException;
import br.com.jonathankbp.entidades.Filme;
import br.com.jonathankbp.entidades.Locacao;
import br.com.jonathankbp.entidades.Usuario;
import br.com.jonathankbp.utils.DataUtils;
import buildermaster.BuilderMaster;
import dao.LocacaoDAO;
import dao.LocacaoDaoFake;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.com.jonathankbp.builders.FilmeBuilder.umFilme;
import static br.com.jonathankbp.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.jonathankbp.builders.UsuarioBuilder.umUsuario;
import static br.com.jonathankbp.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    private LocacaoService service;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
        LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
        service.setLocacaoDAO(dao);
    }

    @Test
    public void DeveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificação
        error.checkThat(locacao.getValor(), is(5.0));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarfilmeSemEstoque() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

        //acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //acao

        try {
            service.alugarFilme(null, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }

    }

    @Test
    public void naoDeveAlugarSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        //acao
        service.alugarFilme(usuario, null);


    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme>  filmes = Arrays.asList(umFilme().agora());

        //acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //vericacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    public static void main(String[] args) {
        new BuilderMaster().gerarCodigoClasse(Locacao.class);
    }

//  Opções para forma elegante TestLocacao_filmeSemEstoque
//    @Test
//    public void TestLocacao_filmeSemEstoque_2() {
//        //cenario
//        LocacaoService service = new LocacaoService();
//        Usuario usuario = new Usuario("Usuario 1");
//        Filme filme = new Filme("Filme 1", 0, 5.0);
//
//        try {
//            //acao
//            service.alugarFilme(usuario, filme);
//            Assert.fail("Deveria ter lancado uma excecao");
//        } catch (Exception e) {
//            assertThat(e.getMessage(), is("Filme sem estoque"));
//        }
//    }
//
//    @Test
//    public void TestLocacao_filmeSemEstoque_3() throws Exception {
//        //cenario
//        LocacaoService service = new LocacaoService();
//        Usuario usuario = new Usuario("Usuario 1");
//        Filme filme = new Filme("Filme 1", 0, 5.0);
//
//        exception.expect(Exception.class);
//        exception.expectMessage("Filme sem estoque");
//
//        //acao
//        service.alugarFilme(usuario, filme);
//    }
}
