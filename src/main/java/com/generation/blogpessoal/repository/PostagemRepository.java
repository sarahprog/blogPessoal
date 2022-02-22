package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.generation.blogpessoal.model.Postagem;

@Repository
public interface PostagemRepository extends JpaRepository <Postagem, Long>{  /* Long chave primária */

	List <Postagem> findAllByTituloContainingIgnoreCase(String titulo);
	
	/* consulta SQL -- select * from tb_postagens where titulo like "%titulo%"; */
}
