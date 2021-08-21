package br.com.jonathankbp.servicos;

import br.com.jonathankbp.Exception.FilmeSemEstoqueException;
import br.com.jonathankbp.Exception.LocadoraException;
import br.com.jonathankbp.entidades.Filme;
import br.com.jonathankbp.entidades.Locacao;
import br.com.jonathankbp.entidades.Usuario;
import br.com.jonathankbp.utils.DataUtils;
import dao.LocacaoDAO;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.com.jonathankbp.builders.FilmeBuilder.umFilme;
import static br.com.jonathankbp.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.jonathankbp.builders.LocacaoBuilder.umLocacao;
import static br.com.jonathankbp.builders.UsuarioBuilder.umUsuario;
import static br.com.jonathankbp.matchers.MatchersProprios.*;
import static br.com.jonathankbp.utils.DataUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest {

    @InjectMocks
    private LocacaoService service;

    @Mock
    private SPCService spc;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private EmailService email;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void DeveAlugarFilme() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(20, 8, 2021));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificação
        error.checkThat(locacao.getValor(), is(5.0));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(20, 8, 2021)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(21, 8, 2021)), is(true));
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
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme>  filmes = Arrays.asList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withAnyArguments().thenReturn(DataUtils.obterData(21, 8, 2021));

        //acao
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //vericacao
        boolean ehSegunda = verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
    }

   @Test
    public void naoDeveAlugarFilmeparaNegativadoSPC() throws Exception {
        //Cenario
       Usuario usuario = umUsuario().agora();
       List<Filme> filmes = Arrays.asList(umFilme().agora());

       when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

       //acao
       try {
           service.alugarFilme(usuario, filmes);
       //verificacao
           Assert.fail();
       } catch (LocadoraException e) {
           assertThat(e.getMessage(), is("Usuário Negativado"));
       }

       verify(spc).possuiNegativacao(usuario);
   }

   @Test
    public void deveEnviarEmailLocacoesAtrasadas(){
        //Cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao().atrasado().comUsuario(usuario).agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora());
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

       //acao
       service.notificarAtraso();

       //verificacao
       verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
       verify(email).notificarAtraso(usuario);
       verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
       verify(email, Mockito.never()).notificarAtraso(usuario2);
       Mockito.verifyNoMoreInteractions(email);
   }

   @Test
    public void deveTratarErroNoSPC() throws Exception {
       //Cenario
       Usuario usuario = umUsuario().agora();
       List<Filme> filmes = Arrays.asList(umFilme().agora());

       when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));

       //Verificacao
       exception.expect(LocadoraException.class);
       exception.expectMessage("Problema com SPC, tente novamente");

       //Acao
       service.alugarFilme(usuario, filmes);
   }

   @Test
    public void deveProrrogarUmaLocacao(){
        //Cenario
       Locacao locacao = umLocacao().agora();

       //acao
       service.prorrogarLocacao(locacao, 3);

       //Verificacao
       ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
       Mockito.verify(dao).salvar(argCapt.capture());
       Locacao locacaoRetornado = argCapt.getValue();

       error.checkThat(locacaoRetornado.getValor(), is(12.0));
       error.checkThat(locacaoRetornado.getDataLocacao(), ehHoje());
       error.checkThat(locacaoRetornado.getDataRetorno(), ehHojeComDiferencaDias(3));
   }
}
