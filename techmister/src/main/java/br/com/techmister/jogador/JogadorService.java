package br.com.techmister.jogador;

import br.com.techmister.exception.RecursoNaoEncontradoException;
import br.com.techmister.exception.RegraDeNegocioException;
import br.com.techmister.jogador.dto.JogadorRequest;
import br.com.techmister.jogador.dto.JogadorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JogadorService {

    private final JogadorRepository jogadorRepository;

    // ---------- RF-002: Cadastrar Jogador ----------
    @Transactional
    public JogadorResponse cadastrar(JogadorRequest req) {
        // CN02 do RF-002: documento duplicado
        jogadorRepository.findByDocumento(req.documento())
                .ifPresent(j -> {
                    throw new RegraDeNegocioException(
                            "Já existe um jogador cadastrado com este documento.");
                });

        Jogador novo = Jogador.builder()
                .nome(req.nome())
                .documento(req.documento())
                .posicao(req.posicao())
                .alturaCm(req.alturaCm())
                .pesoKg(req.pesoKg())
                .dataNascimento(req.dataNascimento())
                .ativo(true)
                .build();

        Jogador salvo = jogadorRepository.save(novo);
        return JogadorResponse.de(salvo);
    }

    // ---------- RF-003: Alterar Jogador ----------
    @Transactional
    public JogadorResponse alterar(Long id, JogadorRequest req) {
        Jogador jogador = buscarOuFalhar(id);

        // CN02 do RF-003: documento já pertence a outro jogador
        jogadorRepository.findByDocumentoAndIdNot(req.documento(), id)
                .ifPresent(j -> {
                    throw new RegraDeNegocioException(
                            "Já existe outro jogador cadastrado com este documento.");
                });

        jogador.setNome(req.nome());
        jogador.setDocumento(req.documento());
        jogador.setPosicao(req.posicao());
        jogador.setAlturaCm(req.alturaCm());
        jogador.setPesoKg(req.pesoKg());
        jogador.setDataNascimento(req.dataNascimento());

        return JogadorResponse.de(jogadorRepository.save(jogador));
    }

    // ---------- RF-004: Remover Jogador ----------
    @Transactional
    public void remover(Long id) {
        Jogador jogador = buscarOuFalhar(id);

        // CN02 do RF-004: jogador com vínculo (treinos, comportamentos, desempenhos).
        // Depois de implementar TreinoRepository, ComportamentoRepository e
        // DesempenhoRepository, descomente o bloco abaixo:
        //
        // boolean temVinculo = treinoRepository.existsByJogadorId(id)
        //         || comportamentoRepository.existsByJogadorId(id)
        //         || desempenhoRepository.existsByJogadorId(id);
        //
        // if (temVinculo) {
        //     throw new RegraDeNegocioException(
        //         "Este jogador não pode ser removido pois possui histórico de treinos/partidas. Deseja inativá-lo?");
        // }

        jogadorRepository.delete(jogador);
    }

    // ---------- RF-005: Listar Jogadores ----------
    @Transactional(readOnly = true)
    public List<JogadorResponse> listar() {
        return jogadorRepository.findAll().stream()
                .map(JogadorResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public JogadorResponse buscar(Long id) {
        return JogadorResponse.de(buscarOuFalhar(id));
    }

    // ---------- helper ----------
    private Jogador buscarOuFalhar(Long id) {
        return jogadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Jogador com id " + id + " não encontrado."));
    }
}
