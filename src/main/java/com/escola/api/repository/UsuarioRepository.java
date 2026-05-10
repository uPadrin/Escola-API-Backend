package com.escola.api.repository;

import com.escola.api.entity.Usuario;
import com.escola.api.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<Usuario> findByRole(Role role, Pageable pageable);

    Page<Usuario> findByRoleAndAtivo(Role role, boolean ativo, Pageable pageable);
}
