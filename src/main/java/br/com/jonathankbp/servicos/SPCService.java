package br.com.jonathankbp.servicos;

import br.com.jonathankbp.entidades.Usuario;

public interface SPCService {

    public boolean possuiNegativacao(Usuario usuario) throws Exception;
}
