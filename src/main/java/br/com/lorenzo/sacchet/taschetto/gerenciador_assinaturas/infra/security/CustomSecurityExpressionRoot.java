package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.infra.security;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.MetodoPagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Pagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.AssinaturaRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.MetodoPagamentoRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("securityService")
public class CustomSecurityExpressionRoot {

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private MetodoPagamentoRepository metodoPagamentoRepository;

    public boolean checarPosseAssinatura(Long idAssinatura) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        if (securityHelper.isAdmin(usuarioLogado)) {
            return true;
        }

        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));

        return assinatura.getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario());
    }

    public boolean checarAcessoUsuario(Long idUsuarioAlvo) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        return usuarioLogado.getIdUsuario().equals(idUsuarioAlvo) || securityHelper.isAdmin(usuarioLogado);
    }

    public boolean checarPossePagamento(Long idPagamento) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        if (securityHelper.isAdmin(usuarioLogado)) {
            return true;
        }

        Pagamento pagamento = pagamentoRepository.findById(idPagamento)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento", idPagamento));

        if (pagamento.getAssinatura() == null || pagamento.getAssinatura().getUsuario() == null) {
            return false;
        }

        return pagamento.getAssinatura().getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario());
    }

    public boolean checarPosseMetodoPagamento(Long idMetodoPagamento) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        if (securityHelper.isAdmin(usuarioLogado)) {
            return true;
        }

        MetodoPagamento metodoPagamento = metodoPagamentoRepository.findById(idMetodoPagamento)
                .orElseThrow(() -> new EntityNotFoundException("Método de pagamento", idMetodoPagamento));

        if (metodoPagamento.getUsuario() == null) {
            return false;
        }

        return metodoPagamento.getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario());
    }


}
