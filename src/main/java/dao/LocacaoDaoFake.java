package dao;

import br.com.jonathankbp.entidades.Locacao;

import java.util.List;

public class LocacaoDaoFake implements LocacaoDAO{

    public void salvar(Locacao locacao) {

    }

    @Override
    public List<Locacao> obterLocacoesPendentes() {
        return null;
    }
}
