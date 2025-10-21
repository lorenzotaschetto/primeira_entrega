package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.infra.security;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;

@Component
public class SecurityHelper {


    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            throw new AccessDeniedException("Acesso negado. Usuário não autenticado ou tipo de principal inválido.");
        }

        return (Usuario) authentication.getPrincipal();
    }

    public boolean isAdmin(Usuario usuario) {
        return usuario.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}