package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	// gera uma requisição fazendo o papel do insomnia

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {

		usuarioRepository.deleteAll();
	}

	@Test // indica q é um teste
	@Order(1) // indica que é o primeiro teste a ser executado
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {

		// cria o json do insomnia //atribui ao json o usuario
		HttpEntity<Usuario> corpoDaRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo Antunes",
				"paulo_antunes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

		// cria a ação POST no endereço usuarios/cadastrar recebendo a requisição
		ResponseEntity<Usuario> corpoDaResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoDaRequisicao, Usuario.class);

		// o codigo da resposta é created?
		assertEquals(HttpStatus.CREATED, corpoDaResposta.getStatusCode());

		// o corpo da requisição é igual o da resposta?
		assertEquals(corpoDaRequisicao.getBody().getNome(), corpoDaResposta.getBody().getNome());
		assertEquals(corpoDaRequisicao.getBody().getUsuario(), corpoDaResposta.getBody().getUsuario());
	}

	@Test
	@Order(2)
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278",
				"https://i.imgur.com/T12NIp9.jpg"));

		HttpEntity<Usuario> corpoDaRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva",
				"maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

		ResponseEntity<Usuario> corpoDaResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoDaRequisicao, Usuario.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoDaResposta.getStatusCode());
	}

	@Test
	@Order(3)
	@DisplayName("Alterar um Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCreate = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews",
				"juliana_andrews@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg"));

		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(), "Juliana Andrews Ramos",
				"juliana_ramos@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg");

		HttpEntity<Usuario> corpoDaRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

		ResponseEntity<Usuario> corpoDaResposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoDaRequisicao, Usuario.class);

		assertEquals(HttpStatus.OK, corpoDaResposta.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), corpoDaResposta.getBody().getNome());
		assertEquals(usuarioUpdate.getUsuario(), corpoDaResposta.getBody().getUsuario());
	}

	@Test
	@Order(4)
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123",
				"https://i.imgur.com/5M2p5Wb.jpg"));

		usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123",
				"https://i.imgur.com/Sk5SjWE.jpg"));

		ResponseEntity<String> corpoDaResposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		// get n envia corpo, por isso é null

		assertEquals(HttpStatus.OK, corpoDaResposta.getStatusCode());
	}

	// teste do findById 200.OK
	@Test
	@Order(5)
	@DisplayName("Listar Usuário por ID")
	public void deveMostrarUsuarioDoId() {

		Optional<Usuario> usuario1 = usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches2",
				"sabrina_sanches2@email.com.br", "sabrina2123", "https://i.imgur.com/25M2p5Wb.jpg"));

		ResponseEntity<String> corpoDaResposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuarios/" + usuario1.get().getId(), HttpMethod.GET, null, String.class);
		// get n envia corpo, por isso é null

		assertEquals(HttpStatus.OK, corpoDaResposta.getStatusCode());
	}

	// teste do logar (precisa criar metodo construtor no usuario login) 200.OK
	// (enviar print)
	@Test
	@Order(6)
	@DisplayName("Realizar Login")
	public void deveFazerLogin() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews2",
				"juliana_andrews2@email.com.br", "juliana2123", "https://i.imgur.com/2yDRVeK7.jpg"));

		HttpEntity<UsuarioLogin> corpoDaRequisicao = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L,"",
				"juliana_andrews2@email.com.br", "juliana2123", ""));

		ResponseEntity<UsuarioLogin> corpoDaResposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuarios/logar", HttpMethod.POST, corpoDaRequisicao, UsuarioLogin.class);

		assertEquals(HttpStatus.OK, corpoDaResposta.getStatusCode());
	}
	
	
}
