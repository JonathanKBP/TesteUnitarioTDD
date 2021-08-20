package br.com.jonathankbp.servicos;

import br.com.jonathankbp.entidades.Usuario;

public interface EmailService {

    public void notificarAtraso(Usuario usuario);
}
