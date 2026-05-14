package br.com.techmister.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    /** Carimbo de quando o erro ocorreu. */
    private LocalDateTime timestamp;

    /** Código HTTP. */
    private int status;

    /** Identificador curto do tipo de erro */
    private String erro;

    /** Mensagem legível para o usuário. */
    private String mensagem;

    /** Caminho da requisição que falhou. */
    private String path;

    /** Lista de erros de validação por campo, quando aplicável. */
    private List<CampoInvalido> camposInvalidos;

    @Getter
    @AllArgsConstructor
    public static class CampoInvalido {
        private String campo;
        private String mensagem;
    }
}
