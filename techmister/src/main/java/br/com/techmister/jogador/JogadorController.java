package br.com.techmister.jogador;

import br.com.techmister.jogador.dto.JogadorRequest;
import br.com.techmister.jogador.dto.JogadorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/jogadores")
@RequiredArgsConstructor
@Tag(name = "Jogadores", description = "CRUD do elenco (RFs 002 a 005)")
public class JogadorController {

    private final JogadorService jogadorService;

    @PostMapping
    @Operation(summary = "Cadastra um novo jogador (RF-002)")
    public ResponseEntity<JogadorResponse> cadastrar(@Valid @RequestBody JogadorRequest request,
                                                     UriComponentsBuilder uriBuilder) {
        JogadorResponse criado = jogadorService.cadastrar(request);
        URI location = uriBuilder.path("/jogadores/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera os dados de um jogador (RF-003)")
    public ResponseEntity<JogadorResponse> alterar(@PathVariable Long id,
                                                   @Valid @RequestBody JogadorRequest request) {
        return ResponseEntity.ok(jogadorService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um jogador (RF-004)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        jogadorService.remover(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    @Operation(summary = "Lista todos os jogadores do elenco (RF-005)")
    public ResponseEntity<List<JogadorResponse>> listar() {
        return ResponseEntity.ok(jogadorService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um jogador pelo identificador")
    public ResponseEntity<JogadorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(jogadorService.buscar(id));
    }
}
